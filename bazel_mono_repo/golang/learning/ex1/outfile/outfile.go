package outfile

import (
	"fmt"
	"log"
	"os"
	"time"

	"github.com/parquet-go/parquet-go"
	"github.com/parquet-go/parquet-go/compress"
	"github.com/parquet-go/parquet-go/encoding"
	"github.com/parquet-go/parquet-go/parquet"
	"github.com/parquet-go/parquet-go/writer"
)

type Person struct {
	ID   int64     `parquet:"name=id, type=INT64"`
	Name string    `parquet:"name=name, type=BYTE_ARRAY, convertedtype=UTF8"`
	Ts   time.Time `parquet:"name=ts, type=INT96"`
}

func Hello() {
	fmt.Println("Hello")

	// schema := parquet.SchemaOf(new(simpleFlat))
	// writer := parquet.NewGenericWriter[any](output, schema)
	data := []Person{
		{ID: 1, Name: "Alice", Ts: time.Now()},
		{ID: 2, Name: "Bob", Ts: time.Now()},
	}

	// Create a local file writer
	fw, err := os.Create("output.parquet")
	if err != nil {
		log.Println("can not create file:", err)
		return
	}
	defer fw.Close()

	// Create a parquet writer
	pw, err := writer.NewParquetWriter(fw, new(Person), 4)
	if err != nil {
		log.Println("can not create parquet writer:", err)
		return
	}
	pw.CompressionType = parquet.CompressionCodec_SNAPPY

	defer pw.WriteStop()

	// Write data
	for _, d := range data {
		if err = pw.Write(d); err != nil {
			log.Println("Write error:", err)
			return
		}
	}

	fmt.Println("Parquet file created successfully!")
}
