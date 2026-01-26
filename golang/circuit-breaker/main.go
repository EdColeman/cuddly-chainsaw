package main

import (
	"context"
	"fmt"
	"log"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/repo"
	_ "github.com/jackc/pgx/v5"
	"github.com/spf13/viper"
)

type Config struct {
	User struct {
		username string
		userPass string
	}
	Database struct {
		Host string `mapstructure:"host"`
		Port string `mapstructure:"port"`
	} `mapstructure:"database"`
}

func initConfig() *Config {
	viper.SetConfigName("test_config")
	viper.AddConfigPath("./resources")

	err := viper.BindEnv(repo.EnvUserNameKey)
	if err != nil {
		log.Panicf("env %s is not set", repo.EnvUserNameKey)
	}

	err = viper.BindEnv(repo.EnvUserPassKey)
	if err != nil {
		log.Panicf("env %s is not set", repo.EnvUserPassKey)
	}

	// Find and read the config file
	if err := viper.ReadInConfig(); err != nil {
		log.Panic("fatal error reading config file", err)
	}

	var config Config

	if err := viper.Unmarshal(&config); err != nil {
		log.Fatalf("Unable to unmarshal config into struct: %s", err)
	}

	//username := viper.Get(repo.EnvUserNameKey)
	//userPass := viper.Get(repo.EnvUserPassKey)
	//
	//Config.User.username = username
	//Config.User.userPass = userPass

	return &config
}
func main() {

	config := initConfig()
	log.Printf("** CONFIG %+v", config)

	ctx := context.Background()
	dbMgr := repo.Connect(ctx)
	defer dbMgr.Close()

	fmt.Printf("Hello - user_env %v\n", dbMgr)

	fmt.Println("create types")
	dbMgr.CreateTypes(ctx)

	fmt.Println("create table")
	dbMgr.CreateTable(ctx)

	ep1 := model.NewEndPoint("http://localhost:8090", "system1")

	store := repo.NewStore(dbMgr.Pool())

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

	dbMgr.TestQuery(ctx)

	stat := dbMgr.Pool().Stat()
	fmt.Printf("stat: %+v\n", stat)
}
