package log

import "testing"
import "os"
import . "mobile.ooyala.com/common/path"

// yes, these are just smoke tests, not really unit tests.

func Test_SilentLogger(t *testing.T) {
	l := NewStdoutLogger()
	l.Println("log.foo")
}

func Test_StdoutLogger(t *testing.T) {
	l := NewStdoutLogger()
	l.Println("log.foo")
}

func Test_FilAndStdoutLogger(t *testing.T) {
	path := "/tmp/testing.logger_test.out"
	err := os.RemoveAll(path)
	if err != nil {
		t.FailNow()
	}
	l, err := NewFileAndStdoutLogger(MakeFileAbs(path))
	if err != nil {
		t.FailNow()
	}
	l.Println("log.foo")
}
