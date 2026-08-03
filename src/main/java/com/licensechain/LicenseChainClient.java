package com.licensechain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.licensechain.exceptions.*;
import com.licensechain.models.*;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * LicenseChain Java SDK Client
 * 
 * Main client for interacting with the LicenseChain API.
 */
public class LicenseChainClient {
    private static final String DEFAULT_BASE_URL = "https://api.licensechain.app/v1";
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int DEFAULT_RETRY_ATTEMPTS = 3;
    private static final long DEFAULT_RETRY_DELAY = 1000;

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final String apiKey;
    private final Gson gson;

    public LicenseChainClient(String apiKey) {
        this(apiKey, DEFAULT_BASE_URL, DEFAULT_TIMEOUT, DEFAULT_RETRY_ATTEMPTS, DEFAULT_RETRY_DELAY);
    }

    public LicenseChainClient(String apiKey, String baseUrl) {
        this(apiKey, baseUrl, DEFAULT_TIMEOUT, DEFAULT_RETRY_ATTEMPTS, DEFAULT_RETRY_DELAY);
    }

    public LicenseChainClient(String apiKey, String baseUrl, int timeout, int retryAttempts, long retryDelay) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key is required");
        }

        this.apiKey = apiKey;
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.gson = new Gson();

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor(retryAttempts, retryDelay))
                .build();
    }

    // Authentication Methods

    /**
     * Register a new user
     */
    public User registerUser(UserRegistrationRequest request) throws LicenseChainException {
        return post("/auth/register", request, User.class);
    }

    /**
     * Login with email and password
     */
    public LoginResponse login(LoginRequest request) throws LicenseChainException {
        return post("/auth/login", request, LoginResponse.class);
    }

    /**
     * Logout the current user
     */
    public void logout() throws LicenseChainException {
        post("/auth/logout", null, Void.class);
    }

    /**
     * Refresh authentication token
     */
    public TokenRefreshResponse refreshToken(String refreshToken) throws LicenseChainException {
        JsonObject request = new JsonObject();
        request.addProperty("refresh_token", refreshToken);
        return post("/auth/refresh", request, TokenRefreshResponse.class);
    }

    /**
     * Get current user profile
     */
    public User getUserProfile() throws LicenseChainException {
        return get("/auth/me", User.class);
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(UserUpdateRequest request) throws LicenseChainException {
        throw new ValidationException("User profile update endpoint is not available in API v1");
    }

    /**
     * Change user password
     */
    public void changePassword(PasswordChangeRequest request) throws LicenseChainException {
        patch("/auth/password", request, Void.class);
    }

    /**
     * Request password reset
     */
    public void requestPasswordReset(String email) throws LicenseChainException {
        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        post("/auth/forgot-password", request, Void.class);
    }

    /**
     * Reset password with token
     */
    public void resetPassword(PasswordResetRequest request) throws LicenseChainException {
        post("/auth/reset-password", request, Void.class);
    }

    // Application Management

    /**
     * Create a new application
     */
    public Application createApplication(ApplicationCreateRequest request) throws LicenseChainException {
        return post("/apps", request, Application.class);
    }

    /**
     * List applications with pagination
     */
    public PaginatedResponse<Application> listApplications(ApplicationListRequest request) throws LicenseChainException {
        Map<String, String> params = new HashMap<>();
        if (request.getPage() != null) params.put("page", String.valueOf(request.getPage()));
        if (request.getLimit() != null) params.put("limit", String.valueOf(request.getLimit()));
        if (request.getStatus() != null) params.put("status", request.getStatus());
        if (request.getSortBy() != null) params.put("sort_by", request.getSortBy());
        if (request.getSortOrder() != null) params.put("sort_order", request.getSortOrder());

        return get("/apps", params, PaginatedResponse.class);
    }

    /**
     * Get application details
     */
    public Application getApplication(String appId) throws LicenseChainException {
        return get("/apps/" + appId, Application.class);
    }

    /**
     * Update application
     */
    public Application updateApplication(String appId, ApplicationUpdateRequest request) throws LicenseChainException {
        return patch("/apps/" + appId, request, Application.class);
    }

    /**
     * Delete application
     */
    public void deleteApplication(String appId) throws LicenseChainException {
        delete("/apps/" + appId);
    }

    /**
     * Regenerate API key for application
     */
    public ApiKeyResponse regenerateApiKey(String appId) throws LicenseChainException {
        return post("/apps/" + appId + "/regenerate-key", null, ApiKeyResponse.class);
    }

    // License Management

    /**
     * Create a new license
     */
    public License createLicense(LicenseCreateRequest request) throws LicenseChainException {
        JsonObject payload = gson.toJsonTree(request).getAsJsonObject();
        String appId = getOptionalString(payload, "appId");
        if (appId == null) {
            appId = getOptionalString(payload, "app_id");
        }
        if (appId == null || appId.trim().isEmpty()) {
            throw new ValidationException("appId/app_id is required for API v1 license creation");
        }

        JsonObject createRequest = new JsonObject();
        createRequest.addProperty("appId", appId);
        String plan = getOptionalString(payload, "plan");
        createRequest.addProperty("plan", (plan == null || plan.trim().isEmpty()) ? "FREE" : plan);

        String issuedEmail = getOptionalString(payload, "issuedEmail");
        if (issuedEmail == null) issuedEmail = getOptionalString(payload, "issued_email");
        if (issuedEmail == null) issuedEmail = getOptionalString(payload, "userEmail");
        if (issuedEmail == null) issuedEmail = getOptionalString(payload, "email");
        if (issuedEmail != null && !issuedEmail.trim().isEmpty()) {
            createRequest.addProperty("issuedEmail", issuedEmail);
        }

        String expiresAt = getOptionalString(payload, "expiresAt");
        if (expiresAt == null) expiresAt = getOptionalString(payload, "expires_at");
        if (expiresAt != null && !expiresAt.trim().isEmpty()) {
            createRequest.addProperty("expiresAt", expiresAt);
        }

        return post("/apps/" + appId + "/licenses", createRequest, License.class);
    }

    /**
     * List licenses with filters
     */
    public PaginatedResponse<License> listLicenses(LicenseListRequest request) throws LicenseChainException {
        Map<String, String> params = new HashMap<>();
        if (request.getPage() != null) params.put("page", String.valueOf(request.getPage()));
        if (request.getLimit() != null) params.put("limit", String.valueOf(request.getLimit()));
        if (request.getAppId() != null) params.put("app_id", request.getAppId());
        if (request.getStatus() != null) params.put("status", request.getStatus());
        if (request.getUserId() != null) params.put("user_id", request.getUserId());
        if (request.getUserEmail() != null) params.put("user_email", request.getUserEmail());
        if (request.getSortBy() != null) params.put("sort_by", request.getSortBy());
        if (request.getSortOrder() != null) params.put("sort_order", request.getSortOrder());

        return get("/licenses", params, PaginatedResponse.class);
    }

    /**
     * Get license details
     */
    public License getLicense(String licenseId) throws LicenseChainException {
        return get("/licenses/" + licenseId, License.class);
    }

    /**
     * Update license
     */
    public License updateLicense(String licenseId, LicenseUpdateRequest request) throws LicenseChainException {
        return patch("/licenses/" + licenseId, request, License.class);
    }

    /**
     * Delete license
     */
    public void deleteLicense(String licenseId) throws LicenseChainException {
        delete("/licenses/" + licenseId);
    }

    /**
     * Validate a license key. Optional hwuid for ecosystem HMAC/HWUID contract.
     */
    public LicenseValidationResult validateLicense(String licenseKey, String appId) throws LicenseChainException {
        return validateLicense(licenseKey, appId, null);
    }

    /**
     * Validate a license key with optional app ID and hardware ID.
     */
    public LicenseValidationResult validateLicense(String licenseKey, String appId, String hwuid) throws LicenseChainException {
        JsonObject request = new JsonObject();
        request.addProperty("key", licenseKey);
        if (appId != null && !appId.isEmpty()) {
            request.addProperty("app_id", appId);
        }
        if (hwuid != null && !hwuid.trim().isEmpty()) {
            request.addProperty("hwuid", hwuid.trim());
        } else {
            request.addProperty("hwuid", generateDefaultHwuid());
        }
        return post("/licenses/verify", request, LicenseValidationResult.class);
    }

    /**
     * Full POST /licenses/verify JSON (valid, optional license_token, license_jwks_uri, etc.).
     */
    public JsonObject verifyLicenseWithDetails(String licenseKey, String appId) throws LicenseChainException {
        return verifyLicenseWithDetails(licenseKey, appId, null);
    }

    /**
     * Full POST /licenses/verify JSON with optional hardware id.
     */
    public JsonObject verifyLicenseWithDetails(String licenseKey, String appId, String hwuid) throws LicenseChainException {
        JsonObject request = new JsonObject();
        request.addProperty("key", licenseKey);
        if (appId != null && !appId.isEmpty()) {
            request.addProperty("app_id", appId);
        }
        if (hwuid != null && !hwuid.trim().isEmpty()) {
            request.addProperty("hwuid", hwuid.trim());
        } else {
            request.addProperty("hwuid", generateDefaultHwuid());
        }
        return post("/licenses/verify", request, JsonObject.class);
    }

    /**
     * Revoke a license
     */
    public void revokeLicense(String licenseId, String reason) throws LicenseChainException {
        JsonObject request = new JsonObject();
        if (reason != null) {
            request.addProperty("reason", reason);
        }
        patch("/licenses/" + licenseId + "/revoke", request, Void.class);
    }

    /**
     * Activate a license
     */
    public void activateLicense(String licenseId) throws LicenseChainException {
        patch("/licenses/" + licenseId + "/activate", null, Void.class);
    }

    /**
     * Extend license expiration
     */
    public void extendLicense(String licenseId, String expiresAt) throws LicenseChainException {
        JsonObject request = new JsonObject();
        request.addProperty("expires_at", expiresAt);
        patch("/licenses/" + licenseId + "/extend", request, Void.class);
    }

    // Webhook Management

    /**
     * Create a webhook
     */
    public Webhook createWebhook(WebhookCreateRequest request) throws LicenseChainException {
        return post("/webhooks", request, Webhook.class);
    }

    /**
     * List webhooks
     */
    public PaginatedResponse<Webhook> listWebhooks(WebhookListRequest request) throws LicenseChainException {
        Map<String, String> params = new HashMap<>();
        if (request.getPage() != null) params.put("page", String.valueOf(request.getPage()));
        if (request.getLimit() != null) params.put("limit", String.valueOf(request.getLimit()));
        if (request.getAppId() != null) params.put("app_id", request.getAppId());
        if (request.getStatus() != null) params.put("status", request.getStatus());

        return get("/webhooks", params, PaginatedResponse.class);
    }

    /**
     * Get webhook details
     */
    public Webhook getWebhook(String webhookId) throws LicenseChainException {
        return get("/webhooks/" + webhookId, Webhook.class);
    }

    /**
     * Update webhook
     */
    public Webhook updateWebhook(String webhookId, WebhookUpdateRequest request) throws LicenseChainException {
        return patch("/webhooks/" + webhookId, request, Webhook.class);
    }

    /**
     * Delete webhook
     */
    public void deleteWebhook(String webhookId) throws LicenseChainException {
        delete("/webhooks/" + webhookId);
    }

    /**
     * Test webhook
     */
    public void testWebhook(String webhookId) throws LicenseChainException {
        post("/webhooks/" + webhookId + "/test", null, Void.class);
    }

    // Analytics

    /**
     * Get analytics data
     */
    public Analytics getAnalytics(AnalyticsRequest request) throws LicenseChainException {
        Map<String, String> params = new HashMap<>();
        if (request.getPeriod() != null) params.put("period", request.getPeriod());
        return get("/analytics/stats", params, Analytics.class);
    }

    /**
     * Get license analytics
     */
    public Analytics getLicenseAnalytics(String licenseId) throws LicenseChainException {
        return get("/licenses/" + licenseId + "/analytics", Analytics.class);
    }

    /**
     * Get usage statistics
     */
    public UsageStats getUsageStats(UsageStatsRequest request) throws LicenseChainException {
        Map<String, String> params = new HashMap<>();
        if (request.getAppId() != null) params.put("app_id", request.getAppId());
        if (request.getPeriod() != null) params.put("period", request.getPeriod());
        if (request.getGranularity() != null) params.put("granularity", request.getGranularity());

        return get("/analytics/usage", params, UsageStats.class);
    }

    // System Status

    /**
     * Get system status
     */
    public SystemStatus getSystemStatus() throws LicenseChainException {
        return get("/health", SystemStatus.class);
    }

    /**
     * Get health check
     */
    public HealthCheck getHealthCheck() throws LicenseChainException {
        return get("/health", HealthCheck.class);
    }

    // HTTP Methods

    private <T> T get(String path, Class<T> responseClass) throws LicenseChainException {
        return get(path, null, responseClass);
    }

    private <T> T get(String path, Map<String, String> params, Class<T> responseClass) throws LicenseChainException {
        String normalizedPath = normalizePath(path);
        
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + normalizedPath).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("User-Agent", "LicenseChain-Java-SDK/1.0.0")
                .addHeader("Accept", "application/json")
                .build();

        return executeRequest(request, responseClass);
    }

    private <T> T post(String path, Object body, Class<T> responseClass) throws LicenseChainException {
        String normalizedPath = normalizePath(path);
        
        RequestBody requestBody = null;
        if (body != null) {
            String json = gson.toJson(body);
            requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        } else {
            requestBody = RequestBody.create("", MediaType.get("application/json; charset=utf-8"));
        }

        Request request = new Request.Builder()
                .url(baseUrl + normalizedPath)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("User-Agent", "LicenseChain-Java-SDK/1.0.0")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return executeRequest(request, responseClass);
    }

    private <T> T patch(String path, Object body, Class<T> responseClass) throws LicenseChainException {
        String normalizedPath = normalizePath(path);
        
        RequestBody requestBody = null;
        if (body != null) {
            String json = gson.toJson(body);
            requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        } else {
            requestBody = RequestBody.create("", MediaType.get("application/json; charset=utf-8"));
        }

        Request request = new Request.Builder()
                .url(baseUrl + normalizedPath)
                .patch(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("User-Agent", "LicenseChain-Java-SDK/1.0.0")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return executeRequest(request, responseClass);
    }

    private void delete(String path) throws LicenseChainException {
        String normalizedPath = normalizePath(path);
        
        Request request = new Request.Builder()
                .url(baseUrl + normalizedPath)
                .delete()
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("User-Agent", "LicenseChain-Java-SDK/1.0.0")
                .addHeader("Accept", "application/json")
                .build();

        executeRequest(request, Void.class);
    }

    private <T> T executeRequest(Request request, Class<T> responseClass) throws LicenseChainException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleErrorResponse(response);
            }

            if (responseClass == Void.class) {
                return null;
            }

            String responseBody = response.body().string();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }

            return gson.fromJson(responseBody, responseClass);
        } catch (IOException e) {
            throw new NetworkException("Network error: " + e.getMessage(), e);
        }
    }

    private String normalizePath(String path) {
        boolean baseHasV1 = baseUrl.endsWith("/v1");
        if (path.startsWith("/v1/")) {
            return baseHasV1 ? path.substring(3) : path;
        }
        if (path.startsWith("/")) {
            return baseHasV1 ? path : "/v1" + path;
        }
        return baseHasV1 ? "/" + path : "/v1/" + path;
    }

    private static String normalizeBaseUrl(String baseUrl) {
        String normalized = baseUrl == null ? "" : baseUrl.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.isEmpty()) {
            return DEFAULT_BASE_URL;
        }
        return normalized.endsWith("/v1") ? normalized : normalized + "/v1";
    }

    private void handleErrorResponse(Response response) throws LicenseChainException {
        String errorBody = "";
        try {
            if (response.body() != null) {
                errorBody = response.body().string();
            }
        } catch (IOException e) {
            // Ignore error reading body
        }

        String errorMessage = "HTTP " + response.code();
        String errorCode = null;
        Map<String, Object> details = new HashMap<>();

        if (!errorBody.isEmpty()) {
            try {
                JsonObject errorJson = JsonParser.parseString(errorBody).getAsJsonObject();
                if (errorJson.has("error")) {
                    errorMessage = errorJson.get("error").getAsString();
                }
                if (errorJson.has("code")) {
                    errorCode = errorJson.get("code").getAsString();
                }
                if (errorJson.has("details")) {
                    // Parse details if available
                }
            } catch (Exception e) {
                // Use default error message
            }
        }

        int statusCode = response.code();
        switch (statusCode) {
            case 400:
                throw new ValidationException(errorMessage, errorCode, statusCode, details);
            case 401:
            case 403:
                throw new AuthenticationException(errorMessage, errorCode, statusCode, details);
            case 404:
                throw new NotFoundException(errorMessage, errorCode, statusCode, details);
            case 429:
                throw new RateLimitException(errorMessage, errorCode, statusCode, details);
            case 500:
            case 502:
            case 503:
            case 504:
                throw new ServerException(errorMessage, errorCode, statusCode, details);
            default:
                if (statusCode >= 400 && statusCode < 500) {
                    throw new ValidationException(errorMessage, errorCode, statusCode, details);
                } else if (statusCode >= 500) {
                    throw new ServerException(errorMessage, errorCode, statusCode, details);
                } else {
                    throw new LicenseChainException(errorMessage, errorCode, statusCode, details);
                }
        }
    }

    private String getOptionalString(JsonObject obj, String key) {
        if (obj == null || key == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }
        try {
            return obj.get(key).getAsString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String generateDefaultHwuid() {
        String raw = "licensechain|java|" +
            System.getProperty("user.name", "") + "|" +
            System.getProperty("os.name", "") + "|" +
            System.getProperty("os.arch", "");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(hash.length * 2);
            for (byte b : hash) out.append(String.format("%02x", b));
            return out.toString();
        } catch (Exception ignored) {
            return Integer.toHexString(raw.hashCode());
        }
    }

    public static class UserRegistrationRequest {}
    public static class LoginRequest {}
    public static class LoginResponse {}
    public static class TokenRefreshResponse {}
    public static class UserUpdateRequest {}
    public static class PasswordChangeRequest {}
    public static class PasswordResetRequest {}
    public static class ApplicationCreateRequest {}
    public static class Application {}
    public static class ApplicationUpdateRequest {}
    public static class ApiKeyResponse {}
    public static class LicenseCreateRequest {}
    public static class LicenseUpdateRequest {}
    public static class LicenseValidationResult {}
    public static class WebhookCreateRequest {}
    public static class WebhookUpdateRequest {}
    public static class Analytics {}
    public static class UsageStats {}
    public static class SystemStatus {}
    public static class HealthCheck {}

    public static class ApplicationListRequest {
        public Integer getPage() { return null; }
        public Integer getLimit() { return null; }
        public String getStatus() { return null; }
        public String getSortBy() { return null; }
        public String getSortOrder() { return null; }
    }

    public static class LicenseListRequest {
        public Integer getPage() { return null; }
        public Integer getLimit() { return null; }
        public String getAppId() { return null; }
        public String getStatus() { return null; }
        public String getUserId() { return null; }
        public String getUserEmail() { return null; }
        public String getSortBy() { return null; }
        public String getSortOrder() { return null; }
    }

    public static class WebhookListRequest {
        public Integer getPage() { return null; }
        public Integer getLimit() { return null; }
        public String getAppId() { return null; }
        public String getStatus() { return null; }
    }

    public static class AnalyticsRequest {
        public String getPeriod() { return null; }
    }

    public static class UsageStatsRequest {
        public String getAppId() { return null; }
        public String getPeriod() { return null; }
        public String getGranularity() { return null; }
    }

    public static class PaginatedResponse<T> {}

    // Retry Interceptor
    private static class RetryInterceptor implements Interceptor {
        private final int maxRetries;
        private final long retryDelay;

        public RetryInterceptor(int maxRetries, long retryDelay) {
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;

            for (int i = 0; i <= maxRetries; i++) {
                try {
                    response = chain.proceed(request);
                    if (response.isSuccessful() || !isRetryableError(response.code())) {
                        return response;
                    }
                    response.close();
                } catch (IOException e) {
                    exception = e;
                }

                if (i < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * (long) Math.pow(2, i));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry", e);
                    }
                }
            }

            if (response != null) {
                return response;
            }
            throw exception;
        }

        private boolean isRetryableError(int statusCode) {
            return statusCode == 429 || statusCode >= 500;
        }
    }
}
