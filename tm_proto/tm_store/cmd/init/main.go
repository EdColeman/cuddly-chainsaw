package main

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/jackc/pgx/v5"
	"github.com/spf13/viper"
)

func main() {
	viper.AddConfigPath("./configs") // path to look for the config file in
	viper.SetConfigName("db")        // name of config file (without extension)
	viper.AddConfigPath(".")         // path to look for the config file in
	viper.SetConfigType("yaml")

	if err := viper.ReadInConfig(); err != nil {
		slog.Error("Error reading config file, %v\n", "error", err)
	}

	port := viper.GetInt("database.port")
	fmt.Println("Port:", port)

	connStr := fmt.Sprintf("postgres://%s:%s@%s:%d/%s",
		viper.GetString("database.username"),
		viper.GetString("database.password"),
		viper.GetString("database.host"),
		viper.GetInt("database.port"),
		viper.GetString("database.dbname"),
	)

	conn, err := pgx.Connect(context.Background(), connStr)
	if err != nil {
		slog.Error("Unable to connect to database: %v\n", "error", err)
		return
	}
	defer conn.Close(context.Background())

	err = conn.Ping(context.Background())
	if err != nil {
		slog.Error("Unable to ping database: %v\n", "error", err)
		return
	}

	slog.Info("Successfully connected and pinged the database!")
}
