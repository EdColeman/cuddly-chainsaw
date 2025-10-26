package web

import (
	"diffDisplay/config"
	"fmt"
	"net/http"
	"strings"
)

func Server(app *config.Application, serverPort int) {

	app.Logger.Info("Starting server on port %d", serverPort)

	mux := http.NewServeMux()
	// Handle GET requests to the root path
	mux.HandleFunc("GET /{$}", homePage)
	// Handle GET requests to a sub path
	mux.HandleFunc("GET /clicked", getSub)
	// Handle POST requests to the root path
	mux.HandleFunc("POST /accept", acceptFile)

	fileServer := http.FileServer(http.Dir("./ui/static"))
	mux.Handle("GET /static/", http.StripPrefix("/static", neuter(fileServer)))

	http.ListenAndServe(fmt.Sprintf(":%d", serverPort), mux)
	fmt.Println("Listening on Port: ", serverPort)
}

// stop file browsing in static dir: see https://www.alexedwards.net/blog/disable-http-fileserver-directory-listings
func neuter(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if strings.HasSuffix(r.URL.Path, "/") {
			http.NotFound(w, r)
			return
		}

		next.ServeHTTP(w, r)
	})
}
