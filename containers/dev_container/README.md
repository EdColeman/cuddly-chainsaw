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

## postgres container

container run -d --name db1 -e POSTGRES_USER=ts_user -e POSTGRES_PASSWORD=dbpass987 -e POSTGRES_DB=ts_db -p 5432:5432 postgres:18

## Using DNS

- one time container configuration
sudo container system dns create dev-domain
container system property set dns.domain dev-domain

container system stop
container system start

- create network
container network create dev-network

container run -d --name postgres-tm1 --network dev-network -e POSTGRES_USER=ts_user -e POSTGRES_PASSWORD=dbpass987 -e POSTGRES_DB=ts_db -p 5432:5432 postgres:18

postgres name
postgres-tm1.dev-domain

To start the dev container with the network
container run -v /Users/edc/workspace/cuddly-chainsaw/tm_proto:/app --network dev-network -it dev-image-1:37d29fa /bin/bash