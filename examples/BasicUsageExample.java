// LicenseChain Java SDK - Basic Usage Example
import com.licensechain.LicenseChainClient;
import com.licensechain.LicenseChainConfig;
import com.licensechain.LicenseChainException;

public class BasicUsageExample {
    public static void main(String[] args) {
        System.out.println("?? LicenseChain Java SDK - Basic Usage Example");
        System.out.println("=" + "=".repeat(50));
        
        // Initialize the client
        LicenseChainConfig config = new LicenseChainConfig.Builder()
            .apiKey("your-api-key-here")
            .appName("MyJavaApp")
            .version("1.0.0")
            .build();
            
        LicenseChainClient client = new LicenseChainClient(config);
        
        // Connect to LicenseChain
        System.out.println("\n Connecting to LicenseChain...");
        try {
            client.connect();
            System.out.println(" Connected to LicenseChain successfully!");
        } catch (LicenseChainException e) {
            System.err.println(" Failed to connect: " + e.getMessage());
            return;
        }
        
        // Example 1: User Registration
        System.out.println("\n Registering new user...");
        try {
            User user = client.register("testuser", "password123", "test@example.com");
            System.out.println(" User registered successfully!");
            System.out.println("Session ID: " + user.getSessionId());
        } catch (LicenseChainException e) {
            System.err.println(" Registration failed: " + e.getMessage());
        }
        
        // Example 2: License Validation
        System.out.println("\n Validating license...");
        try {
            License license = client.validateLicense("LICENSE-KEY-HERE");
            System.out.println(" License is valid!");
            System.out.println("License Key: " + license.getKey());
            System.out.println("Status: " + license.getStatus());
        } catch (LicenseChainException e) {
            System.err.println(" License validation failed: " + e.getMessage());
        }
        
        // Cleanup
        System.out.println("\n Cleaning up...");
        client.logout();
        client.disconnect();
        System.out.println(" Cleanup completed!");
    }
}
