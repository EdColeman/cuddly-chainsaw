package main

import (
	"fmt"
	"github.com/EdColeman/cuddly-chainsaw/bazel_mono_repo/golang/learning/ex1/sub1"
)

func main() {
	fmt.Println(sub1.Get())

	fmt.Println(sub1.Read("../data/lines.txt"))
}
