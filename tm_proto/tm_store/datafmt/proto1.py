import struct

import pyarrow as pa
import pyarrow.json as json
import pyarrow.parquet as pq
import pandas as pd
import struct
import awscrt.checksums

metadata = pa.field("metadata", pa.map_(pa.string(), pa.string()))

base_struct = pa.struct(
    [
        pa.field("parent_uuid", pa.string()),
        pa.field("ckey", pa.string()),
        pa.field("optional", pa.string(), nullable=True),
    ]
)

tm_schema = pa.schema(
    [
        pa.field("base", base_struct),
        pa.field("metadata_map", pa.map_(pa.string(), pa.string())),
    ]
)

def main():
    print(tm_schema)

    data = {
        "base": {
            "parent_uuid": "123e4567-e89b-12d3-a456-426614171111",
            "ckey": "ckey1"
        },
        "metadata_map": {"key1": "value1", "key2": "value2"}
    }

    data2 = {
        "base": {
            "parent_uuid": "123e4567-e89b-12d3-a456-426614172222",
            "ckey": "ckey1"
        },
        "metadata_map": {"key1": "value3", "key2": "value4"}
    }
    
    df = pd.DataFrame([data, data2])

    json_str = df.to_json(orient="records", indent=2)
    print(f"Hello from datafmt!{json_str}")
    
if __name__ == "__main__":
    main()
