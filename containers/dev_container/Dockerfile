# Stage 1: Build/Development Environment
FROM registry.access.redhat.com/ubi9/ubi AS dev-base

# Install packages using dnf (full package manager)

ARG GIT_SHA
ENV VERSION=$GIT_SHA
ENV GO_VERSION=1.26.3

# Install Python 3.11 and Go
# UBI 9 provides standard packages for these through dnf
RUN <<EOF

dnf -y update && \
dnf -y install git tar gzip unzip&& \
dnf clean all

curl -LsSf https://astral.sh/uv/install.sh | sh && \
ln -s /root/.local/bin/uv /usr/local/bin/uv

uv python install 3.14

curl -LO https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-arm64.zip && \
    mkdir -p /usr/local/sam && \
    unzip aws-sam-cli-linux-arm64.zip -d /usr/local/sam && \
    /usr/local/sam/install.sh && \
    rm aws-sam-cli-linux-arm64.zip

# FROM ://redhat.com

# specifies macos arch - arm64 instead of amd64
curl -LO https://go.dev/dl/go${GO_VERSION}.linux-arm64.tar.gz && \
    tar -C /usr/local -xzf go${GO_VERSION}.linux-arm64.tar.gz && \
    rm go${GO_VERSION}.linux-arm64.tar.gz

EOF

ENV PATH=$PATH:/usr/local/go/bin:/usr/local/sam/dist


# Set up your working directory
WORKDIR /app

# (Optional) Copy your requirements and install dependencies
# COPY requirements.txt .
# RUN pip3 install --no-cache-dir -r requirements.txt

# (Optional) Set up Go environment
# ENV GOPATH=/go
# ENV PATH=$GOPATH/bin:/usr/local/go/bin:$PATH
