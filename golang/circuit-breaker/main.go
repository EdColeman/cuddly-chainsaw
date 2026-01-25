package main

import (
	"context"
	"fmt"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/repo"
	_ "github.com/jackc/pgx/v5"
)

func main() {

	ctx := context.Background()
	dbUrl := repo.GetDbConnUrl()

	pool := repo.ConnUrl(ctx, dbUrl)
	defer pool.Close()

	fmt.Printf("Hello - user_env %v\n", pool)

	fmt.Println("create types")
	repo.CreateTypes(ctx, pool)

	fmt.Printf("Hello - user_env %v\n", pool)

	fmt.Println("create table")
	repo.CreateTable(ctx, pool)

	ep1 := model.NewEndPoint("http://localhost:8090", "system1")

	store := repo.NewStore(pool)

	err := store.CreateEndPoint(ctx, ep1)
	if err != nil {
		fmt.Println("CreateEndPoint failed\n", err)
		return
	}

	result, okay := store.ListEndPoints(ctx)
	if !okay {
		fmt.Println("ListEndPoints failed")
	}

	fmt.Printf("EndPoints %v+\n", result)

	state, err := store.CheckEndPointState(ctx, ep1.Url)
	if err != nil {
		fmt.Println("CheckEndPointState failed")
	}

	fmt.Println("CheckEndPointState current state " + model.ConnStateNames[state])

	repo.TestQuery(ctx, pool)

	stat := pool.Stat()
	fmt.Printf("stat: %+v\n", stat)

	pool.Close()
}
