import struct

import pyarrow as pa
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
        ("uuid", pa.string()),
        ("ckey", pa.string()),
        ("role_access", pa.string()),
        ("timestamp", pa.timestamp("ms")),
        ("edh", edh_struct),
        ("payload", payload_struct),
    ]
)

def main():
    print("Hello from datafmt!")


if __name__ == "__main__":
    main()

def data_checksum(payload: bytes) -> bytes:
    # Placeholder for checksum calculation
    crc_int = awscrt.checksums.crc64nvme(payload) 
    return struct.pack('>Q', crc_int)
