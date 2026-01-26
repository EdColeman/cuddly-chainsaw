package repo

import (
	"context"
	"errors"
	"fmt"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type EndPointPgxStore struct {
	pool *pgxpool.Pool
}

func NewStore(pool *pgxpool.Pool) *EndPointPgxStore {
	return &EndPointPgxStore{
		pool: pool,
	}
}

func (s EndPointPgxStore) CreateEndPoint(ctx context.Context, endPoint model.EndPoint) error {
	tx, err := s.pool.Begin(ctx)

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

	stmt := `INSERT INTO ` + model.TableName() + ` (url, state, description) ` +
		`VALUES(@url, @state, @description)`

	args := pgx.NamedArgs{
		"url":         endPoint.Url,
		"state":       endPoint.State,
		"description": endPoint.Description,
	}

	_, err = s.pool.Exec(ctx, stmt, args)
	if err != nil {
		e2 := fmt.Errorf("error inserting args: %+v into table %s: %w", args, model.TableName(), err)
		return e2
	}

	err = tx.Commit(ctx)
	if err != nil {
		return err
	}

	return nil
}

func (s EndPointPgxStore) ListEndPoints(ctx context.Context) (endpoints []model.EndPoint, ok bool) {
	return s.ListEndPointsFilter(ctx, func(v model.EndPoint) bool {
		return true
	})
}

func (s EndPointPgxStore) ListEndPointsFilter(ctx context.Context, filter model.EndPointFilter) (endpoints []model.EndPoint, ok bool) {
	tx, err := s.pool.Begin(ctx)

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
	stmt := `SELECT * FROM ` + model.TableName()

	rows, err := s.pool.Query(ctx, stmt)
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
func (s EndPointPgxStore) CheckEndPointState(ctx context.Context, url string) (model.ConnState, error) {
	tx, err := s.pool.Begin(ctx)

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

	stmt := "SELECT * FROM " + model.TableName() + " WHERE url = $1"

	rows, err := s.pool.Query(ctx, stmt, url)
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

		state, err := model.ProcessState(endpoint)

		return state, err
	}

	return model.Open, errors.New("Url `" + url + "` not found in database")
}

func (s EndPointPgxStore) ReportEndPointError(ctx context.Context, url string) bool {
	return false
}
