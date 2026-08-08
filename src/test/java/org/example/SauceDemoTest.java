package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SauceDemoTest extends BaseTest {

    private SauceLoginPage loginPage;

    @BeforeEach
    void openLogin() {
        loginPage = new SauceLoginPage(driver).open();
    }

    @Test
    void validLoginLandsOnProductsPage() {
        InventoryPage inventory = loginPage.loginAs("standard_user", "secret_sauce");
        assertEquals("Products", inventory.getTitle());
    }

    @Test
    void lockedOutUserSeesError() {
        loginPage.attemptLogin("locked_out_user", "secret_sauce");
        assertTrue(loginPage.getErrorMessage().contains("locked out"));
    }

    @Test
    void addingItemsUpdatesCartBadge() {
        InventoryPage inventory = loginPage.loginAs("standard_user", "secret_sauce");
        inventory.addToCart("sauce-labs-backpack").addToCart("sauce-labs-bike-light");
        assertEquals(2, inventory.getCartCount());
    }

    @Test
    void cartContainsTheItemsAdded() {
        InventoryPage inventory = loginPage.loginAs("standard_user", "secret_sauce");
        inventory.addToCart("sauce-labs-backpack");
        List<String> items = inventory.openCart().getItemNames();
        assertEquals(List.of("Sauce Labs Backpack"), items);
    }

    @Test
    void fullCheckoutShowsConfirmation() {
        InventoryPage inventory = loginPage.loginAs("standard_user", "secret_sauce");
        CheckoutPage checkout = inventory.addToCart("sauce-labs-backpack")
                .openCart()
                .checkout()
                .enterDetails("Test", "User", "560001")
                .finish();
        assertEquals("Thank you for your order!", checkout.getConfirmation());
    }

    @Test
    void removingItemEmptiesCart() {
        InventoryPage inventory = loginPage.loginAs("standard_user", "secret_sauce");
        CartPage cart = inventory.addToCart("sauce-labs-backpack")
                .openCart()
                .removeItem("sauce-labs-backpack");
        assertTrue(cart.getItemNames().isEmpty());
    }
}