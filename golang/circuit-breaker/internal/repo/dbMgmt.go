package repo

import (
	"context"
	"errors"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/jackc/pgx/v5/pgxpool"
)

const tableName = "endpoints"

func CheckEndPointState(ctx context.Context, pool *pgxpool.Pool, url string) (model.ConnState, error) {
	tx, err := pool.Begin(ctx)

	fmt.Println("Starting ListEndPointsFilter")

	// on failure return circuit break open to inform the client to not make calls.
	if err != nil {
		fmt.Println("Failed to begin transaction", err)
		return model.Open, err
	}

	// Defer a function to handle commit or rollback
	defer func() {
		if err != nil {
			// Rollback if an error occurred during the transaction
			fmt.Println("CheckEndPointstate rollback")
			tx.Rollback(ctx)
		} else {
			// Commit if everything was successful
			fmt.Println("CheckEndPointstate commit")
			err = tx.Commit(ctx)
		}
	}()

	stmt := "SELECT * FROM " + tableName + " WHERE url = $1"

	rows, err := pool.Query(ctx, stmt, url)
	if err != nil {
		fmt.Println("Failed select for url "+url, err)
		return model.Open, err
	}

	defer rows.Close()

	fmt.Printf("Found %v+\n", rows)

	for rows.Next() {
		var endpoint model.EndPoint
		err := rows.Scan(&endpoint.Id, &endpoint.Url, &endpoint.State, &endpoint.Description, &endpoint.Timeout, &endpoint.LastErrors)
		if err != nil {
			fmt.Println("Rows failed to scan", err)
		}
		fmt.Printf("Rows scanned found: %v+\n", endpoint)

		state, err := processState(endpoint)

		return state, err
	}

	return model.Open, errors.New("Url `" + url + "` not found in database")
}

func processState(endpoint model.EndPoint) (model.ConnState, error) {

	state := model.ConnState(endpoint.State)
	switch state {
	case model.Open:
		return model.Open, nil
	case model.Closed:
		checkTimeoutExpired(endpoint.Timeout)
		return model.Closed, nil
	case model.HalfOpen:
		return model.Closed, nil
	default:
		return model.Open, errors.New("Url `" + endpoint.Url + "` state `" + model.ConnStateNames[state] + "` is undefined")
	}

}

const timeoutThreshold = 5_000 // default 5 second timeout threshold

// checkTimeoutExpired returns false if the endpoint timeout is less than the threshold. Returns true if
// expired or nil.
func checkTimeoutExpired(timeout *time.Time) bool {
	if timeout == nil {
		return true
	}

	now := time.Now().UTC()

	delta := now.Sub(timeout.UTC()).Milliseconds()

	if delta > timeoutThreshold {
		fmt.Printf("timeout expired with %d milliseconds. Threshold is %d\n", delta, timeoutThreshold)
		return true
	}

	return false
}

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
