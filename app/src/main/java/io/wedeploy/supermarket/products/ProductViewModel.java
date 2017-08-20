package io.wedeploy.supermarket.products;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.wedeploy.android.transport.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.products.model.Product;
import io.wedeploy.supermarket.repository.SupermarketData;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static io.wedeploy.supermarket.SupermarketApplication.getContext;

/**
 * @author Silvio Santos
 */
public class ProductViewModel extends ViewModel {

	public void addToCart(Product product) {
		SupermarketData data = SupermarketData.getInstance();

		cartItemCount.setValue(cartItemCount.getValue() + 1);

		disposables.add(data.addToCart(product)
			.asSingle()
			.subscribeOn(Schedulers.io())
			.subscribe(new BiConsumer<Response, Throwable>() {
				@Override
				public void accept(
					@io.reactivex.annotations.NonNull Response response,
					@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
					if (throwable == null) {
						return;
					}

					cartItemCount.setValue(cartItemCount.getValue() - 1);

					Toast.makeText(
						getContext(),
						R.string.could_not_add_item_to_cart,
						Toast.LENGTH_SHORT).show();
				}
			}));
	}

	public LiveData<Integer> getCartItemCount() {
		if (cartItemCount == null) {
			cartItemCount = new MutableLiveData<>();
			cartItemCount.setValue(0);
		}

		loadCartItemCount();

		return cartItemCount;
	}

	public LiveData<List<Product>> getProducts() {
		if (products == null) {
			products = new MutableLiveData<>();
			loadProducts(type);
		}

		return products;
	}

	public void filterProducts(String type) {
		if (changedFilter(type)) {
			loadProducts(type);
		}

		this.type = type;
	}

	@Override
	protected void onCleared() {
		disposables.clear();
	}

	private boolean changedFilter(String type) {
		return !this.type.equalsIgnoreCase(type);
	}

	private void loadCartItemCount() {
		SupermarketData data = SupermarketData.getInstance();

		disposables.add(data.getCartCount()
			.asSingle()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Consumer<Response>() {
				@Override
				public void accept(
					@io.reactivex.annotations.NonNull Response response) throws Exception {

					cartItemCount.setValue(Integer.valueOf(response.getBody()));
				}
			}, new Consumer<Throwable>() {
				@Override
				public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
					throws Exception {

					cartItemCount.setValue(null);
				}
			}));
	}

	private void loadProducts(String type) {
		SupermarketData data = SupermarketData.getInstance();
		disposables.add(data.getProducts(type)
			.asSingle()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(
				new Consumer<Response>() {
					@Override
					public void accept(@NonNull Response response) throws Exception {
						products.setValue(parseProducts(response));
					}
				},
				new Consumer<Throwable>() {
					@Override
					public void accept(@NonNull Throwable throwable) throws Exception {
						products.setValue(null);
					}
				}));
	}

	@NonNull
	private List<Product> parseProducts(Response response) throws JSONException {
		JSONArray jsonArray = new JSONArray(response.getBody());
		List<Product> products = new ArrayList<>(50);

		for (int i = 0; i < jsonArray.length(); i++) {
			products.add(new Product(jsonArray.getJSONObject(i)));
		}

		return products;
	}

	private MutableLiveData<Integer> cartItemCount;
	private CompositeDisposable disposables = new CompositeDisposable();
	private MutableLiveData<List<Product>> products;
	private String type = "all";

}
