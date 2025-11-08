package main

import (
	"log/slog"
	"os"

	"github.com/spf13/viper"
)

type application struct {
	logger *slog.Logger
}

func (a *application) init() {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelDebug, AddSource: true}))
	slog.SetDefault(logger)

	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./resources")
}
