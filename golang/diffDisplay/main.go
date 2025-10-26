package main

import (
	"diffDisplay/cmd/web"
	"diffDisplay/config"
	lineDiff "diffDisplay/diff-files"
	"fmt"
	"log/slog"
	"os"

	"github.com/spf13/viper"
)

func main() {

	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelDebug, AddSource: true}))
	slog.SetDefault(logger)

	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./resources")

	err := viper.ReadInConfig() // Find and read the config file
	if err != nil {             // Handle errors reading the config file
		logger.Error("invalid configuration file", "error", err.Error())
		panic(fmt.Errorf("fatal error config file: %w", err))
	}

	htmlPort := viper.GetInt("port")
	leftPath := viper.GetString("leftPath")
	rightPath := viper.GetString("rightPath")

	app := &config.Application{Logger: logger}

	result, ok := lineDiff.CompareFiles(leftPath, rightPath)
	if !ok {
		logger.Error("Failed to compare files")
		os.Exit(1)
	}

	fmt.Printf("left:%d\n", result.NumLeft)
	fmt.Printf("right:%d\n", result.NumRight)
	fmt.Printf("changed:%d\n", result.NumLines)
	fmt.Printf("skipped:%d\n", result.NumSkipped)
	fmt.Printf("** DR **:%v\n", result.Changed)

	web.Server(app, htmlPort)
}
