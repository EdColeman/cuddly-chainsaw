package difflines

import (
	"fmt"
	"log"
	"testing"
)

func Test_invalid_lines(t *testing.T) {
	left := []string{"aaaaa 123"} // no comma
	right := []string{"bbbbb"}    // no value

	result, ok := compareLines(left, right)
	if !ok {
		t.Errorf("diff failed")
	}
	if result.numLeft != len(left) {
		t.Errorf("left count = %d, want %d", result.numLeft, len(left))
	}
	if result.numRight != len(right) {
		t.Errorf("right count = %d, want %d", result.numRight, len(right))
	}
	if result.numLines != 0 {
		t.Errorf("change count = %d, want %d", result.numLines, 0)
	}
	if result.numSkipped != 2 {
		t.Errorf("skip count = %d, want %d", result.numLines, 2)
	}
}

func Test_equal_lines(t *testing.T) {
	left := []string{"aaaaa, 123"}
	right := []string{"aaaaa, 123"}

	result, ok := compareLines(left, right)
	if !ok {
		t.Errorf("diff failed")
	}
	if result.numLeft != len(left) {
		t.Errorf("left count = %d, want %d", result.numLeft, len(left))
	}
	if result.numRight != len(right) {
		t.Errorf("right count = %d, want %d", result.numRight, len(right))
	}
	if result.numLines != 1 {
		t.Errorf("change count = %d, want %d", result.numLines, 1)
	}
}

func Test_changed_lines(t *testing.T) {
	left := []string{"aaaaa, 123"}
	right := []string{"aaaaa, 234"}

	result, ok := compareLines(left, right)
	if !ok {
		t.Errorf("diff failed")
	}
	if result.numLeft != len(left) {
		t.Errorf("left count = %d, want %d", result.numLeft, len(left))
	}
	if result.numRight != len(right) {
		t.Errorf("right count = %d, want %d", result.numRight, len(right))
	}
	if result.numLines != 1 {
		t.Errorf("change count = %d, want %d", result.numLines, 1)
	}
}
func Test_deleted_lines(t *testing.T) {
	left := []string{"aaaaa, 123", "bbbbb, 321"}
	right := []string{"aaaaa, 234"}

	result, ok := compareLines(left, right)

	fmt.Print("deletes: ", result)

	if !ok {
		t.Errorf("diff failed")
	}
	if result.numLeft != len(left) {
		t.Errorf("left count = %d, want %d", result.numLeft, len(left))
	}
	if result.numRight != len(right) {
		t.Errorf("right count = %d, want %d", result.numRight, len(right))
	}
	if result.numLines != 2 {
		t.Errorf("change count = %d, want %d", result.numLines, 2)
	}
}

// filtered result should be 1 change (aaaaa) and 1 add (ccccc), (bbbbb) is deleted
func Test_filter_lines(t *testing.T) {
	left := []string{"aaaaa, 9999", "bbbbb, 321"}
	right := []string{"aaaaa, 1111", "ccccc, 2222"}

	result, ok := compareLines(left, right)

	if !ok {
		t.Errorf("failed to diff files")
	}
	out := filter(result)
	if len(out) != 2 {
		t.Errorf("filtered output, received = %d, want %d", len(out), 2)
	}
}

// Inouts
// left
// aaaaaa, 10   -> 10 (no change)
// bbbbbb, 20   -> 21 (changed)
// cccccc, 30   -> 30 (no change)
// dddddd, 40   -> (deleted)
// eeeeee, 50   -> 50 (no change)
//
//	n/a -> ffffff 61 (added)
//
// zzzzzz, 260  -> 260 (no change)
func Test_read_files(t *testing.T) {

	leftPath := "../test_data/input1.csv"
	rightPath := "../test_data/change1.csv"

	result, ok := CompareFiles(leftPath, rightPath)
	if !ok {
		log.Fatalf("Failed to compare files")
	}
	out := filter(result)
	if len(out) != 6 {
		t.Errorf("filtered output, received = %d, want %d", len(out), 6)
	}
	fmt.Println("OUT: ", out)
}
