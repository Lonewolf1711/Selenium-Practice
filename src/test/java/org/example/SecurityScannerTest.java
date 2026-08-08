package org.example;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityScannerTest extends BaseTest {

    private void injectHtml(String html) {
        driver.get("about:blank");
        ((JavascriptExecutor) driver).executeScript("document.body.innerHTML = arguments[0];", html);
    }

    @Test
    void flagsScriptExecution() {
        String payload = "<img src=x onerror=alert(1)>";
        injectHtml(payload);
        assertFalse(SecurityScanner.scan(driver, payload).isEmpty());
    }

    @Test
    void flagsUnescapedReflection() {
        String payload = "<script>alert(1)</script>";
        injectHtml(payload);
        List<String> findings = SecurityScanner.scan(driver, payload);
        assertTrue(findings.stream().anyMatch(f -> f.contains("reflected unescaped")));
    }

    @Test
    void flagsLeakedErrorText() {
        injectHtml("<pre>java.lang.NullPointerException</pre>");
        List<String> findings = SecurityScanner.scan(driver, "' OR 1=1 --");
        assertTrue(findings.stream().anyMatch(f -> f.contains("leak")));
    }

    @Test
    void passesWhenInputIsProperlyEscaped() {
        String payload = "<script>alert(1)</script>";
        driver.get("about:blank");
        ((JavascriptExecutor) driver).executeScript("document.body.textContent = arguments[0];", payload);
        assertTrue(SecurityScanner.scan(driver, payload).isEmpty());
    }
}