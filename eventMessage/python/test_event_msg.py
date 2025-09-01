import json
from event_msg import EventMessage
from jsonschema import validate

schema_name = '../schema/event_message.schema.json'

def func(x):
    return x + 1


def test_min_message():
    msg1 = EventMessage("U","system1", "test")
    json_string = json.dumps(msg1, default=lambda o: o.__json__() if hasattr(o, '__json__') else o.__dict__)

    schema = load_schema()

    print(f"schema: {schema}")
    print(f"message: {json_string}")

    validate(instance=json.loads(json_string), schema=schema)

    print(json_string)



def load_schema():
    try:
        with open(schema_name, 'r') as f:
            global schema
            return json.load(f)

    except FileNotFoundError:
        print(f"Error: inputs json file not found: {schema_name}")
        assert 1 == 2
