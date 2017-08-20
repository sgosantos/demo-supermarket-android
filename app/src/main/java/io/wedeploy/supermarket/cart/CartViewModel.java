package io.wedeploy.supermarket.cart;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.wedeploy.android.transport.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.cart.model.CartProduct;
import io.wedeploy.supermarket.repository.SupermarketData;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silvio Santos
 */
public class CartViewModel extends ViewModel {

	public LiveData<List<CartProduct>> getCart() {
		if (cartProducts == null) {
			cartProducts = new MutableLiveData<>();
			loadCard();
		}

		return cartProducts;
	}

	private void loadCard() {
		SupermarketData data = SupermarketData.getInstance();

		data.getCart()
			.asSingle()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Consumer<Response>() {
				@Override
				public void accept(@NonNull Response response) throws Exception {
					cartProducts.setValue(parseCartProducts(response));
				}
			}, new Consumer<Throwable>() {
				@Override
				public void accept(@NonNull Throwable throwable) throws Exception {

				}
			});
	}

	@android.support.annotation.NonNull
	private List<CartProduct> parseCartProducts(Response response) throws JSONException {
		JSONArray jsonArray = new JSONArray(response.getBody());
		List<CartProduct> cartProducts = new ArrayList<>(50);

		for (int i = 0; i < jsonArray.length(); i++) {
			cartProducts.add(new CartProduct(jsonArray.getJSONObject(i)));
		}

		return cartProducts;
	}

	private MutableLiveData<List<CartProduct>> cartProducts;

}
