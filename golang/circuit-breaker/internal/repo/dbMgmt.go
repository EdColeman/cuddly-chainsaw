package repo

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

const tableName = "endpoints"

// CreateNewEndPoint create an endpoint
func CreateNewEndPoint(ctx context.Context, pool *pgxpool.Pool, endPoint model.EndPoint) error {
	tx, err := pool.Begin(ctx)

	if err != nil {
		return fmt.Errorf("error starting transaction: %w", err)
	}

	// Defer a function to handle commit or rollback
	defer func() {
		if err != nil {
			// Rollback if an error occurred during the transaction
			tx.Rollback(ctx)
		} else {
			// Commit if everything was successful
			err = tx.Commit(ctx)
		}
	}()

	stmt := `INSERT INTO ` + tableName + ` (url, state, description) ` +
		`VALUES( @url, @state, @description)`

	args := pgx.NamedArgs{
		"url":         endPoint.Url,
		"state":       endPoint.State,
		"description": endPoint.Description,
	}

	_, err = pool.Exec(ctx, stmt, args)
	if err != nil {

	}

	err = tx.Commit(ctx)
	if err != nil {
		return err
	}

	return nil
}

func ListEndPoints(ctx context.Context, pool *pgxpool.Pool) (endpoints []model.EndPoint, ok bool) {
	return ListEndPointsFilter(ctx, pool, func(v model.EndPoint) bool {
		return true
	})
}

func ListEndPointsFilter(ctx context.Context, pool *pgxpool.Pool, filter model.EndPointFilter) (endpoints []model.EndPoint, ok bool) {
	tx, err := pool.Begin(ctx)

	fmt.Println("Starting ListEndPointsFilter")

	if err != nil {
		fmt.Println("Failed to begin transaction", err)
		return nil, false
	}

	// Defer a function to handle commit or rollback
	defer func() {
		if err != nil {
			// Rollback if an error occurred during the transaction
			fmt.Println("ListEndPointsFilter rollback")
			tx.Rollback(ctx)
		} else {
			// Commit if everything was successful
			fmt.Println("ListEndPointsFilter commit")
			err = tx.Commit(ctx)
		}
	}()

	// stmt := `SELECT id, url, state, description, timeout, lasterrors FROM ` + tableName
	stmt := `SELECT * FROM ` + tableName

	rows, err := pool.Query(ctx, stmt)
	if err != nil {
		fmt.Println("Failed select", err)
		return nil, false
	}

	fmt.Printf("Found %v+\n", rows)
	var p1 []model.EndPoint
	for rows.Next() {
		var endpoint model.EndPoint
		err := rows.Scan(&endpoint.Id, &endpoint.Url, &endpoint.State, &endpoint.Description, &endpoint.Timeout, &endpoint.LastErrors)
		if err != nil {
			fmt.Println("Rows failed to scan", err)
		}
		fmt.Printf("Rows scanned appending %v+\n", endpoint)
		p1 = append(p1, endpoint)
	}

	return p1, true
}

func CheckEndPointstate(ctx context.Context, pool *pgxpool.Pool, url string) bool {
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
		"state conn_state NOT NULL, "+
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
