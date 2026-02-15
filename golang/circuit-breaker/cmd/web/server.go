package main

import (
	"fmt"
	"io/fs"
	"log"
	"net/http"
	"strings"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/ui"
	"github.com/spf13/viper"
)

func main() {
	staticFiles := ui.StaticFiles
	files, err := fs.ReadDir(staticFiles, "static")
	if err != nil {
		log.Fatal(err)
	}

	for _, file := range files {
		fmt.Println("Embedded file:", file.Name())
	}

	// for testing - read the config from the bundled embedded files
	data, err := staticFiles.ReadFile("resources/default.yaml")
	if err != nil {
		log.Fatalf("Fatal error reading embedded config file: %v", err)
	}
	var appCtx = application{}
	appCtx.init(data)

	serverPort := viper.GetInt("port")
	fmt.Printf("Port: %d", serverPort)
	// appCtx.logger.Info("Starting server on serverPort %d", serverPort)

	mux := http.NewServeMux()
	// create file system rooted at /static from the embedded file system
	httpFS, err := fs.Sub(staticFiles, "static")
	fileServer := http.FileServer(http.FS(httpFS))
	mux.Handle("GET /static/", http.StripPrefix("/static", neuter(fileServer)))

	// Handle GET requests to the root path
	mux.HandleFunc("GET /{$}", appCtx.homePage)

	mux.HandleFunc("GET /data", appCtx.getData)
	//// Handle GET requests to a sub path
	//mux.HandleFunc("GET /clicked", getSub)
	//// Handle POST requests to the root path
	//mux.HandleFunc("POST /accept", acceptFile)


	mux.HandleFunc("PUT /put", appCtx.putHandler)

	mux.HandleFunc("POST /post", appCtx.postHandler)

	fmt.Println("Start server listening on Port: ", serverPort)
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
