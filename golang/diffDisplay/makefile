
GO_SRC = ./ 
RESOURCES_DIR = ./resources

HTMX_MIN_FILE = $(RESOURCES_DIR)/htmx.min.js
HTMX_FILE = $(RESOURCES_DIR)/htmx.js

HTMX_VER = 2.0.6
HTMX_MIN_SHA256 = b6768eed4f3af85b73a75054701bd60e17cac718aef2b7f6b254e5e0e2045616
HTMX_SHA256 = f31cc3cf16109d7ae75bea6928540c68a243bea12344321c44dcb97d6c88567b
#default target

.PHONY: fmt vet build test run

all: build

check_htmx_min_sha := echo "$(HTMX_MIN_SHA256) $(HTMX_MIN_FILE)" | sha256sum -c -

check_htmx_sha := echo "$(HTMX_SHA256) $(HTMX_FILE)" | sha256sum -c -

# download js resources for htmx and templ
# htmx github repo for artifacts and sha sums : https://github.com/bigskysoftware/htmx/releases
install_js_deps: download_htmx_min download_htmx
	echo `$(check_htmx_sha)`
	echo `$(check_htmx_min_sha)`

fmt:
	go fmt $(GO_SRC)

vet: fmt
	go vet $(GO_SRC)

build: vet test
	@echo "Building diffDisplay"
	go build $(GO_SRC)

test:
	@echo "Testing diffDisplay"
	go test -v $(GO_SRC)

run:
	go run main.go

download_htmx_min:
	@if test -f $(HTMX_MIN_FILE); then \
		echo "$(HTMX_MIN_FILE) file exists"; \
	else \
		echo "downloading htmx_min to $(HTMX_MIN_FILE)"; \
		curl --create-dirs -O --output-dir $(RESOURCES_DIR) https://cdn.jsdelivr.net/npm/htmx.org@$(HTMX_VER)/dist/htmx.min.js; \
	fi

download_htmx:
	@if test -f $(HTMX_FILE); then \
		echo "$(HTMX_FILE) file exists"; \
	else \
		echo "downloading htmx to $(HTMX_FILE)"; \
		curl --create-dirs -O --output-dir $(RESOURCES_DIR) https://cdn.jsdelivr.net/npm/htmx.org@$(HTMX_VER)/dist/htmx.js; \
	fi

