package main

import "fmt"

func main() {
	fmt.Println("Hello, Bazel! 💚")
	anws := Bye()
	fmt.Printf("said %d\n", anws)
}

func Bye() int {
	fmt.Println("Good Bye, Bazel! 💚")
	return 1
}
