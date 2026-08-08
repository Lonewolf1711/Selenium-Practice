package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSecurityTest extends BaseTest {

    static Stream<String> payloads() {
        return SecurityPayloads.all().stream();
    }

    @ParameterizedTest(name = "the-internet login resists: {0}")
    @MethodSource("payloads")
    void theInternetLoginResistsPayload(String payload) {
        LoginPage page = new LoginPage(driver).open();
        page.login(payload, payload);

        List<String> findings = SecurityScanner.scan(driver, payload);
        assertTrue(findings.isEmpty(), "Security findings: " + findings);
        assertTrue(page.getMessage().contains("invalid"), "Payload must not authenticate");
    }

    @ParameterizedTest(name = "Sauce Demo login resists: {0}")
    @MethodSource("payloads")
    void sauceDemoLoginResistsPayload(String payload) {
        SauceLoginPage page = new SauceLoginPage(driver).open();
        page.attemptLogin(payload, payload);

        List<String> findings = SecurityScanner.scan(driver, payload);
        assertTrue(findings.isEmpty(), "Security findings: " + findings);
        assertTrue(page.getErrorMessage().contains("do not match"), "Payload must not authenticate");
    }
}