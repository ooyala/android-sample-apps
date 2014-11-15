package log

import gl "log"
import "os"

func NewStdoutLogger() *gl.Logger {
	return gl.New(os.Stdout, "", gl.LstdFlags)
}
