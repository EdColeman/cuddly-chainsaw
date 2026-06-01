import struct

import awscrt.checksums
import pandas as pd
import pyarrow as pa
import pyarrow.parquet as pq
import pyarrow.json as json

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
        ("parent_uuid", pa.string()),
        ("record_uuid", pa.string()),
        ("ckey", pa.string()),
        ("role_access", pa.string()),
        ("timestamp", pa.timestamp("ms")),
        ("edh", edh_struct),
        ("payload", payload_struct),
    ]
)

def data_checksum(payload: bytes) -> bytes:
    # Placeholder for checksum calculation
    crc_int = awscrt.checksums.crc64nvme(payload) 
    return struct.pack('>Q', crc_int)

def create_primary_record(
    parent_uuid: str,
    record_uuid: str,
    ckey: str,
    role_access: str,
    timestamp: pd.Timestamp,
    edh_cstring: str,
    edh_l_cat: str,
    edh_a_id: str,
    edh_received: pd.Timestamp,
    payload_feature_id: str,
    payload_timestamp: pd.Timestamp,
    payload_record_type: str,
    payload_encoding: str,
    payload_data: bytes
) -> pa.struct:
    edh = {
        "cstring": edh_cstring,
        "l_cat": edh_l_cat,
        "a_id": edh_a_id,
        "received": int(edh_received.timestamp() * 1000),  # convert to ms
    }
    
    checksum = data_checksum(payload_data)
    
    payload = {
        "feature_id": payload_feature_id,
        "timestamp": payload_timestamp,
        "checksum": checksum,
        "record_type": payload_record_type,
        "payload_encoding": payload_encoding,
        "payload_data": payload_data
    }
    
    record = {
        "parent_uuid": parent_uuid,
        "record_uuid": record_uuid,
        "ckey": ckey,
        "role_access": role_access,
        "timestamp": timestamp,
        "edh": edh,
        "payload": payload
    }
    
    return pa.struct(record, schema=tm_schema)
    
def main():
    rec = create_primary_record(
        parent_uuid="123e4567-e89b-12d3-a456-426614174000", 
        record_uuid="123e4567-e89b-12d3-a456-426614174001",
        ckey="some_key",
        role_access="some_role",
        timestamp=pd.Timestamp.now(),
        edh_cstring="some_cstring",
        edh_l_cat="some_l_cat",
        edh_a_id="some_a_id",
        edh_received=pd.Timestamp.now(),
        payload_feature_id="some_feature_id",
        payload_timestamp=pd.Timestamp.now(),
        payload_record_type="some_record_type",
        payload_encoding="some_encoding",
        payload_data=b"some_data"
    )
    
    df = pd.DataFrame(rec.to_pydict(), index=[0])

    table = pa.Table.from_pandas(df, schema=tm_schema)
    
    # pq.write_table(table, "output.parquet")
    str = json.dumps(table.to_pylist())

    print(f"Hello from datafmt!{str}")


if __name__ == "__main__":
    main()
