# container basic setup

base image

`container run -it --name rhel9-ubi registry.access.redhat.com/ubi9/ubi bash`

basic env

python
go
aws-sam


## build instructions

container build -t dev-image-1:$(git rev-parse --short HEAD) --build-arg GIT_SHA=$(git rev-parse --short HEAD) .

## run instructions

container run -v /Users/edc/workspace/cuddly-chainsaw/tm_proto:/app -it dev-image-1:bcb1ecd /bin/bash

