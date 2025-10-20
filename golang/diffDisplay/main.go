package main

import (
	"diffDisplay/cmd/web"
	lineDiff "diffDisplay/diff-files"
	"fmt"
	"log"

	"github.com/spf13/viper"
)

//TIP <p>To run your code, right-click the code and select <b>Run</b>.</p> <p>Alternatively, click
// the <icon src="AllIcons.Actions.Execute"/> icon in the gutter and select the <b>Run</b> menu item from here.</p>

func main() {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./resources")

	err := viper.ReadInConfig() // Find and read the config file
	if err != nil {             // Handle errors reading the config file
		panic(fmt.Errorf("fatal error config file: %w", err))
	}

	htmlPort := viper.GetInt("port")
	leftPath := viper.GetString("leftPath")
	rightPath := viper.GetString("rightPath")

	result, ok := lineDiff.CompareFiles(leftPath, rightPath)
	if !ok {
		log.Fatalf("Failed to compare files")
	}

	fmt.Println("left: ", result.NumLeft)
	fmt.Println("right:\n", result.NumRight)
	fmt.Println("changed:\n", result.NumLines)
	fmt.Println("skipped:\n", result.NumSkipped)
	fmt.Println("** DR **:\n", result.Changed)

	web.Server(htmlPort)
}

func readConfig() {
}
