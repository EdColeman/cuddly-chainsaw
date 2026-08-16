#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration – edit these
# ---------------------------------------------------------------------------
IMAGE="grok-dev-image-1:latest"
NETWORK="dev-network"
CPUS=5
MEMORY="4G"
PLAYGROUND_HOST="${HOME}/workspace/donut-playground"
PLAYGROUND_CONT="/donut-playground"
DATA_HOST="${HOME}/workspace/data/donut-data"
DATA_CONT="/donut-data"
SSH_KEY="${HOME}/.ssh/ai_gh_ed25519"
WORKDIR="/donut-playground"          # optional starting directory inside container
CONTAINER_NAME="donut-dev"           # optional; omit for auto-generated name

# Port for the web UI (Playwright will talk to this)
HOST_PORT=3000          # Port on the Mac / other containers
CONTAINER_PORT=3000     # Port the app listens on inside this container

# Set to 1 (or pass --dry-run) to print the final command without executing
DRY_RUN=0

# ---------------------------------------------------------------------------
# Preconditions
# ---------------------------------------------------------------------------
# Ensure the Apple container system is running
if ! container system status &>/dev/null; then
  echo "Starting container system…"
  container system start
fi

# Ensure the donut directories already exist (do not create them)
for dir in "${PLAYGROUND_HOST}" "${DATA_HOST}"; do
  if [[ ! -d "${dir}" ]]; then
    echo "Error: required directory does not exist: ${dir}" >&2
    exit 1
  fi
done

# Start a private SSH agent that contains ONLY the designated key.
# This ensures no other host identities are forwarded into the container.
# Passphrase is retrieved from (or stored into) the Apple Keychain via
# --apple-use-keychain so the script stays non-interactive after the first run.
if [[ ! -f "${SSH_KEY}" ]]; then
  echo "Error: required SSH key not found: ${SSH_KEY}" >&2
  exit 1
fi

# Basic permission check on the private key
if [[ $(stat -f %A "${SSH_KEY}" 2>/dev/null || echo "000") != "600" ]]; then
  echo "Error: SSH key must be mode 600: ${SSH_KEY}" >&2
  exit 1
fi

eval "$(ssh-agent -s)" >/dev/null
ssh-add --apple-use-keychain "${SSH_KEY}" >/dev/null

# Always kill the private agent when this script exits (success or failure)
trap 'ssh-agent -k >/dev/null 2>&1' EXIT

# Optional: ensure the network exists
if ! container network ls --quiet 2>/dev/null | grep -q "^${NETWORK}$"; then
  echo "Creating network ${NETWORK}…"
  container network create "${NETWORK}"
fi

# ---------------------------------------------------------------------------
# Argument parsing (simple)
# ---------------------------------------------------------------------------
for arg in "$@"; do
  case "$arg" in
    --dry-run) DRY_RUN=1 ;;
    *)
      echo "Unknown argument: $arg" >&2
      echo "Usage: $0 [--dry-run]" >&2
      exit 1
      ;;
  esac
done

# ---------------------------------------------------------------------------
# Launch
# ---------------------------------------------------------------------------
# --ssh forwards the current SSH_AUTH_SOCK (our private agent above).
# The private agent lives for the lifetime of this process tree; the EXIT
# trap above guarantees it is killed when the script finishes.
#
# Port is bound to 127.0.0.1 only (not 0.0.0.0) for better host isolation.

CMD=(
  container run
  --name "${CONTAINER_NAME}"
  --rm
  --interactive --tty
  --cpus "${CPUS}"
  --memory "${MEMORY}"
  --network "${NETWORK}"
  --ssh
  --volume "${PLAYGROUND_HOST}:${PLAYGROUND_CONT}"
  --volume "${DATA_HOST}:${DATA_CONT}"
  --workdir "${WORKDIR}"
  -p "127.0.0.1:${HOST_PORT}:${CONTAINER_PORT}"
  "${IMAGE}"
  /bin/bash
)

if [[ "${DRY_RUN}" -eq 1 ]]; then
  echo "Dry-run – would execute:"
  printf ' %q' "${CMD[@]}"
  echo
  exit 0
fi

exec "${CMD[@]}"
