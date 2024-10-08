
package com.licensechain;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LicenseChainClient {
    private final OkHttpClient client;
    private final String apiUrl;

    public LicenseChainClient(String apiUrl) {
        this.client = new OkHttpClient();
        this.apiUrl = apiUrl;
    }

    public String sendRequest(String endpoint, String apiKey) throws Exception {
        Request request = new Request.Builder()
            .url(apiUrl + endpoint)
            .addHeader("Authorization", "Bearer " + apiKey)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new LicenseChainException("Unexpected code " + response);

            return response.body().string();
        }
    }
}
