package model

import (
	"strings"
	"time"
)

type ConnState int

// allowed circuit breaker states
const (
	Open     ConnState = 0 // use explicit 0 value exported to database
	Closed             = 1
	HalfOpen           = 2
)

// ConnStateNames convert from ConnState enum to a string
var ConnStateNames = map[ConnState]string{
	Open:     "OPEN",
	Closed:   "CLOSED",
	HalfOpen: "HALF-OPEN",
}

// ConnStateValues convert from string to enum value
var ConnStateValues = map[string]ConnState{
	"OPEN":      Open,
	"CLOSED":    Closed,
	"HALF-OPEN": HalfOpen,
}

// Implement the fmt.Stringer interface
func (cs ConnState) String() string {
	return ConnStateNames[cs]
}

type EndPoint struct {
	Id          int64        `json:"id"`
	Url         string       `json:"url"`
	State       int          `json:"state"`
	Description string       `json:"description"`
	Timeout     *time.Time   `json:"timeout"`
	LastErrors  *[]time.Time `json:"lastErrors"`
}

func NewEndPoint(url string, description string) EndPoint {
	return EndPoint{
		Id:          0,
		Url:         strings.TrimSpace(url),
		State:       int(Closed),
		Description: strings.TrimSpace(description),
		Timeout:     &time.Time{},
		LastErrors:  nil,
	}
}

type EndPointFilter func(v EndPoint) bool
