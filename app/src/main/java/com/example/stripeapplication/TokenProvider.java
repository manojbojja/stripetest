package com.example.stripeapplication;


import android.util.Log;


import com.stripe.stripeterminal.callable.ConnectionTokenCallback;
import com.stripe.stripeterminal.callable.ConnectionTokenProvider;
import com.stripe.stripeterminal.model.external.ConnectionTokenException;

import org.json.JSONObject;

import javax.annotation.Nonnull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenProvider implements ConnectionTokenProvider {

    @Override
    public void fetchConnectionToken(@Nonnull ConnectionTokenCallback connectionTokenCallback) {
        try {
            String url = "http://192.168.1.17//connectionToken";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .header("X-API-KEY", "XEbDaXQ4zzyOWfGErGg6TmHTSuGRlT3O0lZTZzw2svA")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            String secret = jsonObject.getString("secret");
            Log.d("secret", secret);
            connectionTokenCallback.onSuccess(secret);
        } catch (Exception e) {
            Log.d("Token", "Error happened");
            connectionTokenCallback.onFailure( new ConnectionTokenException("Failed to fetch token", e));
        }
    }
}
