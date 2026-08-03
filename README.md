# LicenseChain Java SDK

[![License](https://img.shields.io/badge/license-Elastic--2.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![Maven Central](https://img.shields.io/maven-central/v/com.licensechain/licensechain-sdk)](https://search.maven.org/artifact/com.licensechain/licensechain-sdk)

Official Java SDK for LicenseChain - Secure license management for Java applications.

## 🚀 Features

- **🔐 Secure Authentication** - User registration, login, and session management
- **📜 License Management** - Create, validate, update, and revoke licenses
- **🛡️ Hardware ID Validation** - Prevent license sharing and unauthorized access
- **🔔 Webhook Support** - Real-time license events and notifications
- **📊 Analytics Integration** - Track license usage and performance metrics
- **⚡ High Performance** - Optimized for production workloads
- **🔄 Async Operations** - Non-blocking HTTP requests and data processing
- **🛠️ Easy Integration** - Simple API with comprehensive documentation

## 📦 Installation

### Method 1: Maven (Recommended)

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.licensechain</groupId>
    <artifactId>licensechain-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Method 2: Gradle

Add to your `build.gradle`:

```gradle
implementation 'com.licensechain:licensechain-sdk:1.0.0'
```

### Method 3: Manual Installation

1. Download the latest JAR from [GitHub Releases](https://github.com/LicenseChain/LicenseChain-Java-SDK/releases)
2. Add the JAR to your classpath
3. Install required dependencies

## 🚀 Quick Start

### Basic Setup

```java
import com.licensechain.LicenseChainClient;
import com.licensechain.LicenseChainConfig;
import com.licensechain.LicenseChainException;

public class BasicExample {
    public static void main(String[] args) {
        // Initialize the client
        LicenseChainConfig config = new LicenseChainConfig.Builder()
            .apiKey("your-api-key")
            .appName("your-app-name")
            .version("1.0.0")
            .build();
            
        LicenseChainClient client = new LicenseChainClient(config);
        
        // Connect to LicenseChain
        try {
            client.connect();
            System.out.println("Connected to LicenseChain successfully!");
        } catch (LicenseChainException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            return;
        }
    }
}
```

### User Authentication

```java
// Register a new user
try {
    User user = client.register("username", "password", "email@example.com");
    System.out.println("User registered successfully!");
    System.out.println("User ID: " + user.getId());
} catch (LicenseChainException e) {
    System.err.println("Registration failed: " + e.getMessage());
}

// Login existing user
try {
    User user = client.login("username", "password");
    System.out.println("User logged in successfully!");
    System.out.println("Session ID: " + user.getSessionId());
} catch (LicenseChainException e) {
    System.err.println("Login failed: " + e.getMessage());
}
```

### License Management

```java
// Validate a license
try {
    License license = client.validateLicense("LICENSE-KEY-HERE");
    System.out.println("License is valid!");
    System.out.println("License Key: " + license.getKey());
    System.out.println("Status: " + license.getStatus());
    System.out.println("Expires: " + license.getExpires());
    System.out.println("Features: " + String.join(", ", license.getFeatures()));
    System.out.println("User: " + license.getUser());
} catch (LicenseChainException e) {
    System.err.println("License validation failed: " + e.getMessage());
}

// Get user's licenses
try {
    List<License> licenses = client.getUserLicenses();
    System.out.println("Found " + licenses.size() + " licenses:");
    for (int i = 0; i < licenses.size(); i++) {
        License license = licenses.get(i);
        System.out.println("  " + (i + 1) + ". " + license.getKey() 
                          + " - " + license.getStatus() 
                          + " (Expires: " + license.getExpires() + ")");
    }
} catch (LicenseChainException e) {
    System.err.println("Failed to get licenses: " + e.getMessage());
}
```

### Hardware ID Validation

```java
// Get hardware ID (automatically generated)
String hardwareId = client.getHardwareId();
System.out.println("Hardware ID: " + hardwareId);

// Validate hardware ID with license
try {
    boolean isValid = client.validateHardwareId("LICENSE-KEY-HERE", hardwareId);
    if (isValid) {
        System.out.println("Hardware ID is valid for this license!");
    } else {
        System.out.println("Hardware ID is not valid for this license.");
    }
} catch (LicenseChainException e) {
    System.err.println("Hardware ID validation failed: " + e.getMessage());
}
```

### Webhook Integration

```java
// Set up webhook handler
client.setWebhookHandler((eventName, data) -> {
    System.out.println("Webhook received: " + eventName);
    
    switch (eventName) {
        case "license.created":
            System.out.println("New license created: " + data.get("licenseKey"));
            break;
        case "license.updated":
            System.out.println("License updated: " + data.get("licenseKey"));
            break;
        case "license.revoked":
            System.out.println("License revoked: " + data.get("licenseKey"));
            break;
    }
});

// Start webhook listener
client.startWebhookListener();
```

## 📚 API Endpoints

Use the canonical API base URL `https://api.licensechain.app/v1`. The SDK also accepts the root host and normalizes requests to the same API version.

### Base URL
- **Production**: `https://api.licensechain.app/v1`
- **Development**: `https://api.licensechain.app/v1`

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/v1/health` | Health check |
| `POST` | `/v1/auth/login` | User login |
| `POST` | `/v1/auth/register` | User registration |
| `GET` | `/v1/apps` | List applications |
| `POST` | `/v1/apps` | Create application |
| `GET` | `/v1/licenses` | List licenses |
| `POST` | `/v1/licenses/verify` | Verify license |
| `GET` | `/v1/webhooks` | List webhooks |
| `POST` | `/v1/webhooks` | Create webhook |
| `GET` | `/v1/analytics` | Get analytics |

**Note**: The SDK accepts either the root host or the canonical `/v1` base and normalizes endpoint requests automatically.

## 📚 API Reference

### LicenseChainClient

#### Constructor

```java
LicenseChainConfig config = new LicenseChainConfig.Builder()
    .apiKey("your-api-key")
    .appName("your-app-name")
    .version("1.0.0")
    .baseUrl("https://api.licensechain.app/v1") // Optional
    .build();
    
LicenseChainClient client = new LicenseChainClient(config);
```

#### Methods

##### Connection Management

```java
// Connect to LicenseChain
client.connect();

// Disconnect from LicenseChain
client.disconnect();

// Check connection status
boolean isConnected = client.isConnected();
```

##### User Authentication

```java
// Register a new user
User user = client.register(username, password, email);

// Login existing user
User user = client.login(username, password);

// Logout current user
client.logout();

// Get current user info
User user = client.getCurrentUser();
```

##### License Management

```java
// Validate a license
License license = client.validateLicense(licenseKey);

// Get user's licenses
List<License> licenses = client.getUserLicenses();

// Create a new license
License license = client.createLicense(userId, features, expires);

// Update a license
License license = client.updateLicense(licenseKey, updates);

// Revoke a license
client.revokeLicense(licenseKey);

// Extend a license
License license = client.extendLicense(licenseKey, days);
```

##### Hardware ID Management

```java
// Get hardware ID
String hardwareId = client.getHardwareId();

// Validate hardware ID
boolean isValid = client.validateHardwareId(licenseKey, hardwareId);

// Bind hardware ID to license
client.bindHardwareId(licenseKey, hardwareId);
```

##### Webhook Management

```java
// Set webhook handler
client.setWebhookHandler(handler);

// Start webhook listener
client.startWebhookListener();

// Stop webhook listener
client.stopWebhookListener();
```

##### Analytics

```java
// Track event
client.trackEvent(eventName, properties);

// Get analytics data
Analytics analytics = client.getAnalytics(timeRange);
```

## 🔧 Configuration

### Properties File

Add to your `application.properties`:

```properties
# Required
licensechain.api.key=your-api-key
licensechain.app.name=your-app-name
licensechain.app.version=1.0.0

# Optional
licensechain.base.url=https://api.licensechain.app/v1
licensechain.timeout=30
licensechain.retries=3
licensechain.debug=false
```

### Spring Boot Integration

```java
@Configuration
public class LicenseChainConfig {
    
    @Value("${licensechain.api.key}")
    private String apiKey;
    
    @Value("${licensechain.app.name}")
    private String appName;
    
    @Value("${licensechain.app.version}")
    private String version;
    
    @Bean
    public LicenseChainClient licenseChainClient() {
        LicenseChainConfig config = new LicenseChainConfig.Builder()
            .apiKey(apiKey)
            .appName(appName)
            .version(version)
            .build();
            
        return new LicenseChainClient(config);
    }
}
```

### Environment Variables

Set these in your environment or through your build process:

```bash
# Required
export LICENSECHAIN_API_KEY=your-api-key
export LICENSECHAIN_APP_NAME=your-app-name
export LICENSECHAIN_APP_VERSION=1.0.0

# Optional
export LICENSECHAIN_BASE_URL=https://api.licensechain.app/v1
export LICENSECHAIN_DEBUG=true
```

## 🛡️ Security Features

### Hardware ID Protection

The SDK automatically generates and manages hardware IDs to prevent license sharing:

```java
// Hardware ID is automatically generated and stored
String hardwareId = client.getHardwareId();

// Validate against license
boolean isValid = client.validateHardwareId(licenseKey, hardwareId);
```

### Secure Communication

- All API requests use HTTPS
- API keys are securely stored and transmitted
- Session tokens are automatically managed
- Webhook signatures are verified

### License Validation

- Real-time license validation
- Hardware ID binding
- Expiration checking
- Feature-based access control

## 📊 Analytics and Monitoring

### Event Tracking

```java
// Track custom events
Map<String, Object> properties = new HashMap<>();
properties.put("level", 1);
properties.put("playerCount", 10);
client.trackEvent("app.started", properties);

// Track license events
Map<String, Object> licenseProperties = new HashMap<>();
licenseProperties.put("licenseKey", "LICENSE-KEY");
licenseProperties.put("features", "premium,unlimited");
client.trackEvent("license.validated", licenseProperties);
```

### Performance Monitoring

```java
// Get performance metrics
PerformanceMetrics metrics = client.getPerformanceMetrics();
System.out.println("API Response Time: " + metrics.getAverageResponseTime() + "ms");
System.out.println("Success Rate: " + String.format("%.2f%%", metrics.getSuccessRate() * 100));
System.out.println("Error Count: " + metrics.getErrorCount());
```

## 🔄 Error Handling

### Custom Exception Types

```java
try {
    License license = client.validateLicense("invalid-key");
} catch (InvalidLicenseException e) {
    System.err.println("License key is invalid");
} catch (ExpiredLicenseException e) {
    System.err.println("License has expired");
} catch (NetworkException e) {
    System.err.println("Network connection failed");
} catch (LicenseChainException e) {
    System.err.println("LicenseChain error: " + e.getMessage());
}
```

### Retry Logic

```java
// Automatic retry for network errors
LicenseChainConfig config = new LicenseChainConfig.Builder()
    .apiKey("your-api-key")
    .appName("your-app-name")
    .version("1.0.0")
    .retries(3)        // Retry up to 3 times
    .timeout(30000)    // Wait 30 seconds for each request
    .build();
```

## 🧪 Testing

### Unit Tests

```bash
# Run tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test
mvn test -Dtest=LicenseChainClientTest
```

### Integration Tests

```bash
# Test with real API
mvn test -Dtest=*IntegrationTest
```

## 📝 Examples

See the `examples/` directory for complete examples:

- `BasicUsageExample.java` - Basic SDK usage
- `AdvancedFeaturesExample.java` - Advanced features and configuration
- `WebhookIntegrationExample.java` - Webhook handling
- `examples/jwks_only/JwksOnly.java` — RS256 `license_token` via JWKS only (see `examples/jwks_only/README.md`; [JWKS_EXAMPLE_PRIORITY](https://docs.licensechain.app/))

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. Clone the repository
2. Install Java 11 or later
3. Install Maven 3.6 or later
4. Build: `mvn clean compile`
5. Test: `mvn test`

## 📄 License

This project is licensed under the Elastic License 2.0 (ELv2) — see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [https://docs.licensechain.app/java](https://docs.licensechain.app/java)
- **Issues**: [GitHub Issues](https://github.com/LicenseChain/LicenseChain-Java-SDK/issues)
- **Discord**: [LicenseChain Discord](https://discord.gg/licensechain)
- **Email**: support@licensechain.app

## 🔗 Related Projects

- [LicenseChain JavaScript SDK](https://github.com/LicenseChain/LicenseChain-JavaScript-SDK)
- [LicenseChain Python SDK](https://github.com/LicenseChain/LicenseChain-Python-SDK)
- [LicenseChain Node.js SDK](https://github.com/LicenseChain/LicenseChain-NodeJS-SDK)
---

**Made with ❤️ for the Java community**

## LicenseChain API (v1)

This SDK targets the **LicenseChain HTTP API v1** implemented by the LicenseChain API service.

- **Production base URL:** https://api.licensechain.app/v1
- **API reference:** [docs.licensechain.app](https://docs.licensechain.app/)
- **Baseline REST mapping (documented for integrators):**
  - GET /health
  - POST /auth/register
  - POST /licenses/verify
  - PATCH /licenses/:id/revoke
  - PATCH /licenses/:id/activate
  - PATCH /licenses/:id/extend
  - GET /analytics/stats

