package io.wedeploy.supermarket.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.databinding.ActivityLoginBinding;
import io.wedeploy.supermarket.products.ProductsActivity;
import io.wedeploy.supermarket.resetpassword.ResetPasswordActivity;
import io.wedeploy.supermarket.signup.SignUpActivity;
import io.wedeploy.supermarket.view.AlertMessage;

import static io.wedeploy.supermarket.util.ErrorUtil.getError;

/**
 * @author Silvio Santos
 */
public class LoginActivity extends AppCompatActivity implements LoginListener {

	@Override
	public void onLoginSuccess() {
		if (isFinishing()) return;

		startActivity(new Intent(this, ProductsActivity.class));
		finishAffinity();
	}

	@Override
	public void onLoginFailed(Throwable throwable) {
		if (isFinishing()) return;

		enableFields(true);
		binding.signInButton.setText(R.string.log_in);

		AlertMessage.showErrorMessage(
			this, getError(throwable, getString(R.string.could_not_login)));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

		binding.signUpButton.setText(getSignUpButtonText());
		binding.signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
				finish();
			}
		});

		binding.signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				enableFields(false);
				binding.signInButton.setText(R.string.logging_in);

				String email = binding.email.getText().toString();
				String password = binding.password.getText().toString();

				LoginRequest.login(LoginActivity.this, email, password);
			}
		});

		binding.forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(
					new Intent(LoginActivity.this, ResetPasswordActivity.class),
					REQUEST_RESET_PASSWORD);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == REQUEST_RESET_PASSWORD) && (resultCode == RESULT_OK)) {
			AlertMessage.showSuccessMessage(
				this, getString(R.string.the_email_should_arrive_within_a_few_minutes));
		}
	}

	private void enableFields(boolean enable) {
		binding.password.setEnabled(enable);
		binding.email.setEnabled(enable);
		binding.signInButton.setEnabled(enable);
	}

	private CharSequence getSignUpButtonText() {
		SpannableStringBuilder sb = new SpannableStringBuilder(
			getString(R.string.dont_you_have_an_account));

		ForegroundColorSpan colorSpan = new ForegroundColorSpan(
			ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));

		SpannableString signUpString = new SpannableString(getString(R.string.sign_up));
		signUpString.setSpan(colorSpan, 0, signUpString.length(), Spanned
			.SPAN_INCLUSIVE_INCLUSIVE);
		sb.append(" ");
		sb.append(signUpString);

		return sb;
	}

	private ActivityLoginBinding binding;
	private static final int REQUEST_RESET_PASSWORD = 1;

}
