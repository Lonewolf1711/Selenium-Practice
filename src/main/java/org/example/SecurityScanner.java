package org.example;

import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class SecurityScanner {

    private static final List<String> ERROR_SIGNATURES = List.of(
            "sqlsyntaxerrorexception",
            "you have an error in your sql syntax",
            "unclosed quotation mark",
            "ora-00",
            "traceback (most recent call last)",
            "nullpointerexception",
            "java.lang.");

    private SecurityScanner() {}

    public static List<String> scan(WebDriver driver, String payload) {
        List<String> findings = new ArrayList<>();

        if (dismissAlertIfPresent(driver)) {
            findings.add("Script executed (JavaScript alert fired) for payload: " + payload);
        }

        String source = driver.getPageSource();
        if (payload.contains("<") && source.contains(payload)) {
            findings.add("Payload reflected unescaped in page source: " + payload);
        }

        String lowered = source.toLowerCase();
        for (String signature : ERROR_SIGNATURES) {
            if (lowered.contains(signature)) {
                findings.add("Possible error/stack trace leak ('" + signature + "') after payload: " + payload);
            }
        }
        return findings;
    }

    private static boolean dismissAlertIfPresent(WebDriver driver) {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}