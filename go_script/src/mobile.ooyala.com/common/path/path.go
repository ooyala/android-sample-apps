package path

import gp "path"
import gpfp "path/filepath"
import "os"
import "fmt"
import "regexp"
import "runtime/debug"

// todo: use embedding to make them all Fragments and maybe then Paths (for Base)?
// i could never get / figure out how to get embedding to work right for me.

// too bad there isn't a 'to string' type interface that's part of core go?
type Stringer interface {
	String() string
}

// todo: versions with different return types; 'string' is just lame.
// maybe move Join to be on the different types below?
func Join(args ...Stringer) string {
	s := make([]string, len(args))
	for i, e := range args {
		s[i] = e.String()
	}
	return gp.Join(s...)
}

type Pather interface {
	Stringer
	Path() Path
}

// ----------------------------------------

func RequireNoWhitespace(s string) string {
	if HasWhitespace(s) {
		stack := string(debug.Stack())
		fmt.Fprintf(os.Stderr, `no whitespace allowed: '`+s+`'`+"\n")
		fmt.Fprintf(os.Stderr, `call stack: `+stack)
		os.Exit(1)
	}
	return s
}

func HasWhitespace(s string) bool {
	m, err := regexp.MatchString("[[:space:]]", s)
	if err != nil {
		fmt.Fprintf(os.Stderr, err.Error())
		os.Exit(1)
	}
	return m
}

// ----------------------------------------

// Fragment is anything that doesn't fit into the types below -- could be a sub-part of a name, sub-part of a directory name, a glob, etc.

type Fragment struct {
	S string
}

func MakeFragment(s string) Fragment {
	return MakeFragmentAllowWhitespace(RequireNoWhitespace(s))
}
func MakeFragmentAllowWhitespace(s string) Fragment {
	return Fragment{S: s}
}
func (this Fragment) Path() Path {
	return MakePath(this.S)
}
func (this Fragment) String() string {
	return this.S
}

// ----------------------------------------

// could be a directory or a file, sometimes we don't need to restrict it.

type Path struct {
	Fragment
}

func MakePath(s string) Path {
	return MakePathAllowWhitespace(RequireNoWhitespace(s))
}
func MakePathAllowWhitespace(s string) Path {
	o := Path{}
	o.S = s
	return o
}

// note: this is assuming we can make a path from our string.
func (this Path) Base() Fragment {
	return MakeFragment(gpfp.Base(this.String()))
}

// ----------------------------------------

type FileName struct {
	Fragment
}

func MakeFileName(s string) FileName {
	return MakeFileNameAllowWhitespace(RequireNoWhitespace(s))
}
func MakeFileNameAllowWhitespace(s string) FileName {
	o := FileName{}
	o.S = s
	return o
}
func (this FileName) Base() Fragment {
	return MakeFragment(this.S)
}

type FileRel struct {
	Fragment
}

func MakeFileRel(s string) FileRel {
	return MakeFileRelAllowWhitespace(RequireNoWhitespace(s))
}
func MakeFileRelAllowWhitespace(s string) FileRel {
	o := FileRel{}
	o.S = s
	return o
}
func (this FileRel) Base() Fragment {
	return MakeFragment(gpfp.Base(this.S))
}

type FileAbs struct {
	Fragment
}

func MakeFileAbs(s string) FileAbs {
	return MakeFileAbsAllowWhitespace(RequireNoWhitespace(s))
}
func MakeFileAbsAllowWhitespace(s string) FileAbs {
	o := FileAbs{}
	o.S = s
	return o
}
func (this FileAbs) Base() Fragment {
	return MakeFragment(gpfp.Base(this.S))
}
func (this FileAbs) Dir() DirAbs {
	return MakeDirAbs(gpfp.Dir(this.S))
}

// ----------------------------------------

type DirName struct {
	Fragment
}

func MakeDirName(s string) DirName {
	return MakeDirNameAllowWhitespace(RequireNoWhitespace(s))
}
func MakeDirNameAllowWhitespace(s string) DirName {
	o := DirName{}
	o.S = s
	return o
}
func (this DirName) Base() Fragment {
	return MakeFragment(this.S)
}

type DirRel struct {
	Fragment
}

func MakeDirRel(s string) DirRel {
	return MakeDirRelAllowWhitespace(RequireNoWhitespace(s))
}
func MakeDirRelAllowWhitespace(s string) DirRel {
	o := DirRel{}
	o.S = s
	return o
}
func (this DirRel) Base() Fragment {
	return MakeFragment(gpfp.Base(this.S))
}

type DirAbs struct {
	Fragment
}

func MakeDirAbs(s string) DirAbs {
	return MakeDirAbsAllowWhitespace(RequireNoWhitespace(s))
}
func MakeDirAbsAllowWhitespace(s string) DirAbs {
	o := DirAbs{}
	o.S = s
	return o
}
func (this DirAbs) Base() Fragment {
	return MakeFragment(gpfp.Base(this.S))
}
