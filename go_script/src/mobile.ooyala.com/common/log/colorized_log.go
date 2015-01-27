package log

import gl "log"
import "runtime"

func ColorizedPrintln(l *gl.Logger, s string) {
	l.Println("\x1b[31;1m" + s + "\x1b[0m")
}

func ColorizedMethodPrintln(l *gl.Logger) {
	ColorizedMethodMessagePrintln(l, "")
}

func ColorizedMethodMessagePrintln(l *gl.Logger, s string) {
	pc, _, _, ok := runtime.Caller(2)
	printed := false
	if( ok ) {
		f := runtime.FuncForPC(pc)
		if( f != nil ) {
			callerName := f.Name()
			ColorizedPrintln(l, "(in " + callerName + ") " + s)
			printed = true
		}
	}
	if(!printed) {
		ColorizedPrintln(l, s)
	}
}
