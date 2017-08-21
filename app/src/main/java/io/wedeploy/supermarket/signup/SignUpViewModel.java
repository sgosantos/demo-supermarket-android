package io.wedeploy.supermarket.signup;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.wedeploy.android.transport.Response;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.repository.SupermarketAuth;

import static io.wedeploy.supermarket.util.State.*;

/**
 * @author Silvio Santos
 */
public class SignUpViewModel extends ViewModel {

	public SignUpViewModel() {
		signUpState = new MutableLiveData<>();
		signUpState.setValue(new SignUpState(IDLE));
	}

	public LiveData<SignUpState> getState() {
		return signUpState;
	}

	public void setIdleState() {
		signUpState.setValue(new SignUpState(IDLE));
	}

	public void signUp(final String email, final String password, String name) {
		final SupermarketAuth auth = SupermarketAuth.getInstance();
		auth.signUp(email, password, name)
			.asSingle()
			.flatMap(new Function<Response, SingleSource<Response>>() {
				@Override
				public SingleSource<Response> apply(@NonNull Response response) throws Exception {
					auth.saveUser(response);

					return auth.signIn(email, password)
						.asSingle()
						.subscribeOn(Schedulers.io());
				}
			})
			.subscribeOn(Schedulers.io())
			.doOnSuccess(new Consumer<Response>() {
				@Override
				public void accept(@NonNull Response response) throws Exception {
					auth.saveToken(response);
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new DisposableSingleObserver<Response>() {
				@Override
				public void onSuccess(Response response) {
					signUpState.setValue(new SignUpState(SUCCESS));
				}

				@Override
				public void onError(Throwable throwable) {
					signUpState.setValue(new SignUpState(FAILURE, new Exception(throwable)));
				}
			});
	}

	private MutableLiveData<SignUpState> signUpState;
}
