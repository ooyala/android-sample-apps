package build

import "log"
import "flag"

type Config struct {
	Path *string
}

func MakeConfig(logger *log.Logger) Config {
	return Config{
		Path: flag.String("path", "", "The path we get our android sdks"),	
	}
}
