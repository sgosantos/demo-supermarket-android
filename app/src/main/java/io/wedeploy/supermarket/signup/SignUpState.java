package io.wedeploy.supermarket.signup;

import io.wedeploy.supermarket.util.RequestState;

/**
 * @author Silvio Santos
 */
class SignUpState {

	public SignUpState(RequestState state) {
		this.state = state;
	}

	public SignUpState(RequestState state, Exception exception) {
		this.state = state;
		this.exception = exception;
	}

	public RequestState getState() {
		return state;
	}

	public Exception getException() {
		return exception;
	}

	private Exception exception;
	private RequestState state;
}
