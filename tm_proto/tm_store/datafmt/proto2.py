import struct

import pyarrow as pa
import pyarrow.json as json
import pyarrow.parquet as pq
import pandas as pd
import struct
import awscrt.checksums

edh_struct = pa.struct(
    [
        ("cstring", pa.string()),
        ("l_cat", pa.string()),
        ("a_id", pa.string()),
        ("received", pa.int64()),  # seconds scince epoch
    ]
)
# allow filtering based on group and intent. This is supplimental to other controls
access_struct = pa.struct(
    [
        ("access_group", pa.string()),
        ("intent", pa.string()),
    ]
)

payload_struct = pa.struct(
    [
        ("feature_id", pa.string()),
        ("timestamp", pa.timestamp("ms")),
        ("checksum", pa.binary(8)),
        ("record_type", pa.string()),
        ("payload_encoding", pa.string()),
        ("payload_data", pa.binary()),
    ]
)
tm_schema = pa.schema(
    [
        pa.field("parent_uuid", pa.string()),
        pa.field("record_uuid", pa.string()),
        pa.field("ckey", pa.string()),
        pa.field("role_access", access_struct),
        pa.field("edh", edh_struct),
        pa.field("metadata_map", pa.map_(pa.string(), pa.string()), nullable=True),
        pa.field("payload", payload_struct),
    ]
)

def create_sample_data():
    data = {
        "parent_uuid": "123e4567-e89b-12d3-a456-426614171111",
        "record_uuid": "123e4567-e89b-12d3-a456-426614171222",
        "ckey": "ckey1",
        "role_access": {
            "access_group": "group1",
            "intent": "test"
        },
        "edh": {
            "cstring": "example_string",
            "l_cat": "example_category",
            "a_id": "example_id",
            "received": 1700000000
        },
        "metadata_map": {"m1": "v1", "m2": "v2"},
        "payload": {
            "feature_id": "feature1",
            "timestamp": pd.Timestamp.now().timestamp() * 1000,
            "checksum": b"checksum",
            "record_type": "type1",
            "payload_encoding": "string",
            "payload_data": b"payload_data"
        }
    }

    df = pd.DataFrame([data])

    json_str = df.to_json(orient="records", indent=2)
    print(f"Sample:\n{json_str}")

def main():

    print(tm_schema)

    create_sample_data()

    df = pd.read_json("./test_data/sample1.json", orient="records")
    
    print(df)

    json_str = df.to_json(orient="records", indent=2)
    print(f"Hello from datafmt!{json_str}")

    table = pa.Table.from_pandas(df, schema=tm_schema)

    # 4. Safely output the structural table data to a Parquet file
    pq.write_table(table, './test_data/sample1.parquet')
    # parse_options = pa.json.ParseOptions(newlines_in_values=True)  
    # table = pa.json.read_json("./test_data/sample1.json", parse_options=parse_options)

    # print(table.schema)
    # print(table.to_pandas())

    
if __name__ == "__main__":
    main()
