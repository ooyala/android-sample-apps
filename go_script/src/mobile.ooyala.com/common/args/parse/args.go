package build

import "log"
import "flag"

func ParseArgs(c Config, l *log.Logger) error {
	l.Println("args.ParseArgs")
	flag.Parse()
	return nil
}
