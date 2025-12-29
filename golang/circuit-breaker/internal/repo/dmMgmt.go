package repo

import (
	"context"
	"fmt"
	"log"

	"github.com/jackc/pgx/v5/pgxpool"
)

const tableName = "endpoints"

type ConnState int

// allowed circuit breaker states
const (
	Open ConnState = iota
	Closed
	HalfOpen
)

// ConnStateNames convert from ConnState enum to a string
var ConnStateNames = map[ConnState]string{
	Open:     "OPEN",
	Closed:   "CLOSED",
	HalfOpen: "HALF-OPEN",
}

// ConnStateValues convert from string to enum value
var ConnStateValues = map[string]ConnState{
	"OPEN":      Open,
	"CLOSED":    Closed,
	"HALF-OPEN": HalfOpen,
}

// Implement the fmt.Stringer interface
func (cs ConnState) String() string {
	return ConnStateNames[cs]
}

type EndPoint struct {
	Id      int64     `json:"id"`
	Url     string    `json:"url"`
	State   ConnState `json:"state"`
	Timeout int64     `json:"timeout"`
}

// General database functions to create and manage circuit breaker tables
func createTable() {}

// GetEndpoint returns the circuit breaker status for an end point
func GetEndpoint(ctx context.Context, pool *pgxpool.Pool, url string) (state ConnStatem, ok bool) {

	return Closed, false
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
		"status conn_state NOT NULL, "+
		"timeout TIMESTAMPTZ NULL, "+
		"last_errors TIMESTAMPTZ[]);", tableName)

	result, err := conn.Query(ctx, q2)
	if err != nil {
		log.Fatalf("Failed to create %s: %v\n", tableName, err)
	}
	defer result.Close()
	result.Close()
}
