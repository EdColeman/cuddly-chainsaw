package main

import (
	"fmt"
	"github.com/a-h/templ"
	"net/http"
)

func main() {
	fmt.Println("Starting...")

	component := hello("John")
	http.Handle("/", templ.Handler(component))

	var port = "10081"

	http.ListenAndServe(":"+port, nil)
	fmt.Println("Listening on Port {}} 🚀", port)
}
