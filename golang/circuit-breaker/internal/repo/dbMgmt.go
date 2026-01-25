package repo

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/jackc/pgx/v5/pgxpool"
)

const tableName = "endpoints"

func ReportEndPointError(ctx context.Context, pool *pgxpool.Pool, url string) bool {
	return false
}

// GetEndPoint returns the circuit breaker state for an end point
func GetEndPoint(ctx context.Context, pool *pgxpool.Pool, url string) (state model.ConnState, ok bool) {
	return model.Closed, false
}

func TestQuery(ctx context.Context, pool *pgxpool.Pool) {
	// SQL query to select table names from the information schema
	query := `
		SELECT table_name
		FROM information_schema.tables
		WHERE table_schema = 'public'
		AND table_type = 'BASE TABLE'
		ORDER BY table_name;
	`

	rows, err := pool.Query(ctx, query)
	if err != nil {
		log.Fatalf("TestQuery failed: %v\n", err)
	}
	defer rows.Close()

	fmt.Println("Tables in 'public' schema:")
	for rows.Next() {
		var tableName string
		if err := rows.Scan(&tableName); err != nil {
			log.Fatalf("Unable to scan row: %v\n", err)
		}
		fmt.Printf("- %s\n", tableName)
	}

	if err = rows.Err(); err != nil {
		log.Fatalf("Row iteration error: %v\n", err)
	}

	rows.Close()
}

func CreateTypes(ctx context.Context, pool *pgxpool.Pool) {
	q1 := "CREATE TYPE conn_state AS ENUM ('OPEN', 'HALF-OPEN', 'CLOSED');"
	result, err := pool.Query(ctx, q1)
	if err != nil {
		log.Fatalf("Failed to create ConnState enum type: %v\n", err)
	}
	defer result.Close()
	result.Close()
}

func CreateTable(ctx context.Context, conn *pgxpool.Pool) {
	q2 := fmt.Sprintf("CREATE TABLE IF NOT EXISTS %s ("+
		"id SERIAL PRIMARY KEY, "+
		"url TEXT UNIQUE NOT NULL, "+
		"state INTEGER NOT NULL, "+
		"description TEXT, "+
		"timeout TIMESTAMPTZ NULL, "+
		"lastErrors TIMESTAMPTZ[]);", tableName)

	result, err := conn.Query(ctx, q2)
	if err != nil {
		log.Fatalf("Failed to create %s: %v\n", tableName, err)
	}
	defer result.Close()
	result.Close()
}

func ConnUrl(ctx context.Context, dbUrl string) *pgxpool.Pool {
	pool, err := pgxpool.New(ctx, dbUrl)

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

func GetDbConnUrl() string {
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

	fmt.Printf("Hello - database %s\n", db)
	return dbUrl
}
