package io.wedeploy.supermarket.resetpassword;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.wedeploy.android.transport.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.repository.SupermarketAuth;

import static io.wedeploy.supermarket.util.RequestState.FAILURE;
import static io.wedeploy.supermarket.util.RequestState.IDLE;
import static io.wedeploy.supermarket.util.RequestState.SUCCESS;

/**
 * @author Silvio Santos
 */
public class ResetPasswordViewModel extends ViewModel {

	public ResetPasswordViewModel() {
		resetPasswordState = new MutableLiveData<>();
		resetPasswordState.setValue(new ResetPasswordState(IDLE));
	}

	public LiveData<ResetPasswordState> getState() {
		return resetPasswordState;
	}

	public void resetPassword(String email) {
		SupermarketAuth auth = SupermarketAuth.getInstance();
		auth.resetPassword(email)
			.asSingle()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Consumer<Response>() {
				@Override
				public void accept(@NonNull Response response) throws Exception {
					resetPasswordState.setValue(new ResetPasswordState(SUCCESS));
				}
			}, new Consumer<Throwable>() {
				@Override
				public void accept(@NonNull Throwable throwable) throws Exception {
					resetPasswordState.setValue(new ResetPasswordState(FAILURE, new Exception(throwable)));
				}
			});
	}

	public void setIdleState() {
		resetPasswordState.setValue(new ResetPasswordState(IDLE));
	}

	private final MutableLiveData<ResetPasswordState> resetPasswordState;

}
