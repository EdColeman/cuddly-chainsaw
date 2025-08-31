import json
import os
from jsonschema import validate, ValidationError


def main():
    print("Hello from python!")

    schema_name = '../schema/event_message.schema.json'
    input_dir = '../schema/test'

    unexpected_error_count = 0
    unexpected_error_files = []
    missed_error_count = 0
    missed_error_files = []

    try:
        with open(schema_name, 'r') as f:
            schema = json.load(f)

        if not os.path.exists(input_dir):
            print(f"No files found in directory {input_dir}")
            exit(-1)

        files = os.listdir(input_dir)


        print(f"files: {files}")

        for filename in files:
            input_name = os.path.join(input_dir, filename)

            print(f"filename:{input_name}")

            try:

                with open(input_name, 'r', encoding='utf-8') as file:
                    message = json.load(file)

                    print(message)

                    # validate(instance={"name": "John", "age": 30}, schema=schema)
                    validate(instance=message, schema=schema)
                    if "error" in filename:
                        missed_error_count += 1
                        missed_error_files.append(filename)

            except FileNotFoundError:
                print(f"Error: inputs json file not found: {input_name}")
                exit()
            except ValidationError:
                if not "error" in filename:
                    unexpected_error_files.append(filename)
                    unexpected_error_count += 1
            except json.JSONDecodeError:
                unexpected_error_count += 1
                print("Error: invalid schema, could not parse file")
                exit()

    except FileNotFoundError:
        print("Error: schema file not found")
        exit()

    if unexpected_error_count > 0:
        print(f"unexpected error count: {unexpected_error_count}, files: {unexpected_error_files}")

    if missed_error_count > 0:
        print(f"missed error count: {missed_error_count}, files: {missed_error_files}")

    #validate(instance={"name": "John", "age": "30"}, schema=schema)

    print("done...")

if __name__ == "__main__":
    main()
