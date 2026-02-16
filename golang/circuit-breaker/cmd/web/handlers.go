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

type PageData struct {
	Title   string
	Records map[string]TableRow
}

type TableRow struct {
	Id       string
	Name     string
	State    string
	NextTry  string
	Endpoint string
}

var memStore = map[string]TableRow{
	"1": {Id: "1", Name: "endpoint 1", State: "CLOSED", NextTry: "0:00", Endpoint: "http://spmewhere/"},
	"2": {Id: "2", Name: "endpoint 2", State: "OPEN", NextTry: "0:30", Endpoint: "http://nowhere/"},
	"3": {Id: "3", Name: "endpoint 3", State: "HALF", NextTry: "0:00", Endpoint: "http://overhere/"},
}

func (appCtx *application) getData(w http.ResponseWriter, r *http.Request) {
	ts, err := template.ParseFS(staticFiles, "html/**/*.tmpl")
	if err != nil {
		log.Print(err.Error())
		http.Error(w, "Internal Server Error - could not read template files", http.StatusInternalServerError)
		return
	}
	pd := PageData{Title: "A title", Records: memStore}
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	err = ts.ExecuteTemplate(w, "data.tmpl", pd)
	if err != nil {
		log.Print(err.Error())
		fmt.Printf("%s", fmt.Errorf("template error %w", err))
		http.Error(w, "Internal Server Error - could not read home page template", http.StatusInternalServerError)
	}
}

func (appCtx *application) homePage(w http.ResponseWriter, r *http.Request) {
	ts, err := template.ParseFS(staticFiles, "html/**/*.tmpl")
	if err != nil {
		log.Print(err.Error())
		fmt.Printf("%s", fmt.Errorf("template error %w", err))
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

	fmt.Printf("Received put request handler %+v", r)

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

	fmt.Printf("Received post request %+v\n", r)
	fmt.Printf("Received post body %+v\n", r.Body)
	err := r.ParseForm()
	if err != nil {
		fmt.Printf("Received post form error %+v\n", err)
	}
	fmt.Printf("header target: %+v\n", r.Header.Get("Hx-Target"))
	fmt.Printf("Index: %+v\n", r.URL.Query().Get("endpoint_idx"))
	fmt.Printf("inout next try: %+v\n", r.FormValue("NextTryValue"))
	fmt.Printf("inout status: %+v\n", r.FormValue("checkbox_status"))

	bodyBytes, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "Can't read body", http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	// In a real application, you would typically unmarshal the JSON
	// body into a Go struct and update a resource (e.g., in a database).
	// For this example, we just echo the length of the received data.
	fmt.Fprintf(w, "Received POST request. Body length: %d bytes\n", len(bodyBytes))
}
