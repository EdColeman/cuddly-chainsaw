package ui

import "embed"

//go:embed html/** static/** resources/**
var StaticFiles embed.FS
