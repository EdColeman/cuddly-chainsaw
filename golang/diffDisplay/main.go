package main

import (
	lineDiff "diffDisplay/diff-files"
	"fmt"
	"log"
)

//TIP <p>To run your code, right-click the code and select <b>Run</b>.</p> <p>Alternatively, click
// the <icon src="AllIcons.Actions.Execute"/> icon in the gutter and select the <b>Run</b> menu item from here.</p>

func main() {

	leftPath := "./test_data/input1.csv"
	rightPath := "./test_data/change1.csv"

	result, ok := lineDiff.CompareFiles(leftPath, rightPath)
	if !ok {
		log.Fatalf("Failed to compare files")
	}

	fmt.Println("left: ", result.NumLeft)
	fmt.Println("right:\n", result.NumRight)
	fmt.Println("changed:\n", result.NumLines)
	fmt.Println("skipped:\n", result.NumSkipped)
	fmt.Println("** DR **:\n", r)

}
