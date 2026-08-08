package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void openLogin() {
        loginPage = new LoginPage(driver).open();
    }

    @Test
    void validLoginShowsSuccessMessage() {
        loginPage.login("tomsmith", "SuperSecretPassword!");
        assertTrue(loginPage.getMessage().contains("You logged into a secure area"));
    }

    @Test
    void invalidUsernameShowsError() {
        loginPage.login("wronguser", "SuperSecretPassword!");
        assertTrue(loginPage.getMessage().contains("Your username is invalid"));
    }
}