# Event Message Schema Test files.

These files test the event message schema and has files that are expected to pass
and files that are expected to fail.  

Files that should cause a validation are named x.error.json. It is a validation error if
the file named x.error.json do not raise a validation exception.

## Valid files

| filename        | description                      |
|:----------------|:---------------------------------|
| message1.json   | fully populated sample message   |
| minMessage.json | message with required files only |

## Invalid Files

| filename                  | description                                             |
|:--------------------------|:--------------------------------------------------------|
| invalidId.error.json      | the event id is not a UUIDv7 (it is formated as UUIDv4) |
| missingEventId.error.json | the event id is not present                             |