package difflines

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"sort"
	"strconv"
	"strings"

	"github.com/neticdk/go-stdlib/diff"
	"github.com/neticdk/go-stdlib/diff/myers"
)

type ChangeState int

const (
	NoChange ChangeState = iota
	Added
	Deleted
	Changed
)

func (s ChangeState) String() string {
	switch s {
	case NoChange:
		return "nochange"
	case Added:
		return "added"
	case Deleted:
		return "deleted"
	case Changed:
		return "deleted"
	default:
		return "Unknown"
	}
}

type DiffResult struct {
	numLeft    int // line count from let
	numRight   int // line count from right
	numLines   int // number of output lines
	numSkipped int // number of invalid lines skipped
	state      ChangeState
	lines      []string
	changed    map[string]Changes
}

type Changes struct {
	state     ChangeState
	text      string
	value     string
	prevValue string
}

func CompareFiles(leftPath string, rightPath string) (DiffResult, bool) {

	left, err := readLines(leftPath)
	if err != nil {
		log.Fatalf("failed to read file '%s' reason: %v", leftPath, err)
		return DiffResult{}, false
	}
	right, err := readLines(rightPath)
	if err != nil {
		log.Fatalf("failed to read file '%s' reason: %v", leftPath, err)
		return DiffResult{}, false
	}

	return compareLines(left, right)
}

func compareLines(left, right []string) (DiffResult, bool) {

	results := DiffResult{}

	results.numLeft = len(left)
	results.numRight = len(right)

	fmt.Println("Lines read from left input file:")
	for _, line := range left {
		fmt.Println(line)
	}
	fmt.Println("Lines read from right right file:")
	for _, line := range right {
		fmt.Println(line)
	}

	differ := myers.NewCustomDiffer(myers.WithContextLines(0),
		myers.WithShowLineNumbers(true),
		myers.WithFormatter(&results))

	r, _ := differ.DiffStrings(left, right)
	results.numLines, _ = strconv.Atoi(r)

	return results, true

}

func filter(diffs DiffResult) []string {
	result := make([]string, 0, len(diffs.lines))
	for _, line := range diffs.lines {
		m := diffs.changed[line]

		if m.state != Deleted {
			s := m.text + ", " + m.value
			result = append(result, s)
		}
	}
	return result
}
func (d *DiffResult) Format(edits []diff.Line, options diff.FormatOptions) string {

	d.lines = make([]string, 0, len(edits))
	d.changed = make(map[string]Changes)

	for l, e := range edits {
		fmt.Println(l, e.Kind, e.Text)
		splits := strings.Split(e.Text, ",")
		fmt.Println("format splits -> ", splits)
		if len(splits) != 2 {
			d.numSkipped++
			continue
		}

		key := strings.ReplaceAll(splits[0], ",", "")
		value := splits[1]

		state := NoChange
		switch e.Kind {
		case diff.Insert:
			state = Added
		case diff.Delete:
			state = Deleted
		case diff.Equal:
			state = NoChange
		}
		fmt.Println("format - detected state: ", state)

		m, ok := d.changed[splits[0]]
		if !ok {
			// fmt.Println("format not found ->", key)
			d.lines = append(d.lines, key)
			if state == Deleted {
				// if delete set curr, prev to the value in the line
				d.changed[key] = Changes{state, key, value, value}
			} else {
				// if changed
				d.changed[key] = Changes{state, key, value, "n/a"}
			}
		} else {
			fmt.Println("format found ->", m, "was: ", m.value, " -> ", value)
			d.changed[key] = Changes{Changed, key, value, m.value}
		}
		fmt.Println("format progress ->", m)
	}

	fmt.Println("r->", d.lines)
	fmt.Println("r->", d.changed)

	return strconv.Itoa(len(d.lines))
}

// return
func readLines(filePath string) ([]string, error) {

	file, err := os.Open(filePath)
	if err != nil {
		return nil, err
	}

	defer func() {
		closeErr := file.Close()
		if closeErr != nil && err == nil { // Only set if no other error occurred
			err = closeErr
		}
	}()

	var lines = make([]string, 0, 4096)
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		text := scanner.Text()
		if text != "" {
			lines = append(lines, text)
		}
	}
	sort.Strings(lines)
	return lines, err
}
