package main

import (
	"fmt"
	"net/http"

	"github.com/a-h/templ"
)

func server(serverPort int) {
	fmt.Println("Called with serverPort", serverPort)
	component := hello("Templ!")
	http.Handle("/", templ.Handler(component))

	component2 := hello("Clicked")
	http.Handle("/clicked", templ.Handler(component2))

	http.ListenAndServe(fmt.Sprintf(":%d", serverPort), nil)
	fmt.Println("Listening on Port: ", serverPort)
}
