package io.wedeploy.supermarket.resetpassword;

import io.wedeploy.supermarket.util.State;

/**
 * @author Silvio Santos
 */
public class ResetPasswordState {

	public ResetPasswordState(State state) {
		this.state = state;
	}

	public ResetPasswordState(State state, Exception exception) {
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
