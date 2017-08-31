package io.wedeploy.supermarket.resetpassword;

import io.wedeploy.supermarket.util.RequestState;

/**
 * @author Silvio Santos
 */
public class ResetPasswordState {

	public ResetPasswordState(RequestState state) {
		this.state = state;
	}

	public ResetPasswordState(RequestState state, Exception exception) {
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
