package infile

import (
	"fmt"
	"slices"
	"testing"
)

// TestGet checks that Get returns one of the strings from fortunes.
func TestGet(t *testing.T) {
	msg := Get()
	if i := slices.Index(Lines, msg); i < 0 {
		t.Errorf("Get returned %q, not one the expected messages", msg)
	}
}

func TestRead(t *testing.T) {
	msg := Read("testdata/sample1.csv")
	fmt.Print("new msg: ", msg)
}
