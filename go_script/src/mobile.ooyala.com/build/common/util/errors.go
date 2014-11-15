package util

import "runtime/debug"
import "errors"

func NewErrorWithMessageAndStack(msg string) error {
	return errors.New(msg + " @ " + string(debug.Stack()))
}

func NewErrorWithErrorAndStack(err error) error {
	var msg string
	if err != nil {
		msg = err.Error()
	} else {
		msg = ""
	}
	return errors.New(msg + " @ " + string(debug.Stack()))
}

type notImplementedError struct {
	Stack string
}

func NewNotImplementedError() *notImplementedError {
	return &notImplementedError{string(debug.Stack())}
}
func (this *notImplementedError) Error() string {
	return "not implemented: " + this.Stack
}
