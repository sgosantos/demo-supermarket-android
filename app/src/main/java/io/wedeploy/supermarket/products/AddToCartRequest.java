package io.wedeploy.supermarket.products;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.wedeploy.android.Callback;
import com.wedeploy.android.transport.Response;
import io.wedeploy.supermarket.R;
import io.wedeploy.supermarket.products.model.Product;
import io.wedeploy.supermarket.repository.SupermarketData;
import io.wedeploy.supermarket.signup.SignUpRequest;
import org.json.JSONException;

/**
 * @author Silvio Santos
 */
public class AddToCartRequest extends Fragment {

	public static void addToCart(AppCompatActivity activity, Product product) {
		AddToCartRequest request = new AddToCartRequest();
		request.setRetainInstance(true);
		request.product = product;

		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		transaction.add(request, TAG);
		transaction.commit();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SupermarketData supermarketData = SupermarketData.getInstance();

		try {
			supermarketData.addToCart(product)
				.execute(new Callback() {
					@Override
					public void onSuccess(Response response) {
						removeRequest();
					}

					@Override
					public void onFailure(Exception e) {
						removeRequest();

						Toast.makeText(
							getContext(),
							R.string.could_not_add_item_to_cart,
							Toast.LENGTH_SHORT).show();
					}
				});
		}
		catch (JSONException e) {
			Toast.makeText(
				getContext(),
				R.string.could_not_add_item_to_cart,
				Toast.LENGTH_SHORT).show();
		}
	}

	private void removeRequest() {
		getActivity().getSupportFragmentManager()
			.beginTransaction()
			.remove(AddToCartRequest.this)
			.commit();
	}

	private Product product;
	private static final String TAG = AddToCartRequest.class.getSimpleName();

}
