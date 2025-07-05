package sub1

import "math/rand"

var Lines = []string{
	"line 1 💚",
	"line 2",
	"line 3",
}

func Get() string {
	return Lines[rand.Intn(len(Lines))]
	// return lines[0]
}
