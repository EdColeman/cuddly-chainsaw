package model

import (
	"context"
	"errors"
	"fmt"
	"strings"
	"time"
)

const timeoutThreshold = 5_000 // default 5 second timeout threshold

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
		State:       Closed,
		Description: strings.TrimSpace(description),
		Timeout:     &time.Time{},
		LastErrors:  nil,
	}
}

type EndPointFilter func(v EndPoint) bool

func ProcessState(endpoint EndPoint) (ConnState, error) {

	state := ConnState(endpoint.State)
	switch state {
	case Open:
		return Open, nil
	case Closed:
		checkTimeoutExpired(endpoint.Timeout)
		return Closed, nil
	case HalfOpen:
		return Closed, nil
	default:
		return Open, errors.New("Url `" + endpoint.Url + "` state `" + ConnStateNames[state] + "` is undefined")
	}

}

// checkTimeoutExpired returns false if the endpoint timeout is less than the threshold. Returns true if
// expired or nil.
func checkTimeoutExpired(timeout *time.Time) bool {
	if timeout == nil {
		return true
	}

	now := time.Now().UTC()

	delta := now.Sub(timeout.UTC()).Milliseconds()

	if delta > timeoutThreshold {
		fmt.Printf("timeout expired with %d milliseconds. Threshold is %d\n", delta, timeoutThreshold)
		return true
	}

	return false
}


type EndPointStore interface {
	CreateEndPoint(ctx context.Context, endPoint EndPoint) error
	CheckEndPointState(ctx context.Context, url string) (ConnState, error)
	ListEndPoints(ctx context.Context) (endpoints []EndPoint, ok bool)
	ListEndPointsFilter(ctx context.Context, filter EndPointFilter) (endpoints []EndPoint, ok bool)
}
