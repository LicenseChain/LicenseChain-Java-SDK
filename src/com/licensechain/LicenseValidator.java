
package com.licensechain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LicenseValidator {
    private final LicenseChainClient client;

    public LicenseValidator(LicenseChainClient(LicenseChainClient client) {
        this.client = client;
    }

    public boolean validateLicense(String licenseKey, String apiKey) throws Exception {
        String endpoint = "/license/" + licenseKey +"/validate";
        String jsonResponse = client.sendRequest(endpoint, apiKey);

        JsonObject responseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return responseObject.get("valid").getAsBoolean();
    }
}
