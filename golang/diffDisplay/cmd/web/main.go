package main

import (
	lineDiff "diffDisplay/internal/diff-files"
	"fmt"
	"log/slog"
	"os"

	"github.com/spf13/viper"
)

func main() {

	var appCtx = application{}
	appCtx.logger = slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelDebug, AddSource: true}))
	slog.SetDefault(appCtx.logger)

	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./resources")

	err := viper.ReadInConfig() // Find and read the config file
	if err != nil {             // Handle errors reading the config file
		appCtx.logger.Error("invalid configuration file", "error", err.Error())
		panic(fmt.Errorf("fatal error config file: %w", err))
	}

	htmlPort := viper.GetInt("port")
	leftPath := viper.GetString("leftPath")
	rightPath := viper.GetString("rightPath")

	result, ok := lineDiff.CompareFiles(leftPath, rightPath)
	if !ok {
		appCtx.logger.Error("Failed to compare files")
		os.Exit(1)
	}

	appCtx.results(&result)

	fmt.Printf("left:%d\n", result.NumLeft)
	fmt.Printf("right:%d\n", result.NumRight)
	fmt.Printf("changed:%d\n", result.NumLines)
	fmt.Printf("skipped:%d\n", result.NumSkipped)
	fmt.Printf("** DR **:%v\n", result.Changed)

	Server(&appCtx, htmlPort)
}
