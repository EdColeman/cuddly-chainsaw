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
        ("record_uuid", pa.string()),
        pa.field("ckey", pa.string()),
        pa.field("role_access", access_struct),
        pa.field("edh", edh_struct),
        pa.field("metadata_map", pa.map_(pa.string(), pa.string())),
        pa.field("payload", payload_struct),
    ]
)

def create_sample_data():
    data = {
        "parent_uuid": "123e4567-e89b-12d3-a456-426614171111",
        "ckey": "ckey1",
        "edh": {
            "cstring": "some_cstring",
            "l_cat": "some_l_cat",
            "a_id": "some_a_id",
            "received": int(pd.Timestamp.now().timestamp() * 1000)  # convert to ms    
        },
        "metadata_map": {"key1": "value1", "key2": "value2"}
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
    
    # parse_options = pa.json.ParseOptions(newlines_in_values=True)  
    # table = pa.json.read_json("./test_data/sample1.json", parse_options=parse_options)

    # print(table.schema)
    # print(table.to_pandas())

    
if __name__ == "__main__":
    main()
