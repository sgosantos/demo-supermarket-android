package io.wedeploy.supermarket.resetpassword;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.wedeploy.android.Callback;
import com.wedeploy.android.transport.Response;
import io.wedeploy.supermarket.repository.SupermarketAuth;

/**
 * @author Silvio Santos
 */
public class ResetPasswordRequest extends Fragment {

	public ResetPasswordRequest() {
		setRetainInstance(true);
	}

	public static void resetPassword(AppCompatActivity activity, String email) {
		ResetPasswordRequest request = new ResetPasswordRequest();
		request.email = email;

		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		transaction.add(request, TAG);
		transaction.commit();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof ResetPasswordListener) {
			this.listener = (ResetPasswordListener)context;
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SupermarketAuth auth = SupermarketAuth.getInstance();
		auth.resetPassword(email)
			.execute(new Callback() {
				@Override
				public void onSuccess(Response response) {
					removeRequest();

					listener.onResetPasswordSuccess();
				}

				@Override
				public void onFailure(Exception e) {
					removeRequest();

					listener.onResetPasswordFailed(e);
				}
			});
	}

	private void removeRequest() {
		getActivity().getSupportFragmentManager()
			.beginTransaction()
			.remove(ResetPasswordRequest.this)
			.commit();
	}

	private String email;
	private ResetPasswordListener listener;

	private static final String TAG = ResetPasswordRequest.class.getSimpleName();

}
