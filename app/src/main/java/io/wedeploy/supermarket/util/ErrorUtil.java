package io.wedeploy.supermarket.util;

import com.wedeploy.android.exception.WeDeployException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silvio Santos
 */
public class ErrorUtil {

	public static String getError(Throwable throwable, String defaultMessage) {
		Throwable cause = throwable.getCause();

		if (cause != null) {
			return cause.getMessage();
		}
		else if (throwable instanceof WeDeployException) {
			try {
				Matcher matcher = Pattern.compile("\\{([\\s\\S]*)\\}").matcher(throwable.getMessage());

				if (matcher.find()) {
					JSONObject jsonObject = new JSONObject(matcher.group(0));

					String errorDescription = jsonObject.optString("error_description", null);

					if (errorDescription != null) {
						return errorDescription;
					}

					return jsonObject.getString("message");
				}
			}
			catch (JSONException je) {
				return defaultMessage;
			}
		}

		return defaultMessage;
	}
}
