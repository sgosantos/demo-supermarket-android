package io.wedeploy.supermarket.products;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.wedeploy.android.transport.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.wedeploy.supermarket.products.model.Product;
import io.wedeploy.supermarket.repository.SupermarketData;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silvio Santos
 */
public class ProductViewModel extends ViewModel {

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

	private boolean changedFilter(String type) {
		return !this.type.equalsIgnoreCase(type);
	}

	private void loadProducts(String type) {
		SupermarketData data = SupermarketData.getInstance();
		data.getProducts(type)
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
				});
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

	private MutableLiveData<List<Product>> products;
	private String type = "all";

}
