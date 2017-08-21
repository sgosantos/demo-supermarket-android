package io.wedeploy.supermarket.signup;

import io.wedeploy.supermarket.util.State;

/**
 * @author Silvio Santos
 */
class SignUpState {

	public SignUpState(State state) {
		this.state = state;
	}

	public SignUpState(State state, Exception exception) {
		this.state = state;
		this.exception = exception;
	}

	public State getState() {
		return state;
	}

	public Exception getException() {
		return exception;
	}

	private Exception exception;
	private State state;
}
