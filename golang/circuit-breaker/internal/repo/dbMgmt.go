package repo

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/jackc/pgx/v5/pgxpool"
)

type DdMgmt interface {
	CreateTable(ctx context.Context)
	CreateTypes(ctx context.Context)
	TestQuery(ctx context.Context)
	Close() error
}

type DbManager struct {
	pool *pgxpool.Pool
}


func Connect(ctx context.Context) *DbManager {

	dbUrl := getDbConnUrl()

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

	return &DbManager{
		pool: pool,
	}
}

func (db DbManager)Close() error{
	if db.pool != nil {
		fmt.Println("Closing database pool")
		db.pool.Close()
	}
	return nil
}

func getDbConnUrl() string {
	const envUserNameKey = "DB_USERNAME"
	const envUserPassKey = "DB_USER_PASS"

	const host = "localhost"
	const port = 5432
	const db = "circuit_test_db"

	userEnv, exists := os.LookupEnv(envUserNameKey)
	if !exists {
		log.Fatalf("Database user_env name env variable %s not set`", envUserNameKey)
	}

	passwdEnv, exists := os.LookupEnv(envUserPassKey)
	if !exists {
		log.Fatalf("Database user_env name env variable %s not set`", envUserPassKey)
	}

	fmt.Printf("Hello - user_env %s, passwd_env %s\n", userEnv, passwdEnv)

	// urlExample := "postgres://username:password@localhost:5432/database_name"
	dbUrl := fmt.Sprintf("postgres://%s:%s@%s:%d/%s", userEnv, passwdEnv, host, port, db)

	fmt.Printf("Hello - database %s\n", db)
	return dbUrl
}

func (db DbManager) Pool()  *pgxpool.Pool{
	return db.pool
}

func (db DbManager)TestQuery(ctx context.Context) {
	// SQL query to select table names from the information schema
	query := `
		SELECT table_name
		FROM information_schema.tables
		WHERE table_schema = 'public'
		AND table_type = 'BASE TABLE'
		ORDER BY table_name;
	`

	rows, err := db.pool.Query(ctx, query)
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

func (db DbManager)CreateTypes(ctx context.Context) {
	q1 := "CREATE TYPE conn_state AS ENUM ('OPEN', 'HALF-OPEN', 'CLOSED');"
	result, err := db.pool.Query(ctx, q1)
	if err != nil {
		log.Fatalf("Failed to create ConnState enum type: %v\n", err)
	}
	defer result.Close()
	result.Close()
}

func (db DbManager)CreateTable(ctx context.Context) {
	q2 := fmt.Sprintf("CREATE TABLE IF NOT EXISTS %s ("+
		"id SERIAL PRIMARY KEY, "+
		"url TEXT UNIQUE NOT NULL, "+
		"state INTEGER NOT NULL, "+
		"description TEXT, "+
		"timeout TIMESTAMPTZ NULL, "+
		"lastErrors TIMESTAMPTZ[]);", model.TableName())

	result, err := db.pool.Query(ctx, q2)
	if err != nil {
		log.Fatalf("Failed to create %s: %v\n", model.TableName(), err)
	}
	defer result.Close()
	result.Close()
}
