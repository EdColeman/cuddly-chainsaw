package sub1

import (
	"bufio"
	"fmt"
	"log"
	"math/rand"
	"os"
	"strings"
)

var Lines = []string{
	"line 1 💚",
	"line 2",
	"line 3",
}

func Get() string {
	return Lines[rand.Intn(len(Lines))]
	// return lines[0]
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
