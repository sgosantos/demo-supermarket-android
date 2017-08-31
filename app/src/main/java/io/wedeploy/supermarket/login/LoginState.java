package io.wedeploy.supermarket.login;

import io.wedeploy.supermarket.util.RequestState;

/**
 * @author Silvio Santos
 */
public class LoginState {

	public LoginState(RequestState state) {
		this.state = state;
	}

	public LoginState(RequestState state, Exception exception) {
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
