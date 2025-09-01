import uuid_utils as uuid
import json
class EventMessage:

    version = "1.0.0"
    def __init__(self, event_classification, system_id, event_type):
        self.eventClassification = event_classification
        self.eventId: uuid.UUID = uuid.uuid7()
        self.systemId = system_id
        self.eventType = event_type
        self.eventId1 = None
        self.eventId2 = None
        self.sequenceNumber = None
        self.timestamp = None
        self.info = []

    def add_info(self, k: str, v: str):
        self.info.append({k,v})

class MsgEncoder(json.JSONEncoder):

    def default(self, msg):
        if isinstance(msg, EventMessage):
            r = {
                "version": msg.version,
                "eventClassification": msg.eventClassification,
                "eventId": str(msg.eventId),
                "systemId": msg.systemId,
                "eventType": msg.eventType,
                "eventId1": uuid_str(msg.eventId1),
                "eventId2": uuid_str(msg.eventId2),
                "sequenceNumber": msg.sequenceNumber,
                "timestamp": msg.timestamp,
                "info": dict(msg.info)
            }
            filtered_data = {k: v for k, v in r.items() if v is not None}

            return filtered_data
        else:
            type_name = msg.__class__.__name___
            raise TypeError("Unexpected type {0}", format(type_name))

    # def __json__(self):
    #     return json.dumps(self, default=custom_serializer, indent=4)


    def custom_serializer(obj):
        if isinstance(obj, EventMessage):
            return { "eventClassification": obj.eventClassification, "eventId": str(obj.eventId), "info": obj.info }
        raise TypeError(f"Object of type {obj.__class__.__name__} is not JSON serializable")


    #
    # def __json__(self):
    #     msg = {
    #         "version": self.version,
    #         "eventClassification": self.eventClassification,
    #         "eventId": self.eventId,
    #         "systemId": self.systemId,
    #         "eventType": self.eventType,
    #         "eventId1": self.eventId1,
    #         "eventId2": self.eventId2,
    #         "sequenceNumber": self.sequenceNumber,
    #         "timestamp": self.timestamp,
    #         "info": self.info }
    #
    #     # cleaned = {k: v for k, v in msg.items() if v is not None and v != "" and v != [] and v != {}}
    #
    #     # print(f"cleaned: {cleaned}")
    #     print(f"self: {self}")
    #
    #     return json.dumps(self)
    #     # return  json.dumps(cleaned, default=lambda o: o.__json__() if hasattr(o, '__json__') else o.__dict__)
    #
def uuid_str(id : uuid.UUID):
    if id is not None:
        return str(id)
    return None
