package log

import gl "log"
import "os"
import "io"
import "time"
import "errors"
import . "mobile.ooyala.com/build/common/path"

func NewFileAndStdoutLogger(outpath FileAbs) (*gl.Logger, error) {
	_, err := os.Stat(outpath.S)
	if err == nil {
		return nil, errors.New(outpath.S + " already exists, won't over-write")
	}
	fileout, err := os.OpenFile(outpath.S, os.O_CREATE|os.O_WRONLY|os.O_APPEND, os.ModePerm)
	if err != nil {
		return nil, err
	}
	mw := io.MultiWriter(fileout, os.Stdout)
	return gl.New(mw, "", gl.LstdFlags), nil
}

func NewFileAndStdoutLoggerNow(outpath FileAbs) (*gl.Logger, error) {
	nowStr := time.Now().Format(time.RFC3339)
	return NewFileAndStdoutLogger(MakeFileAbs(outpath.S + "_" + nowStr))
}
