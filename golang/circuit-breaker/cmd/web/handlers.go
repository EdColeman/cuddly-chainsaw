package main

import (
	"html/template"
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
