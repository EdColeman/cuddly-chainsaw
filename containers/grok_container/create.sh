#!/usr/bin/env bash
set -euo pipefail

# Build the Playwright test container
# - Removes any existing playwright_test images first
# - Tags: playwright_test:<short-sha> and playwright_test:latest
# - Passes GIT_SHA build arg for version stamping

IMAGE_NAME="grok-dev-image-1"
SHORT_SHA="$(git rev-parse --short HEAD)"

echo ">>> Removing previous ${IMAGE_NAME} images (if any)..."
# Remove all tags of this image (ignore errors if none exist)
container image ls --format '{{.Repository}}:{{.Tag}}' 2>/dev/null \
  | grep "^${IMAGE_NAME}:" \
  | xargs -r container image rm -f 2>/dev/null || true

echo ">>> Building ${IMAGE_NAME}:${SHORT_SHA} and ${IMAGE_NAME}:latest ..."
container build \
  -t "${IMAGE_NAME}:${SHORT_SHA}" \
  -t "${IMAGE_NAME}:latest" \
  --build-arg GIT_SHA="${SHORT_SHA}" \
  --no-cache \
  . 2>&1 | tee ./b.txt

echo ">>> Build complete."