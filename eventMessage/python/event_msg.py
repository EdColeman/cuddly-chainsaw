import uuid_utils as uuid

class EventMessage:

    version = "1.0.0"
    def __init__(self, event_classification, system_id, event_type):
        self.eventClassification = event_classification
        self.eventId = str(uuid.uuid7())
        self.systemId = system_id
        self.eventType = event_type
        self.eventId1 = None
        self.eventId2 = None
        self.sequenceNumber = None
        self.timestamp = None
        self.info = []

    def __json__(self):
        msg = {
            "version": self.version,
            "eventClassification": self.eventClassification,
            "eventId": self.eventId,
            "systemId": self.systemId,
            "eventType": self.eventType,
            "eventId1": self.eventId1,
            "eventId2": self.eventId2,
            "sequenceNumber": self.sequenceNumber,
            "timestamp": self.timestamp,
            "info": self.info }

        return {k: v for k, v in msg.items() if v is not None and v != "" and v != [] and v != {}}
    