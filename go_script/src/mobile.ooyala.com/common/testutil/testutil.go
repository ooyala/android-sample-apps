package testutil

import "testing"
import "fmt"
import "os/exec"
import "runtime/debug"
import . "mobile.ooyala.com/common/path"
import "mobile.ooyala.com/common/util"
import "mobile.ooyala.com/common/log"

type setupfn func(t *testing.T)

func ErrorNow(t *testing.T, err error) {
	t.Log(string(debug.Stack()))
	t.Fatalf(err.Error())
}

func MaybeErrorNow(t *testing.T, err error) error {
	if err != nil {
		ErrorNow(t, err)
	}
	return util.NewErrorWithErrorAndStack(err)
}

func RequirePath(t *testing.T, path Pather) {
	MaybeErrorNow(t, util.RequirePath(path, log.NewSilentLogger()))
}

func RequireNoPath(t *testing.T, path Pather) {
	MaybeErrorNow(t, util.RequireNoPath(path, log.NewSilentLogger()))
}

func DeletePath(t *testing.T, path DirAbs) {
	MaybeErrorNow(t, util.DeletePath(path, log.NewSilentLogger()))
}

func EnsurePath(t *testing.T, path DirAbs) {
	MaybeErrorNow(t, util.EnsurePath(path, log.NewSilentLogger()))
}

func EnsureCleanDestinationTest(t *testing.T, dirpath DirAbs, setupfn setupfn) {
	fmt.Printf("testutil.EnsureCleanDestinationTest", dirpath)
	DeletePath(t, dirpath)
	EnsurePath(t, dirpath)

	dummyPath := MakeDirAbs(Join(dirpath, MakeFileName("dummy")))
	cmdstr := "touch " + dummyPath.S
	fmt.Printf(cmdstr)
	MaybeErrorNow(t, exec.Command("bash", "-c", cmdstr).Run())
	if !util.IsPathVisible(dummyPath) {
		ErrorNow(t, util.NewErrorWithMessageAndStack("failed to create dummy: "+dummyPath.S))
	}

	setupfn(t)

	if !util.IsPathVisible(dirpath) {
		ErrorNow(t, util.NewErrorWithMessageAndStack("parent not visible: "+dirpath.S))
	}
	if util.IsPathVisible(dummyPath) {
		ErrorNow(t, util.NewErrorWithMessageAndStack("failed to erase (post) dummy : "+dummyPath.S))
	}

	DeletePath(t, dirpath)
}
