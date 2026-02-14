package main

import (
	"fmt"
	"html/template"
	"io"
	"log"
	"net/http"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/ui"
)

var staticFiles = ui.StaticFiles

func (appCtx *application) homePage(w http.ResponseWriter, r *http.Request) {
	ts, err := template.ParseFS(staticFiles, "html/**/*.tmpl")
	if err != nil {
		log.Print(err.Error())
		http.Error(w, "Internal Server Error - could not read template files", http.StatusInternalServerError)
		return
	}
	err = ts.ExecuteTemplate(w, "home.tmpl", appCtx)
	if err != nil {
		log.Print(err.Error())
		http.Error(w, "Internal Server Error - could not read home page template", http.StatusInternalServerError)
	}
}
func (appCtx *application) updateEndpoint(w http.ResponseWriter, r *http.Request) {
	fmt.Printf("Request: %+v", r)
	w.WriteHeader(http.StatusAccepted)
}

func (appCtx *application) putHandler(w http.ResponseWriter, r *http.Request) {
	// The Go 1.22+ router ensures this is a PUT request.
	// We can now safely process the request body.

	fmt.Printf("Received put request %+v", r)

	bodyBytes, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "Can't read body", http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	// In a real application, you would typically unmarshal the JSON
	// body into a Go struct and update a resource (e.g., in a database).
	// For this example, we just echo the length of the received data.
	fmt.Fprintf(w, "Received PUT request. Body length: %d bytes\n", len(bodyBytes))
}
func (appCtx *application) postHandler(w http.ResponseWriter, r *http.Request) {
	// The Go 1.22+ router ensures this is a PUT request.
	// We can now safely process the request body.

	fmt.Printf("Received post request %+v", r)

	bodyBytes, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "Can't read body", http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	// In a real application, you would typically unmarshal the JSON
	// body into a Go struct and update a resource (e.g., in a database).
	// For this example, we just echo the length of the received data.
	fmt.Fprintf(w, "Received PUT request. Body length: %d bytes\n", len(bodyBytes))
}
