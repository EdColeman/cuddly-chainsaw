# Stage 1: Build/Development Environment
FROM registry.access.redhat.com/ubi9/ubi AS dev-base

# Install packages using dnf (full package manager)

ARG GIT_SHA
ENV VERSION=$GIT_SHA
ENV GO_VERSION=1.26.6

RUN <<EOF

dnf -y install git tar gzip unzip openssh-server jq && \
dnf -y update && \
dnf clean all

curl -LsSf https://astral.sh/uv/install.sh | sh && \
ln -s /root/.local/bin/uv /usr/local/bin/uv

ENV PATH="/root/.local/bin:$PATH"

uv python install 3.14

curl -LO https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-arm64.zip && \
    mkdir -p /usr/local/sam && \
    unzip aws-sam-cli-linux-arm64.zip -d /usr/local/sam && \
    /usr/local/sam/install.sh && \
    rm aws-sam-cli-linux-arm64.zip

# specifies macos arch - arm64 instead of amd64
curl -LO https://go.dev/dl/go${GO_VERSION}.linux-arm64.tar.gz && \
    tar -C /usr/local -xzf go${GO_VERSION}.linux-arm64.tar.gz && \
    rm go${GO_VERSION}.linux-arm64.tar.gz

mkdir -p /donut-playground 
mkdir -p /donut-data
mkdir /run/sshd

EOF
# (Optional) Set up Go environment and AWS SAM CLI environment variables
ENV GOPATH=/go
ENV PATH=$GOPATH/bin:/usr/local/go/bin::/usr/local/sam/dist:$PATH

ENV PATH=$PATH:/usr/local/go/bin:/usr/local/sam/dist


# Set up your working directory
WORKDIR /donut-playground

RUN ssh-keygen -A
RUN /usr/sbin/sshd

RUN echo "dev image built successfully with Go ${GO_VERSION} and Python 3.14"
