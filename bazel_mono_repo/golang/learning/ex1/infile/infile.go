package infile

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strings"
)

type Record struct {
	id    string
	v1    string
	state State
	v2    int
}

func Read(filename string) string {
	log.Println("opening file:", filename)

	path, err := os.Getwd()
	if err != nil {
		log.Println(err)
	}

	fmt.Println(path)

	file, err := os.Open(filename)
	if err != nil {
		log.Fatalf("Error opening file: %v", err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatalf("Error closing file: %v", err)
		}
	}(file)

	scanner := bufio.NewScanner(file)

	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		fmt.Printf("L:%d[%s]\n", len(line), line)
	}

	if err := scanner.Err(); err != nil {
		log.Fatalf("Error scanning file: %v", err)
	}

	return "a: " + filename
}

// State Define a custom type to represent the enum
type State int

// Define the enum values using iota
const (
	Unknown State = iota // 0
	State1               // 1
	State2               // 2
	State3               // 3
)

// Define a map for string to State conversion
var stateMap = map[string]State{
	"State1": State1,
	"State2": State2,
	"State3": State3,
}

// ConvertStringToState converts a string to a State enum value
func ConvertStringToState(s string) (State, error) {
	if val, ok := stateMap[s]; ok {
		return val, nil
	}
	return Unknown, fmt.Errorf("invalid color string: %s", s)
}
