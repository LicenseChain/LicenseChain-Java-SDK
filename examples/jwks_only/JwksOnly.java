// JWKS-only: verify license_token with token + JWKS URI (parity with Go/Rust/PHP jwks_only).
// Env: LICENSECHAIN_LICENSE_TOKEN, LICENSECHAIN_LICENSE_JWKS_URI
// Optional: LICENSECHAIN_EXPECTED_APP_ID
//
// Build SDK then compile this file with the runtime classpath (see README in this folder).

import com.licensechain.LicenseAssertion;
import net.minidev.json.JSONObject;

public class JwksOnly {
    public static void main(String[] args) throws Exception {
        String token = System.getenv("LICENSECHAIN_LICENSE_TOKEN");
        String jwks = System.getenv("LICENSECHAIN_LICENSE_JWKS_URI");
        if (token == null) {
            token = "";
        }
        if (jwks == null) {
            jwks = "";
        }
        token = token.trim();
        jwks = jwks.trim();
        if (token.isEmpty() || jwks.isEmpty()) {
            System.err.println("Set LICENSECHAIN_LICENSE_TOKEN and LICENSECHAIN_LICENSE_JWKS_URI");
            System.exit(1);
        }

        LicenseAssertion.VerifyLicenseAssertionOptions opts = new LicenseAssertion.VerifyLicenseAssertionOptions();
        String appId = System.getenv("LICENSECHAIN_EXPECTED_APP_ID");
        if (appId != null && !appId.trim().isEmpty()) {
            opts.setExpectedAppId(appId.trim());
        }

        var claims = LicenseAssertion.verifyLicenseAssertionJwt(token, jwks, opts);
        JSONObject jo = claims.toJSONObject();
        System.out.println(jo.toJSONString());
    }
}
