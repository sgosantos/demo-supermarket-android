package io.wedeploy.supermarket.products;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.cart.CartActivity;
import io.wedeploy.supermarket.databinding.ActivityMainBinding;
import io.wedeploy.supermarket.logout.LogoutBottomSheet;
import io.wedeploy.supermarket.products.adapter.ProductAdapter;
import io.wedeploy.supermarket.products.model.Product;
import io.wedeploy.supermarket.repository.Settings;
import io.wedeploy.supermarket.view.OnFilterSelectedListener;

import java.util.List;

/**
 * @author Silvio Santos
 */
public class ProductsActivity extends AppCompatActivity
	implements OnFilterSelectedListener, AddToCartListener,
	LifecycleRegistryOwner {

	LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	@Override
	public void onFilterSelected(String type) {
		binding.filterBarView.setFilter(type);

		productViewModel.filterProducts(type);
	}

	@Override
	public void onItemAddedToCart(Product product) {
		productViewModel.addToCart(product);
	}

	@Override
	public LifecycleRegistry getLifecycle() {
		return lifecycleRegistry;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		binding.productsList.setAdapter(adapter);
		binding.cartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ProductsActivity.this, CartActivity.class));
			}
		});

		binding.userPhoto.setUserPhoto(Settings.getUserPhoto(), Settings.getUserName());
		binding.userPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LogoutBottomSheet.show(ProductsActivity.this);
			}
		});

		if (savedInstanceState != null) {
			binding.filterBarView.setFilter(savedInstanceState.getString(STATE_FILTER));
		}
		else {
			binding.filterBarView.setFilter(getString(R.string.all));
		}

		showLoading();
		setSupportActionBar(binding.toolbar);
		productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);

		productViewModel.getProducts()
			.observe(this, new Observer<List<Product>>() {
				@Override
				public void onChanged(@Nullable List<Product> products) {
					showProducts(products);
				}
			});
	}

	@Override
	protected void onResume() {
		super.onResume();

		productViewModel.getCartItemCount()
			.observe(this, new Observer<Integer>() {
				@Override
				public void onChanged(@Nullable Integer count) {
					if (count != null) {
						updateCartItemCount(count);
					}
				}
			});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(STATE_FILTER, binding.filterBarView.getFilter());
	}

	private void showLoading() {
		TransitionManager.beginDelayedTransition(binding.rootLayout, new Fade());
		binding.loading.setVisibility(View.VISIBLE);
		binding.productsList.setVisibility(View.INVISIBLE);
	}

	private void showProducts(List<Product> products) {
		TransitionManager.beginDelayedTransition(binding.rootLayout, new Fade());
		binding.loading.setVisibility(View.INVISIBLE);
		binding.productsList.setVisibility(View.VISIBLE);

		if (products == null) {
			Toast.makeText(this, "Could not load products", Toast.LENGTH_LONG).show();

			return;
		}

		adapter.setItems(products);
	}

	private void updateCartItemCount(int count) {
		binding.cartItemCount.setText(String.valueOf(count));
		binding.cartItemCount.setVisibility((count > 0) ? View.VISIBLE : View.GONE);
	}

	private final ProductAdapter adapter = new ProductAdapter(this);
	private ActivityMainBinding binding;
	private ProductViewModel productViewModel;
	private static final String STATE_FILTER = "filter";

}
