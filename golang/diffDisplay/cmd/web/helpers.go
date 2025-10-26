package web

import (
	config "diffDisplay/config"
	"net/http"
)

func serverError(app *config.Application, w http.ResponseWriter, r *http.Request, err error) {
	var (
		method = r.Method
		uri    = r.URL.RequestURI()
	)

	app.Logger.Error(err.Error(), "method", method, "uri", uri)
	http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
}

func clientError(app *config.Application, w http.ResponseWriter, status int) {
	http.Error(w, http.StatusText(status), status)
}
