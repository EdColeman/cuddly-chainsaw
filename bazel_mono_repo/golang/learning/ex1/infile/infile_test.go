package infile

import (
	"fmt"
	"testing"
)

func TestRead(t *testing.T) {
	msg := Read("testdata/sample1.csv")
	fmt.Print("new msg: ", msg)
}
