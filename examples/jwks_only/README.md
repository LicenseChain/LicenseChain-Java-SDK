# JWKS-only (`license_token`)

Verifies a Core API **`license_token`** using **`GET /v1/licenses/jwks`** (RS256), without calling `verifyLicenseWithDetails` first. Same env contract as **Go / Rust / PHP** `jwks_only` samples ([JWKS_EXAMPLE_PRIORITY](https://docs.licensechain.app/)).

## Env

| Variable | Required | Description |
|----------|----------|-------------|
| `LICENSECHAIN_LICENSE_TOKEN` | yes | JWT string from a successful verify response |
| `LICENSECHAIN_LICENSE_JWKS_URI` | yes | e.g. `https://api.licensechain.app/v1/licenses/jwks` |
| `LICENSECHAIN_EXPECTED_APP_ID` | no | If set, `aud` must include this app UUID |

## Run

From the **Java SDK repo root** (after [Maven](https://maven.apache.org/) install):

```bash
mvn -q -DskipTests package
mvn -q -DincludeScope=runtime dependency:build-classpath -Dmdep.outputFile=/tmp/lc-cp.txt
export LC_CP="target/licensechain-java-sdk-1.0.0.jar:$(cat /tmp/lc-cp.txt)"
javac -encoding UTF-8 -cp "$LC_CP" examples/jwks_only/JwksOnly.java
export LICENSECHAIN_LICENSE_TOKEN="eyJ..."
export LICENSECHAIN_LICENSE_JWKS_URI="https://api.licensechain.app/v1/licenses/jwks"
java -cp "examples/jwks_only:$LC_CP" JwksOnly
```

On Windows, replace `export` with `set` and adjust path separators for `LC_CP`.
