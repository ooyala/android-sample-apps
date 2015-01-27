package path

import "testing"
import "errors"
import gp "path"

func Test_HasWhitespace(t *testing.T) {
	tests := []string{
		"foo bar",
		"foo\tbar",
		"foo\vbar",
		"foo\nbar",
		"foo\rbar",
	}
	for _, s := range tests {
		if !HasWhitespace(s) {
			t.Log(s)
			t.FailNow()
		}
	}
}

// todo: i want this to be a compiler error!
func Test_PathArgument(t *testing.T) {
	f := func(p Path) {}
	f(MakePath("foo"))
}

func Test_Join(t *testing.T) {
	pjs := Join(
		MakeFragment("a"),
		MakePath("b"),
		MakeFileName("c"),
		MakeFileRel("d"),
		MakeFileAbs("/e"),
		MakeDirName("f"),
		MakeDirRel("g"),
		MakeDirAbs("/h"),
	)
	gpjs := gp.Join(
		"a",
		"b",
		"c",
		"d",
		"/e",
		"f",
		"g",
		"/h",
	)
	if pjs != gpjs {
		t.Log(errors.New("expected: '" + gpjs + "', got '" + pjs + "'"))
		t.FailNow()
	}
}
