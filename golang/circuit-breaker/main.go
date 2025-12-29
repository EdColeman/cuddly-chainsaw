package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/repo"
	_ "github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

func main() {

	ctx := context.Background()

	pool := connUrl()
	defer pool.Close()

	fmt.Printf("Hello - user_env %v\n", pool)

	repo.CreateTypes(ctx, pool)
	repo.CreateTable(ctx, pool)

	repo.TestQuery(ctx, pool)

	pool.Stat()

	pool.Close()
}

func connUrl() *pgxpool.Pool {
	const envUserNameKey = "DB_USERNAME"
	const envUserPassKey = "DB_USER_PASS"

	const host = "localhost"
	const port = 5432
	const db = "circuit_test_db"

	userEnv, exists := os.LookupEnv(envUserNameKey)
	if !exists {
		fmt.Fprintf(os.Stderr, "Database user_env name env variable %s not set`", envUserNameKey)
		os.Exit(1)
	}

	passwdEnv, exists := os.LookupEnv(envUserPassKey)
	if !exists {
		fmt.Fprintf(os.Stderr, "Database user_env name env variable %s not set`", envUserPassKey)
		os.Exit(1)
	}
	fmt.Printf("Hello - user_env %s, passwd_env %s\n", userEnv, passwdEnv)

	// urlExample := "postgres://username:password@localhost:5432/database_name"
	dbUrl := fmt.Sprintf("postgres://%s:%s@%s:%d/%s", userEnv, passwdEnv, host, port, db)

	fmt.Printf("Hello - databse %s\n", db)

	//
	//conn, err := pgx.Connect(context.Background(), dbUrl)
	//if err != nil {
	//	fmt.Fprintf(os.Stderr, "Unable to connect to database: %v\n", err)
	//	os.Exit(1)
	//}

	pool, err := pgxpool.New(context.Background(), dbUrl)

	if err != nil {
		fmt.Fprintf(os.Stderr, "Unable to to create database pool: %v\n", err)
		os.Exit(1)
	}

	err = pool.Ping(context.Background())
	if err != nil {
		log.Fatalf("Could not ping the database: %v\n", err)
	}
	fmt.Println("Database ping successful!")

	return pool
}
