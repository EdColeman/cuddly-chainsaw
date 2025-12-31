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

	repo.CreateTypes(ctx, pool)
	repo.CreateTable(ctx, pool)

	ep1 := model.NewEndPoint("http://localhost:8090", "system1")

	err := repo.CreateNewEndPoint(ctx, pool, ep1)
	if err != nil {
		fmt.Println("CreateEndPoint failed\n", err)
		return
	}

	result, okay := repo.ListEndPoints(ctx, pool)
	if !okay {
		fmt.Println("ListEndPoints failed\n")
	}

	fmt.Printf("EndPoints %v+\n", result)

	state, err := repo.CheckEndPointState(ctx, pool, ep1.Url)
	if err != nil {
		fmt.Println("CheckEndPointState failed")
	}

	fmt.Println("CheckEndPointState current state " + model.ConnStateNames[state])

	repo.TestQuery(ctx, pool)

	stat := pool.Stat()
	fmt.Printf("stat: %+v\n", stat)

	pool.Close()
}
