package difflines

import (
	"bufio"
	"log/slog"
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
	NumLeft    int                // line count from let
	NumRight   int                // line count from right
	NumLines   int                // number of output Lines
	NumSkipped int                // number of invalid Lines skipped
	State      ChangeState        // the change
	Lines      []string           // the lines in edit order
	Changed    map[string]Changes // map with line changes
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
		slog.Error("failed to read file '%s' reason: %v", leftPath, err)
		return DiffResult{}, false
	}
	right, err := readLines(rightPath)
	if err != nil {
		slog.Error("failed to read file '%s' reason: %v", leftPath, err)
		return DiffResult{}, false
	}

	return compareLines(left, right)
}

func compareLines(left, right []string) (DiffResult, bool) {

	results := DiffResult{}

	results.NumLeft = len(left)
	results.NumRight = len(right)

	differ := myers.NewCustomDiffer(myers.WithContextLines(0),
		myers.WithShowLineNumbers(true),
		myers.WithFormatter(&results))

	r, _ := differ.DiffStrings(left, right)
	results.NumLines, _ = strconv.Atoi(r)

	return results, true

}

func filter(diffs DiffResult) []string {
	result := make([]string, 0, len(diffs.Lines))
	for _, line := range diffs.Lines {
		m := diffs.Changed[line]

		if m.state != Deleted {
			s := m.text + ", " + m.value
			result = append(result, s)
		}
	}
	return result
}
func (d *DiffResult) Format(edits []diff.Line, _ diff.FormatOptions) string {

	d.Lines = make([]string, 0, len(edits))
	d.Changed = make(map[string]Changes)

	for _, e := range edits {
		splits := strings.Split(e.Text, ",")
		if len(splits) != 2 {
			d.NumSkipped++
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

		m, ok := d.Changed[splits[0]]
		if !ok {
			d.Lines = append(d.Lines, key)
			if state == Deleted {
				// if delete set curr, prev to the value in the line
				d.Changed[key] = Changes{state, key, value, value}
			} else {
				// if Changed
				d.Changed[key] = Changes{state, key, value, "n/a"}
			}
		} else {
			d.Changed[key] = Changes{Changed, key, value, m.value}
		}
	}
	return strconv.Itoa(len(d.Lines))
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
