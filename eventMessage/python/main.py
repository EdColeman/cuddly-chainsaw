import json
from jsonschema import validate, ValidationError, Draft202012Validator


def main():
    print("Hello from python!")

    schemaName = '../schema/event_message.schema.json'
    inputName = '../schema/message1.json'

    # schema = {
    #     "type": "object",
    #     "properties": {
    #         "name": {"type": "string"},
    #         "age": {"type": "number"},
    #     },
    #     "required": ["name"],
    # }

    try:
        with open(schemaName, 'r') as f:
            schema = json.load(f)

        with open(inputName, 'r', encoding='utf-8') as file:
            message = json.load(file)

        print(message)

        # validate(instance={"name": "John", "age": 30}, schema=schema)
        validate(instance=message, schema=schema)

    except FileNotFoundError:
        print("Error: schema file not found")
        exit()
    except json.JSONDecodeError:
        print("Error: invalid schema, could not parse file")
        exit()

    #validate(instance={"name": "John", "age": "30"}, schema=schema)

    print("done...")

if __name__ == "__main__":
    main()
