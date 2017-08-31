package io.wedeploy.supermarket.cart;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.wedeploy.android.Callback;
import com.wedeploy.android.transport.Response;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.cart.adapter.CartAdapter;
import io.wedeploy.supermarket.cart.model.CartProduct;
import io.wedeploy.supermarket.databinding.ActivityCartBinding;
import io.wedeploy.supermarket.repository.Settings;
import io.wedeploy.supermarket.repository.SupermarketEmail;

import java.util.List;

/**
 * @author Silvio Santos
 */
public class CartActivity extends AppCompatActivity
	implements DeleteFromCartListener, LifecycleRegistryOwner {

	LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	@Override
	public void onDeleteFromCart(CartProduct cartProduct) {
		DeleteCartItemRequest.delete(this, cartProduct.getId());

		if (adapter.getItemCount() == 0) {
			showEmptyCart();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public LifecycleRegistry getLifecycle() {
		return lifecycleRegistry;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
		binding.cartList.setAdapter(adapter);

		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				updateCheckoutAmount();
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				updateCheckoutAmount();
			}
		});

		showLoading();
		ViewModelProviders.of(this).get(CartViewModel.class)
			.getCart()
			.observe(this, new Observer<List<CartProduct>>() {
				@Override
				public void onChanged(@Nullable List<CartProduct> cartProducts) {
					showCartProducts(cartProducts);
				}
			});
	}

	private void sendCheckoutEmail() {
		SupermarketEmail.getInstance()
			.sendCheckoutEmail(
				Settings.getUserName(),
				Settings.getUserEmail(),
				adapter.getCartProducts())
			.execute(new Callback() {
				@Override
				public void onSuccess(Response response) {
					Log.i(TAG, "Checkout email sent");
				}

				@Override
				public void onFailure(Exception e) {
					Log.e(TAG, "Failed to send checkout email", e);
				}
			});
	}

	private void showCartProducts(List<CartProduct> products) {
		if (products == null) {
			showEmptyCart();
			Toast.makeText(this, "Could not load products", Toast.LENGTH_LONG).show();

			return;
		}

		if (products.isEmpty()) {
			showEmptyCart();

			return;
		}

		adapter.setItems(products);

		TransitionManager.beginDelayedTransition(binding.rootLayout, new Fade());
		binding.emptyView.setVisibility(View.INVISIBLE);
		binding.loading.setVisibility(View.INVISIBLE);
		binding.cartList.setVisibility(View.VISIBLE);
		binding.button.setVisibility(View.VISIBLE);
		binding.button.setText(R.string.checkout);
		binding.button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendCheckoutEmail();

				finish();
			}
		});
	}

	private void showEmptyCart() {
		TransitionManager.beginDelayedTransition(binding.rootLayout, new Fade());
		binding.emptyView.setVisibility(View.VISIBLE);
		binding.loading.setVisibility(View.INVISIBLE);
		binding.cartList.setVisibility(View.INVISIBLE);
		binding.button.setVisibility(View.VISIBLE);
		binding.button.setText(R.string.start_shopping);
		binding.button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private void showLoading() {
		TransitionManager.beginDelayedTransition(binding.rootLayout, new Fade());
		binding.emptyView.setVisibility(View.INVISIBLE);
		binding.loading.setVisibility(View.VISIBLE);
		binding.cartList.setVisibility(View.INVISIBLE);
		binding.button.setVisibility(View.INVISIBLE);
	}

	private void updateCheckoutAmount() {
		if (adapter.getItemCount() == 0) {
			return;
		}

		List<CartProduct> cartProducts = adapter.getCartProducts();
		double sum = 0;

		for (CartProduct cartProduct : cartProducts) {
			sum = sum + cartProduct.getProductPrice();
		}

		binding.button.setText(getString(R.string.checkout) + ": " + String.format("%.2f", sum));
	}

	private final CartAdapter adapter = new CartAdapter(this);
	private ActivityCartBinding binding;

	private static final String TAG = CartActivity.class.getSimpleName();

}
