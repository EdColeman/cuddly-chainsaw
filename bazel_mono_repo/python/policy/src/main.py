from dataclasses import dataclass
from datetime import datetime
from datetime import timedelta
from enum import Enum
from typing import List

@dataclass
class ReasonId:
    id : str

class ModAction(Enum):
    """ define the possible modification options """
    MODIFY = 1     # add or subtract time
    HOLD = 2       # override age-off and keep indefinitely
    DELETE = 3     # override and remove immediately
    DELETE_ON = 4  # age-off after explicit date

@dataclass
class Modifier:
    ReasonId: str
    source: str
    action: ModAction
    timestamp : datetime | timedelta
    reason: str

class RegistrationEvent:
    dataId : str
    parentId : str
    reasonIds : List[ReasonId]
    dataDateTime : datetime

