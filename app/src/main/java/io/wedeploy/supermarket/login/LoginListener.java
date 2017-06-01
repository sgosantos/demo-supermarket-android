package io.wedeploy.supermarket.login;

/**
 * @author Silvio Santos
 */
interface LoginListener {

	void onLoginSuccess();

	void onLoginFailed(Throwable throwable);

}
