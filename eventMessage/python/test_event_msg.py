import json
from event_msg import EventMessage
from event_msg import MsgEncoder

from jsonschema import validate

schema_name = '../schema/event_message.schema.json'

def test_min_message():
    """test minimum populate files"""
    schema = load_schema()

    msg1 = EventMessage("U","system1", "test")
    jstr = json.dumps(msg1, cls=MsgEncoder)
    print(f"DUMP: {jstr}")

    validate(instance=json.loads(jstr), schema=schema)

def test_man_message():
    schema = load_schema()

    msg1 = EventMessage("U","system1", "test")
    msg1.add_info("k1", "v1")
    msg1.add_info("k2", "v2")

    jstr = json.dumps(msg1, cls=MsgEncoder)
    print(f"DUMP: {jstr}")

    validate(instance=json.loads(jstr), schema=schema)

def test_info_message():
    schema = load_schema()
    msg1 = EventMessage("U","system1", "test")
    msg1.add_info("k1", "v1")
    msg1.add_info("k2", "v2")

    jstr = json.dumps(msg1, cls=MsgEncoder)
    print(f"DUMP: {jstr}")

    validate(instance=json.loads(jstr), schema=schema)

def load_schema():
    try:
        with open(schema_name, 'r') as f:
            return json.load(f)

    except FileNotFoundError:
        print(f"Error: inputs json file not found: {schema_name}")
        assert 1 == 2
