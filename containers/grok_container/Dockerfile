# Stage 1: Development Environment (Apple Silicon optimized)
FROM registry.access.redhat.com/ubi9/ubi AS dev-base

ARG GIT_SHA
ARG GO_VERSION=1.26.6
ARG TARGETARCH=arm64

ENV VERSION=$GIT_SHA \
    GOPATH=/go \
    PATH="/root/.local/bin:/usr/local/go/bin:/go/bin:/usr/local/sam/dist:$PATH"

# System packages (cached layer)
RUN dnf -y install --setopt=install_weak_deps=False \
        git tar gzip unzip openssh-server jq ca-certificates sudo \
    && dnf -y update \
    && dnf clean all \
    && rm -rf /var/cache/dnf

# uv + Python 3.14
RUN curl -LsSf https://astral.sh/uv/install.sh | sh \
    && ln -sf /root/.local/bin/uv /usr/local/bin/uv \
    && uv python install 3.14

# AWS SAM CLI (arm64)
RUN curl -LO "https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-${TARGETARCH}.zip" \
    && mkdir -p /usr/local/sam \
    && unzip aws-sam-cli-linux-${TARGETARCH}.zip -d /usr/local/sam \
    && /usr/local/sam/install \
    && rm -f aws-sam-cli-linux-${TARGETARCH}.zip

# Go
RUN curl -LO "https://go.dev/dl/go${GO_VERSION}.linux-${TARGETARCH}.tar.gz" \
    && tar -C /usr/local -xzf "go${GO_VERSION}.linux-${TARGETARCH}.tar.gz" \
    && rm -f "go${GO_VERSION}.linux-${TARGETARCH}.tar.gz"

    # templ CLI (type-safe HTML components for Go)
RUN go install github.com/a-h/templ/cmd/templ@latest

# Grok Build CLI
RUN curl -fsSL https://x.ai/cli/install.sh | bash

# Directories
RUN mkdir -p /donut-playground /donut-data /run/sshd /root/.ssh \
    && chmod 700 /root/.ssh

# Generate host keys once (required for sshd)
RUN ssh-keygen -A

# Harden sshd for container use + VS Code
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config \
    && sed -i 's/#PubkeyAuthentication yes/PubkeyAuthentication yes/' /etc/ssh/sshd_config \
    && sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config \
    && sed -i 's/#PermitEmptyPasswords no/PermitEmptyPasswords no/' /etc/ssh/sshd_config \
    && echo "AllowTcpForwarding yes" >> /etc/ssh/sshd_config \
    && echo "GatewayPorts no" >> /etc/ssh/sshd_config \
    && echo "ClientAliveInterval 60" >> /etc/ssh/sshd_config \
    && echo "ClientAliveCountMax 3" >> /etc/ssh/sshd_config

WORKDIR /donut-playground

# Expose SSH port
EXPOSE 22

# Start sshd in the foreground (required so the container stays alive)
CMD ["/usr/sbin/sshd", "-D", "-e"]