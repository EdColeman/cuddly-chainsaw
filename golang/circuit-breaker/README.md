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
CREATE USER circuit_test WITH ENCRYPTED PASSWORD 'circuit-test-1-2-3' LOGIN;
CREATE DATABASE circuit_test_db OWNER circuit_test;
```
If you don't want the test user to be the owner 
```
GRANT ALL PRIVILEGES ON DATABASE circuit_test_db TO circuit_test;

```
log out of the psql shell abd test that you can connect:

```
 psql -U circuit_test -d circuit_test_db -h localhost -W
```

## Environment Variables (for testing)

DB_USERNAME=circuit_test 
DB_USER_PASS=circuit-test-1-2-3
