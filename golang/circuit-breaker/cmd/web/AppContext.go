package main

import (
	"bytes"
	"log"
	"log/slog"
	"os"

	"github.com/spf13/viper"
)

type application struct {
	logger *slog.Logger
}

func (a *application) init(data []byte) {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelDebug, AddSource: true}))
	slog.SetDefault(logger)
	viper.SetConfigType("yaml")
	// 3. Read the configuration from the buffer
	if err := viper.ReadConfig(bytes.NewReader(data)); err != nil {
		log.Fatalf("Fatal error reading config: %v", err)
	}
}
