package repo

import (
	"testing"

	"github.com/EdColeman/cuddly-chainsaw/golang/circuit-breaker/internal/model"
)

func TestStoreTypeImplementation(t *testing.T) {
	// compile type check to validate that the model.EndPointStore methods are implemented
	var _ model.EndPointStore = (*EndPointPgxStore)(nil)
}