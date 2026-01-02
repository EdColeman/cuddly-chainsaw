package repo

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
)

func TestTimeout(t *testing.T) {

	now := time.Now().UTC()
	// create expired time *2 of expiration threshold
	expiredTime := now.Add(-timeoutThreshold * 2 * time.Millisecond)
	// create valid time 1/2 of expiration threshold
	validTime := now.Add((-timeoutThreshold / 2) * time.Millisecond)

	var tests = []struct {
		name  string
		input *time.Time
		want  bool
	}{
		{"nil treated as expired", nil, true},
		{"expired time *2 of expiration threshold", &expiredTime, true},
		{"valid time has not expired", &validTime, false},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ans := checkTimeoutExpired(tt.input)
			if ans != tt.want {
				t.Errorf("got %v, want %v", ans, tt.want)
			}
		})
	}
}

func TestProcessState(t *testing.T) {

	ctx := context.WithValue(context.Background(), "a", 123)
	ep1 := model.NewEndPoint("http://localhost:8090", "system1")

	// ctx context.Context, pool *pgxpool.Pool, tx pgx.Tx
	state, err := processState(ctx, nil, nil, ep1)
	if err != nil {
		t.Errorf("got %b, err %v", state, err)
		return
	}
	fmt.Println("State returned is " + state.String())
}
