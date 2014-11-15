package util

import "fmt"
import "log"
import "os"
import "regexp"
import "os/exec"
import "strconv"
import "strings"
import "reflect"
import "flag"
import "bytes"
import "bufio"
import "unicode"
import gpfp "path/filepath"
import . "mobile.ooyala.com/build/common/path"

type RunAction func() error
type VisitFieldFn func(name string, field reflect.Value) error

const FindFilesRecursivePrefix = "find . -type f"
const FindFilesPrefix = "find . -type f -maxdepth 1"

func Run(actions []RunAction) error {
	for _, fn := range actions {
		err := fn()
		if err != nil {
			return NewErrorWithErrorAndStack(err)
		}
	}
	return nil
}

func Die(err error, logger *log.Logger) {
	if err != nil {
		msg := "!!! util.Die " + err.Error()
		if logger != nil {
			logger.Println(msg)
		} else {
			fmt.Fprintf(os.Stderr, msg)
		}
	}
	os.Exit(1)
}

func MaybeDie(err error, logger *log.Logger) {
	if err != nil {
		msg := "!!! " + err.Error()
		if logger != nil {
			logger.Println(msg)
		} else {
			fmt.Fprintf(os.Stderr, msg)
		}
		os.Exit(1)
	}
}

func ToDirAbs(d DirRel) (DirAbs, error) {
	rootStr, err := gpfp.Abs(d.S)
	if err != nil {
		return MakeDirAbs(""), err
	}
	return MakeDirAbs(rootStr), nil
}

func RequireCountFilesRecursive(path DirAbs, expected int, logger *log.Logger) error {
	return RequireCountFilesOf(CountFilesRecursive, path, expected, logger)
}

func RequireCountFiles(path DirAbs, expected int, logger *log.Logger) error {
	return RequireCountFilesOf(CountFiles, path, expected, logger)
}

// todo: add a RequireCountFiles{Recursive}Named()
type CounterAction func(DirAbs, *log.Logger) (int, error)

func RequireCountFilesOf(counter CounterAction, path DirAbs, expected int, logger *log.Logger) error {
	if !IsPathVisible(path) {
		return NewErrorWithMessageAndStack("path should be visible: " + path.S)
	}
	count, err := counter(path, logger)
	if err != nil {
		return NewErrorWithErrorAndStack(err)
	}
	if count != expected {
		return NewErrorWithMessageAndStack("wrong count, expected " + strconv.Itoa(expected) + ", got " + strconv.Itoa(count))
	}
	return nil
}

func RequirePaths(pathers []Pather, logger *log.Logger) error {
	logger.Println("util.RequirePaths")
	for _, e := range pathers {
		err := RequirePath(e, logger)
		if err != nil {
			return NewErrorWithErrorAndStack(err)
		}
	}
	return nil
}

func RequirePath(pather Pather, logger *log.Logger) error {
	path := pather.Path()
	logger.Println("util.RequirePath: " + path.S)
	if !IsPathVisible(path) {
		return NewErrorWithMessageAndStack("path should be visible: " + path.S)
	} else {
		return nil
	}
}

func RequireNoPaths(paths []Pather, logger *log.Logger) error {
	logger.Println("util.RequireNoPaths")
	for _, e := range paths {
		err := RequireNoPath(e, logger)
		if err != nil {
			return NewErrorWithErrorAndStack(err)
		}
	}
	return nil
}

func RequireNoPath(pather Pather, logger *log.Logger) error {
	path := pather.Path()
	logger.Println("util.RequireNoPath: " + path.S)
	if IsPathVisible(path) {
		return NewErrorWithMessageAndStack("path should not be visible: " + path.S)
	} else {
		return nil
	}
}

func DeletePath(pather Pather, logger *log.Logger) error {
	path := pather.Path()
	logger.Println("util.DeletePath: " + path.S)
	if !IsPathVisible(path) {
		return nil
	}
	err := os.RemoveAll(path.S)
	if err != nil {
		return NewErrorWithErrorAndStack(err)
	}
	return RequireNoPath(path, logger)
}

func EnsurePaths(paths []DirAbs, logger *log.Logger) error {
	for _, e := range paths {
		err := EnsurePath(e, logger)
		if err != nil {
			return NewErrorWithErrorAndStack(err)
		}
	}
	return nil
}

func EnsurePath(path DirAbs, logger *log.Logger) error {
	logger.Println("util.EnsurePath: " + path.S)
	if IsPathVisible(path) {
		return nil
	}
	err := os.MkdirAll(path.S, os.ModeDir|os.ModePerm)
	if err != nil {
		return NewErrorWithErrorAndStack(err)
	}
	return RequirePath(path, logger)
}

func DeleteFile(path FileAbs, logger *log.Logger) error {
	logger.Println("util.DeleteFile: " + path.S)
	if IsPathVisible(path) {
		err := os.Remove(path.S)
		if err != nil {
			return NewErrorWithErrorAndStack(err)
		}
	}
	return nil
}

func EnsureCleanDestinationPath(path DirAbs, logger *log.Logger) error {
	logger.Println("util.EnsureCleanDestinationPath: " + path.S)
	err := Run([]RunAction{
		func() error { return DeletePath(path, logger) },
		func() error { return os.MkdirAll(path.S, os.ModeDir|os.ModePerm) },
	})
	if err != nil {
		return NewErrorWithErrorAndStack(err)
	}
	return RequirePath(path, logger)
}

func CopyPath(srcPather Pather, dstPather Pather, logger *log.Logger) error {
	logger.Println("util.CopyPath", srcPather, dstPather)
	_, err := RunBashCommand(`cp `+srcPather.Path().S+` `+dstPather.Path().S, logger)
	return err
}

func CopyPathLiteral(srcPath string, dstPather Pather, logger *log.Logger) error {
	logger.Println("util.CopyPathLiteral", srcPath, dstPather)
	_, err := RunBashCommand(`cp `+srcPath+` `+dstPather.Path().S, logger)
	return err
}

func CopyPathRecursive(srcPather Pather, dstPather Pather, logger *log.Logger) error {
	logger.Println("util.CopyPathRecursive", srcPather, dstPather)
	_, err := RunBashCommand(`cp -R `+srcPather.Path().S+` `+dstPather.Path().S, logger)
	return err
}

func CopyPathContents(srcPather Pather, dstPather Pather, logger *log.Logger) error {
	logger.Println("util.CopyPathContents", srcPather, dstPather)
	_, err := RunBashCommand(`cp `+srcPather.Path().S+`/* `+dstPather.Path().S+`/`, logger)
	return err
}

func CopyPathContentsRecursive(srcPather Pather, dstPather Pather, logger *log.Logger) error {
	logger.Println("util.CopyPathContentsRecursive", srcPather, dstPather)
	_, err := RunBashCommand(`cp -R `+srcPather.Path().S+`/* `+dstPather.Path().S+`/`, logger)
	return err
}

// todo: fix up the arbitrary return type differences among RunBash*()'s.

func RunBashCommandParts(parts []string, logger *log.Logger) (string, error) {
	return RunBashCommand(strings.Join(parts, " "), logger)
}

func RunBashCommand(command string, logger *log.Logger) (string, error) {
	logger.Println("util.RunBashCommand", command)
	out, err := exec.Command("bash", "-c", command).Output()
	if err != nil {
		err = NewErrorWithErrorAndStack(err)
	}
	return strings.TrimRightFunc(string(out), unicode.IsSpace), err
}

func RunBashCommandPartsInDir(dir DirAbs, parts []string, logger *log.Logger) (string, error) {
	return RunBashCommandInDir(dir, strings.Join(parts, " "), logger)
}

func RunBashCommandInDir(dir DirAbs, command string, logger *log.Logger) (string, error) {
	logger.Println("util.RunBashCommandInDir")
	return RunBashCommand("cd "+dir.S+"&& "+command, logger)
}

// if you really need the full output returned, then write your own loop somewhere.
func RunBashCommandsInDir(dir DirAbs, commands []string, logger *log.Logger) error {
	for _, c := range commands {
		cd := "cd " + dir.S
		full_cmd := cd + " && " + c
		logger.Println("util.RunBashCommandsInDir:", full_cmd)
		exec := exec.Command("bash", "-c", full_cmd)
		// todo: figure out something better some day.
		exec.Stdout = os.Stdout
		exec.Stderr = os.Stderr
		err := exec.Run()
		if err != nil {
			return NewErrorWithErrorAndStack(err)
		}
	}
	return nil
}

func IsPathVisible(pather Pather) bool {
	return IsPathVisibleError(pather) == nil
}

func IsPathVisibleError(pather Pather) error {
	path := pather.Path()
	_, err := os.Stat(path.S)
	return err
}

func MakeFindCommand(findSrcCmd string, findDstCmd string) string {
	return "if [ `" + findSrcCmd + " | wc -l` == `" + findDstCmd + " | wc -l` ]; then exit 0; else exit 1; fi"
}

func RequireEqualCount(runPath DirAbs, findSrcCmd string, findDstCmd string, logger *log.Logger) error {
	cmd := MakeFindCommand(findSrcCmd, findDstCmd)
	_, err := RunBashCommandInDir(runPath, cmd, logger)
	return err
}

func CountFilesRecursive(path DirAbs, logger *log.Logger) (int, error) {
	return CountFilesOf(path, FindFilesRecursivePrefix, logger)
}

func CountFiles(path DirAbs, logger *log.Logger) (int, error) {
	return CountFilesOf(path, FindFilesPrefix, logger)
}

func CountFilesOf(path DirAbs, find string, logger *log.Logger) (int, error) {
	fullCommand := "cd " + path.S + " && " + find + " | wc -l"
	cmdexec := exec.Command("bash", "-c", fullCommand)
	logger.Println(fullCommand)
	out, err := cmdexec.Output()
	if err != nil {
		return 0, NewErrorWithErrorAndStack(err)
	}
	sout := strings.TrimSpace(string(out))
	count64, err := strconv.ParseInt(sout, 10, 0)
	if err != nil {
		return 0, NewErrorWithErrorAndStack(err)
	}
	logger.Println(sout)
	return int(count64), nil
}

func GrepFile(path FileAbs, query string) bool {
	regex, err := regexp.Compile(query)
	if err != nil {
		return false
	}
	fh, err := os.Open(path.S)
	if err != nil {
		return false
	}
	defer fh.Close()

	scanner := bufio.NewScanner(fh)
	if scanner == nil {
		return false
	}
	for scanner.Scan() {
		if err := scanner.Err(); err != nil {
			return false
		}
		if regex.MatchString(scanner.Text()) {
			return true
		}
	}
	return false
}

// note: in the end this doesn't really fit what i wanted
// to do, since really the only way to know if a
// struct field has/not been set is to have them
// all be pointers and check vs. nil. which then
// sucks for consumers of the struct..
func RequireFullStructOrDie(x interface{}, logger *log.Logger, ignore ...string) {
	MaybeDie(RequireFullStruct(x, ignore...), logger)
}

func RequireFullStruct(x interface{}, ignore ...string) error {
	vfn := func(name string, value reflect.Value) error {
		if IsZero(value) {
			return NewErrorWithMessageAndStack("zero field: " + name + ", " + value.String())
		} else {
			return nil
		}
	}
	return visitStruct(x, vfn, ignore...)
}

// todo: this is only for non-pointer structs at the moment!?
func visitStruct(x interface{}, vfn VisitFieldFn, ignore ...string) error {
	//fmt.Println( "+ util.visitStruct: struct = ", x )
	_v := func(x interface{}, vfn VisitFieldFn, ignore ...string) error {
		s := reflect.ValueOf(x)
		if s.Kind() != reflect.Struct {
			return NewErrorWithMessageAndStack("not a struct")
		}
		for i := 0; i < s.NumField(); i++ {
			f := s.Field(i)
			name := s.Type().Field(i).Name
			//fmt.Println( "  util.visitStruct: field name =", name )
			skip := false
			for _, e := range ignore {
				if name == e {
					skip = true
				}
			}

			if !skip {
				err := vfn(name, f)
				if err != nil {
					return err
				}
			}
		}
		return nil
	}
	err := _v(x, vfn, ignore...)
	//fmt.Println( "- util.visitStruct" )
	return err
}

// http://code.google.com/p/go/issues/detail?id=7501
func IsZero(v reflect.Value) bool {
	//fmt.Println( "util.IsZero", v, v.Kind() )
	izi := reflect.TypeOf((*IsZeroer)(nil)).Elem()
	if v.Type().Implements(izi) {
		in := make([]reflect.Value, 0)
		return v.MethodByName("IsZero").Call(in)[0].Bool()
	} else {
		switch v.Kind() {
		case reflect.Array, reflect.String:
			return v.Len() == 0
		case reflect.Bool:
			return !v.Bool()
		case reflect.Int, reflect.Int8, reflect.Int16, reflect.Int32, reflect.Int64:
			return v.Int() == 0
		case reflect.Uint, reflect.Uint8, reflect.Uint16, reflect.Uint32, reflect.Uint64, reflect.Uintptr:
			return v.Uint() == 0
		case reflect.Float32, reflect.Float64:
			return v.Float() == 0
		case reflect.Interface, reflect.Map, reflect.Ptr, reflect.Slice:
			return v.IsNil()
		case reflect.Struct:
			hasZero := false
			vfn := func(name string, field reflect.Value) error {
				//fmt.Println( "util.IsZero.vfn", hasZero, name, field )
				if IsZero(field) {
					hasZero = true
				}
				return nil
			}
			MaybeDie(
				visitStruct(v.Interface(), vfn),
				nil,
			)
			return hasZero
		default:
			return false
		}
	}
}

func FlagSetToHelpString(flagSet *flag.FlagSet) string {
	var b bytes.Buffer
	flagSet.VisitAll(func(f *flag.Flag) { b.WriteString("flag: " + f.Name + " (" + f.Usage + ")\n") })
	return b.String()
}

func BashEscape(src string, escapee string) string {
	return strings.Replace(src, escapee, `\`+escapee, -1)
}
