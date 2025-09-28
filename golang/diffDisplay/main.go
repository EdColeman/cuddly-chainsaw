package main

import (
	"bufio"
	differ "diffDisplay/diff-files"
	"fmt"
	"log"
	"os"
	"sort"
	"strconv"
	"strings"

	"github.com/neticdk/go-stdlib/diff"
	"github.com/neticdk/go-stdlib/diff/myers"
)

//TIP <p>To run your code, right-click the code and select <b>Run</b>.</p> <p>Alternatively, click
// the <icon src="AllIcons.Actions.Execute"/> icon in the gutter and select the <b>Run</b> menu item from here.</p>

func main() {

	leftPath := "./test_data/input1.csv"
	rightPath := "./test_data/change1.csv"

	r, ok := differ.CompareFiles(leftPath, rightPath)
	if !ok {
		log.Fatalf("Failed to compare files")
	}

	fmt.Println("file compare results", r)

	current := readLines(leftPath)
	change := readLines(rightPath)

	dr := diffResult{}
	// dr.lines = make([]string, 0, 2048)

	differ := myers.NewCustomDiffer(myers.WithContextLines(0),
		myers.WithShowLineNumbers(true),
		myers.WithFormatter(&dr))

	diffResult, _ := differ.DiffStrings(current, change)

	fmt.Println("length ", len(diffResult))
	fmt.Println("diffResult:\n", diffResult)
	fmt.Println("** DR **:\n", dr.lines)
	fmt.Println("** DR **:\n", dr.changed)
	fmt.Println("split:\n", strings.Split(diffResult, "\n"))

	fmt.Println(" --- splitting the output ---")
	for _, line := range strings.Split(diffResult, "\n") {
		fmt.Println(line)
	}
	// lines := strings.Split(diffResult, "\n")
	//for line, t := range diffResult {
	//	fmt.Printf("l: %d - %c (%d)\n", line, t, t)
	//}

}

type ChangeState int

const (
	noChange ChangeState = iota
	added
	deleted
	changed
)

type changes struct {
	state     ChangeState
	text      string
	value     string
	prevValue string
}

type diffResult struct {
	state   ChangeState
	lines   []string
	changed map[string]changes
}

func (d *diffResult) Format(edits []diff.Line, options diff.FormatOptions) string {

	d.lines = make([]string, 0, len(edits))
	d.changed = make(map[string]changes)

	for l, e := range edits {
		fmt.Println(l, e.Kind, e.Text)
		splits := strings.Split(e.Text, " ")
		fmt.Println("format splits -> ", splits)
		if len(splits) < 2 {
			continue
		}

		key := splits[0]
		value := splits[1]

		state := noChange
		switch e.Kind {
		case diff.Insert:
			state = added
		case diff.Delete:
			state = deleted
		case diff.Equal:
			state = noChange
		}
		fmt.Println("format - detected state: ", state)

		m, ok := d.changed[splits[0]]
		if !ok {
			// fmt.Println("format not found ->", key)
			d.lines = append(d.lines, key)
			d.changed[key] = changes{state, key, value, "n/a"}
		} else {
			fmt.Println("format found ->", m, "was: ", m.value, " -> ", value)
			d.changed[key] = changes{changed, key, value, m.value}
		}
	}

	fmt.Println("r->", d.lines)
	fmt.Println("r->", d.changed)

	return strconv.Itoa(len(d.lines))
}

func readLines(filePath string) []string {

	file, err := os.Open(filePath)
	if err != nil {
		log.Fatalf("Error opening file: %v", err)
	}

	defer file.Close()

	var lines []string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		text := scanner.Text()
		if text != "" {
			lines = append(lines, text)
		}
	}
	sort.Strings(lines)
	return lines
}
