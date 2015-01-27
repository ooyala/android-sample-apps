package log

import gl "log"

type SilentWriter struct {
}

func (this *SilentWriter) Write(p []byte) (n int, err error) {
	return len(p), nil
}

func NewSilentLogger() *gl.Logger {
	silentWriter := &SilentWriter{}
	return gl.New(silentWriter, "", gl.LstdFlags)
}
