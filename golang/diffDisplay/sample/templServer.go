package sample

import (
	"fmt"
	"io"
	"net/http"
)

func getHello(w http.ResponseWriter, r *http.Request) {
	hello("Welcome!").Render(r.Context(), w)
}

func getSub(w http.ResponseWriter, r *http.Request) {
	clicked("Clicked").Render(r.Context(), w)
}

func templServer(serverPort int) {
	fmt.Println("Called with serverPort", serverPort)

	mux := http.NewServeMux()
	// Handle GET requests to the root path
	mux.HandleFunc("GET /{$}", getHello)
	// Handle POST requests to the root path
	mux.HandleFunc("GET /clicked", getSub)

	http.ListenAndServe(fmt.Sprintf(":%d", serverPort), mux)
	fmt.Println("Listening on Port: ", serverPort)
}

// temp
// mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
// 	hello("Welcome!").Render(r.Context(), w)
// })
