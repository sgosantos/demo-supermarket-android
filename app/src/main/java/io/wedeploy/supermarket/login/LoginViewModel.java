package io.wedeploy.supermarket.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.wedeploy.android.auth.TokenAuthorization;
import com.wedeploy.android.transport.Response;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.repository.SupermarketAuth;

import static io.wedeploy.supermarket.util.RequestState.*;

/**
 * @author Silvio Santos
 */
public class LoginViewModel extends ViewModel {

	public LoginViewModel() {
		loginState = new MutableLiveData<>();
		loginState.setValue(new LoginState(IDLE));
	}

	public LiveData<LoginState> getLoginState() {
		return loginState;
	}

	public void login(String email, String password) {
		loginState.setValue(new LoginState(LOADING));

		doLogin(email, password);
	}

	public void setIdleState() {
		loginState.setValue(new LoginState(IDLE));
	}

	private void doLogin(String email, String password) {
		final SupermarketAuth auth = SupermarketAuth.getInstance();
		auth.signIn(email, password)
			.asSingle()
			.subscribeOn(Schedulers.io())
			.flatMap(new Function<Response, SingleSource<? extends Response>>() {
				@Override
				public SingleSource<? extends Response> apply(@NonNull Response response)
					throws Exception {
					String token = auth.saveToken(response);

					return auth.getUser(new TokenAuthorization(token))
						.asSingle()
						.subscribeOn(Schedulers.io());
				}
			})
			.doOnSuccess(new Consumer<Response>() {
				@Override
				public void accept(@NonNull Response response) throws Exception {
					auth.saveUser(response);
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new DisposableSingleObserver<Response>() {
				@Override
				public void onSuccess(Response response) {
					loginState.setValue(new LoginState(SUCCESS));
				}

				@Override
				public void onError(Throwable throwable) {
					loginState.setValue(new LoginState(FAILURE, new Exception(throwable)));
				}
			});
	}

	private MutableLiveData<LoginState> loginState;

}
