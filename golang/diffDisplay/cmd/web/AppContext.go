package main

import (
	difflines "diffDisplay/internal/diff-files"
	"log/slog"
	"os"

	"github.com/spf13/viper"
)

type ChangeState int

const (
	NoChange ChangeState = iota
	Added
	Deleted
	Changed
)

func (s ChangeState) String() string {
	switch s {
	case NoChange:
		return "nochange"
	case Added:
		return "added"
	case Deleted:
		return "deleted"
	case Changed:
		return "deleted"
	default:
		return "Unknown"
	}
}

type Summary struct {
	NumOrigLines     int // line count from the current set (left)
	NumProposedLines int // line count from proposed set (right)
	NumOutLines      int // number of output Lines )combined changes)
	NumSkipped       int // number of parse errors - invalid Lines skipped
}
type DisplayLine struct {
	Text     string
	Original string
	Update   string
	Change   string
}

type application struct {
	logger       *slog.Logger
	Summary      *Summary
	DisplayLines *[]DisplayLine
}

func (a *application) init() {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelDebug, AddSource: true}))
	slog.SetDefault(logger)

	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./resources")
}

func (appCtx *application) results(result *difflines.DiffResult) {
	// change diff to UI template friendly values
	appCtx.Summary = &Summary{result.NumLeft, result.NumRight, result.NumLines, result.NumSkipped}

	lines := make([]DisplayLine, len(result.Lines))

	for index, value := range result.Lines {
		v, ok := result.Changed[value]
		if ok {
			c := DisplayLine{Text: v.Text, Original: v.Value, Update: v.PrevValue, Change: v.State.String()}
			lines[index] = c
		} else {
			slog.Error("Line " + value + " not in changed list")
		}
	}

	appCtx.DisplayLines = &lines

}
