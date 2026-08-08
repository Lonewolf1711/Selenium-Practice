package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    protected void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito", "--window-size=1920,1080");
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        }
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        driver = new ChromeDriver(options);
    }

    @AfterEach
    protected void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}