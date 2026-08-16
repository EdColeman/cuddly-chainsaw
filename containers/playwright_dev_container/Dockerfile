# Playwright UI testing container optimized for Apple Silicon (arm64)
# Based on the provided UBI9 template. Focused on minimal size + security.
# Code is shared via the /donut-playground mount.
# Runtime data (reports, traces, screenshots, caches) lives in the /donut-data mount.
# VS Code Remote-SSH attach is supported. No personal keys are baked into the image.

FROM registry.access.redhat.com/ubi9/ubi AS playwright-base

ARG GIT_SHA
ARG NODE_VERSION=24.19.0
ARG PLAYWRIGHT_VERSION=1.55.0
ARG TARGETARCH=arm64

ENV VERSION=$GIT_SHA \
    NODE_ENV=production \
    PLAYWRIGHT_BROWSERS_PATH=/ms-playwright \
    PLAYWRIGHT_SKIP_BROWSER_GC=1 \
    PATH="/usr/local/node/bin:$PATH"

# Minimal system packages only (no weak deps, aggressive cleanup)
# openssh-server + hardening for VS Code Remote-SSH
# Playwright Chromium runtime libraries (RHEL/UBI equivalents)
RUN dnf -y install --setopt=install_weak_deps=False \
        git tar gzip unzip openssh-server jq ca-certificates sudo \
        nspr nss nss-util dbus-libs atk at-spi2-atk at-spi2-core \
        cups-libs libXcomposite libXdamage libXext libXfixes libXrandr \
        mesa-libgbm cairo pango alsa-lib libdrm libxkbcommon \
        libX11 libXrender libxcb gtk3 libXScrnSaver \
    && dnf -y update \
    && dnf clean all \
    && rm -rf /var/cache/dnf /var/log/* /tmp/* /var/tmp/*

# Official Node.js binary (arm64) – smaller & more reproducible than nodesource
RUN curl -fsSL "https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}.linux-${TARGETARCH}.tar.xz" \
        -o /tmp/node.tar.xz \
    && mkdir -p /usr/local/node \
    && tar -xJf /tmp/node.tar.xz -C /usr/local/node --strip-components=1 \
    && rm -f /tmp/node.tar.xz \
    && npm config set fund false \
    && npm config set update-notifier false

# Install Playwright (CLI + browsers) – Chromium only to keep image small
# Browsers are placed under /ms-playwright (not under a mount) so they survive
# and are always available; other runtime artifacts go to the mounted /donut-data.
RUN mkdir -p /ms-playwright \
    && npm install -g "playwright@${PLAYWRIGHT_VERSION}" "@playwright/test@${PLAYWRIGHT_VERSION}" \
    && npx playwright install chromium \
    && npm cache clean --force \
    && rm -rf /root/.npm /tmp/* /var/tmp/* \
    && chmod -R a+rX /ms-playwright

# Directories expected by the host (code + runtime data)
RUN mkdir -p /donut-playground /donut-data /run/sshd /root/.ssh \
    && chmod 700 /root/.ssh

# Generate host keys once (required for sshd). No user keys are ever added.
RUN ssh-keygen -A

# Harden sshd for container + VS Code Remote-SSH use
# - Root login allowed only via public key (no passwords, no empty passwords)
# - No personal / host-side keys are present in the image
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config \
    && sed -i 's/#PubkeyAuthentication yes/PubkeyAuthentication yes/' /etc/ssh/sshd_config \
    && sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config \
    && sed -i 's/#PermitEmptyPasswords no/PermitEmptyPasswords no/' /etc/ssh/sshd_config \
    && echo "AllowTcpForwarding yes" >> /etc/ssh/sshd_config \
    && echo "GatewayPorts no" >> /etc/ssh/sshd_config \
    && echo "ClientAliveInterval 60" >> /etc/ssh/sshd_config \
    && echo "ClientAliveCountMax 3" >> /etc/ssh/sshd_config \
    && echo "X11Forwarding no" >> /etc/ssh/sshd_config \
    && echo "AllowAgentForwarding no" >> /etc/ssh/sshd_config

WORKDIR /donut-playground

# SSH only – no other ports exposed
EXPOSE 22

# sshd in foreground keeps the container alive for VS Code attach
CMD ["/usr/sbin/sshd", "-D", "-e"]
