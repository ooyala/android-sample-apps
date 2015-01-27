package util

import "os"
import "strings"
import "fmt"

func DbcPre(b bool, msgs ...string) {
	dbcAssert(b, "pre-condition", msgs)
}

func DbcPost(b bool, msgs ...string) {
	dbcAssert(b, "post-condition", msgs)
}

func dbcAssert(b bool, dbcType string, msgs []string) {
	if !b {
		msg := strings.Join(msgs, ", ")
		fullmsg := fmt.Sprintf("failed %s: %s\n", dbcType, msg)
		fmt.Fprintf(os.Stderr, fullmsg)
		panic(fullmsg)
	}
}
