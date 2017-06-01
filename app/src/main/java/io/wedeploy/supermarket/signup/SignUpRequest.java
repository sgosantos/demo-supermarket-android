package io.wedeploy.supermarket.signup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.wedeploy.android.transport.Response;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.repository.SupermarketAuth;

/**
 * @author Silvio Santos
 */
public class SignUpRequest extends Fragment {

	public static void signUp(
		AppCompatActivity activity, String email, String password, String name) {

		SignUpRequest request = new SignUpRequest();
		request.setRetainInstance(true);
		request.email = email;
		request.password = password;
		request.name = name;

		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		transaction.add(request, TAG);
		transaction.commit();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof SignUpListener) {
			this.listener = (SignUpListener)context;
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
					removeRequest();

					listener.onSignUpSuccess();
				}

				@Override
				public void onError(Throwable throwable) {
					removeRequest();

					listener.onSignUpFailed(throwable);
				}
			});
	}

	private void removeRequest() {
		getActivity().getSupportFragmentManager()
			.beginTransaction()
			.remove(SignUpRequest.this)
			.commit();
	}

	private String email;
	private SignUpListener listener;
	private String name;
	private String password;
	private static final String TAG = SignUpRequest.class.getSimpleName();

}
