package com.licensechain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class HwuidHashSpecTest {
    @Test
    void defaultHwuidMatchesCanonicalHashSpec() throws Exception {
        LicenseChainClient client = new LicenseChainClient("test-api-key");
        Method method = LicenseChainClient.class.getDeclaredMethod("generateDefaultHwuid");
        method.setAccessible(true);

        String h1 = (String) method.invoke(client);
        String h2 = (String) method.invoke(client);

        assertNotNull(h1);
        assertEquals(h1, h2);
        assertTrue(h1.matches("^[a-f0-9]{64}$"));
    }
}
