# Introduction

This is an example project to explore creating a web services that implements a circuit-break
pattern

## Goals:
 - basic UI using htmx

## Set-up.

Assumes running postgres instance with a circuit-breaker-test database.

To create the database:

1 - login to postgres with default superuser account

```
sudo -u postgres psql
```

2 - create a user with password (change the username and password for your own env)

```
CREATE USER cicuit_test WITH PASSWORD 'circuit-test-1-2-3';

CREATE DATABASE circuit_test_db;

GRANT ALL PRIVILEGES ON DATABASE circuit_test_db TO circuit_test;

```
log out of the psql shell abd test that you can connect:

```
 psql -U circuit_test -d circuit_test_db -h localhost -W
```