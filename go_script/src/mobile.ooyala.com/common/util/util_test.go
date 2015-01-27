package util

import "testing"
import "strconv"
import "flag"
import . "encoding/hex"
import . "mobile.ooyala.com/common/path"
import "mobile.ooyala.com/common/log"

func check(t *testing.T, err error) {
	if err != nil {
		t.Log(err.Error())
		t.FailNow()
	}
}

func require(t *testing.T, err error) {
	if err == nil {
		t.Log(err.Error())
		t.FailNow()
	}
}

// ----------------------------------------

func Test_IsPathVisible(t *testing.T) {
	is := IsPathVisible(MakeDirAbs("/tmp/thisshouldbeaprettybogusfilename"))
	if is {
		t.FailNow()
	}
}

func Test_IsPathVisibleError(t *testing.T) {
	err := IsPathVisibleError(MakeDirAbs("/tmp/thisshouldbeaprettybogusfilename"))
	require(t, err)
}

// ----------------------------------------

func Test_RunBashCommandInDir_Junk(t *testing.T) {
	l := log.NewStdoutLogger()
	check(t, EnsureCleanDestinationPath(MakeDirAbs("/tmp/sdk_tmp"), l))
	_, err := RunBashCommandInDir(MakeDirAbs("/tmp/sdk_tmp"), "for me in 1 2 3 4 5; do echo $me `date +%s`; sleep 1; done", l)
	check(t, err)
}

func Test_RunBashCommandInDir_ExitCode1(t *testing.T) {
	l := log.NewStdoutLogger()
	_, err := RunBashCommandInDir(MakeDirAbs("/tmp"), "exit 1", l)
	require(t, err)
}

func Test_RunBashCommandInDir_ExitCode0(t *testing.T) {
	l := log.NewStdoutLogger()
	_, err := RunBashCommandInDir(MakeDirAbs("/tmp"), "exit 0", l)
	check(t, err)
}

// ----------------------------------------

func RunBashCommandsInDir_TwoExitCodesOneError(code1 int, code2 int) error {
	l := log.NewStdoutLogger()
	cmds := []string{
		"exit " + strconv.Itoa(code1),
		"exit " + strconv.Itoa(code2),
	}
	return RunBashCommandsInDir(MakeDirAbs("/tmp"), cmds, l)
}

func Test_RunBashCommandsInDir_ExitCode1_1(t *testing.T) {
	err := RunBashCommandsInDir_TwoExitCodesOneError(1, 1)
	require(t, err)
}

func Test_RunBashCommandsInDir_ExitCode0_1(t *testing.T) {
	err := RunBashCommandsInDir_TwoExitCodesOneError(0, 1)
	require(t, err)
}

func Test_RunBashCommandsInDir_ExitCode1_0(t *testing.T) {
	err := RunBashCommandsInDir_TwoExitCodesOneError(1, 0)
	require(t, err)
}

func Test_RunBashCommandsInDir_ExitCode0_0(t *testing.T) {
	err := RunBashCommandsInDir_TwoExitCodesOneError(0, 0)
	check(t, err)
}

// ----------------------------------------

func Test_CountFilesOf(t *testing.T) {
	l := log.NewStdoutLogger()
	_, err := RunBashCommandInDir(MakeDirAbs("/tmp"), "for me in 1 2 3 4 5; do touch ${me}.sdk_tmp_test; done", l)
	count, err := CountFilesOf(MakeDirAbs(`/tmp`), FindFilesPrefix+` -name \*sdk_tmp_test`, l)
	check(t, err)
	if count < 5 {
		t.FailNow()
	}
}

// ----------------------------------------

func Test_FlagSetToHelpString(t *testing.T) {
	flagSet := flag.NewFlagSet("test", flag.ExitOnError)
	flagSet.String("str", "default", "set me")
	s := FlagSetToHelpString(flagSet)
	e := "flag: str (set me)\n"
	if s != e {
		t.Log(s)
		t.Log(e)
		bs := []byte(s)
		t.Log(Dump(bs))
		be := []byte(e)
		t.Log(Dump(be))
		t.FailNow()
	}
}

// ----------------------------------------

func Test_DeletePath_Dir(t *testing.T) {
	l := log.NewStdoutLogger()
	d := MakeDirAbs("/tmp/sdk_test_foobar")
	check(t, EnsureCleanDestinationPath(d, l))
	check(t, DeletePath(d, l))
}
