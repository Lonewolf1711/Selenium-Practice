package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SauceLoginPage extends BasePage {

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorBanner = By.cssSelector("[data-test='error']");

    public SauceLoginPage(WebDriver driver) {
        super(driver);
    }

    public SauceLoginPage open() {
        driver.get("https://www.saucedemo.com/");
        return this;
    }

    public InventoryPage loginAs(String username, String password) {
        attemptLogin(username, password);
        return new InventoryPage(driver);
    }

    public SauceLoginPage attemptLogin(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return this;
    }

    public String getErrorMessage() {
        return getText(errorBanner);
    }
}