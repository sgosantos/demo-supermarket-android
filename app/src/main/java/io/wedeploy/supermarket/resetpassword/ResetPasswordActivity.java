package io.wedeploy.supermarket.resetpassword;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.databinding.ActivityResetPasswordBinding;
import io.wedeploy.supermarket.view.AlertMessage;

/**
 * @author Silvio Santos
 */
public class ResetPasswordActivity extends AppCompatActivity implements LifecycleRegistryOwner {

	LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	@Override
	public LifecycleRegistry getLifecycle() {
		return lifecycleRegistry;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		resetPasswordViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel.class);
		resetPasswordViewModel.getState()
			.observe(this, new Observer<ResetPasswordState>() {
				@Override
				public void onChanged(@Nullable ResetPasswordState state) {
					setResetPasswordState(state);
				}
			});

		binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
		binding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				enableFields(false);
				binding.resetPasswordButton.setText(R.string.sending_reset_instructions);

				resetPasswordViewModel.resetPassword(binding.editText.getText().toString());
			}
		});

	}

	private void setResetPasswordState(ResetPasswordState state) {
		switch (state.getState()) {
			case IDLE:
				enableFields(true);
				break;

			case SUCCESS:
				setResult(RESULT_OK);
				finish();
				break;

			case FAILURE:
				resetPasswordViewModel.setIdleState();
				binding.resetPasswordButton.setText(R.string.send_reset_instructions);

				AlertMessage.showErrorMessage(this, getString(R.string.invalid_email));
				break;

			case LOADING:
				break;
		}
	}

	private void enableFields(boolean enable) {
		binding.editText.setEnabled(enable);
		binding.resetPasswordButton.setEnabled(enable);
	}

	private ActivityResetPasswordBinding binding;

	private ResetPasswordViewModel resetPasswordViewModel;
}
