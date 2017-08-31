package io.wedeploy.supermarket.signup;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.databinding.ActivitySignUpBinding;
import io.wedeploy.supermarket.login.LoginActivity;
import io.wedeploy.supermarket.products.ProductsActivity;
import io.wedeploy.supermarket.view.AlertMessage;

import static io.wedeploy.supermarket.util.ErrorUtil.getError;

public class SignUpActivity extends AppCompatActivity implements LifecycleRegistryOwner {

	LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	@Override
	public LifecycleRegistry getLifecycle() {
		return lifecycleRegistry;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
		binding.logInButton.setText(getLogInButtonText());
		binding.logInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
				finish();
			}
		});

		signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
		signUpViewModel.getState()
			.observe(this, new Observer<SignUpState>() {
				@Override
				public void onChanged(@Nullable SignUpState state) {
					setSignUpState(state);
				}
			});

		setupStepButtons();
	}

	private void enableFields(boolean enable) {
		binding.name.setEnabled(enable);
		binding.email.setEnabled(enable);
		binding.password.setEnabled(enable);
		binding.steps.getNextButton().setEnabled(enable);
		binding.steps.getPreviousButton().setEnabled(enable);
	}

	private CharSequence getLogInButtonText() {
		SpannableStringBuilder sb = new SpannableStringBuilder(
			getString(R.string.already_have_an_account));

		ForegroundColorSpan colorSpan = new ForegroundColorSpan(
			ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));

		SpannableString signUpString = new SpannableString(getString(R.string.log_in));
		signUpString.setSpan(colorSpan, 0, signUpString.length(), Spanned
			.SPAN_INCLUSIVE_INCLUSIVE);
		sb.append(" ");
		sb.append(signUpString);

		return sb;
	}

	private void setSignUpState(SignUpState state) {
		switch (state.getState()) {

			case IDLE:
				enableFields(true);
				break;

			case SUCCESS:
				finishAffinity();
				startActivity(new Intent(this, ProductsActivity.class));
				break;

			case FAILURE:
				signUpViewModel.setIdleState();

				binding.steps.getNextButton().setText(R.string.sign_up);

				AlertMessage.showErrorMessage(
					this, getError(state.getException(), getString(R.string.could_not_sign_up)));

				break;
			case LOADING:
				break;
		}
	}

	private void setupStepButtons() {
		binding.steps.setOnDoneClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				enableFields(false);
				binding.steps.getNextButton().setText(R.string.signing_up);

				String email = binding.email.getText().toString();
				String password = binding.password.getText().toString();
				String name = binding.name.getText().toString();

				signUpViewModel.signUp(email, password, name);
			}
		});
	}

	private ActivitySignUpBinding binding;
	private SignUpViewModel signUpViewModel;

}
