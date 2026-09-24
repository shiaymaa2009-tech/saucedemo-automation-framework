package org.example.tests.session;

import io.qameta.allure.*;
import org.example.base.BaseTest;
import org.example.components.MenuComponent;
import org.example.config.ConfigReader;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.example.pages.cart.CartPage;
import org.example.pages.inventory.InventoryPage;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Epic("SauceDemo E-Commerce")
@Feature("Module 6: Logout, Session State & Cart Persistence (TS-SD-SESS-001)")
public class SessionLogoutTest extends BaseTest {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private MenuComponent menuComponent;

    @BeforeMethod
    public void setUpSession() {
        loginPage = new LoginPage();
        menuComponent = new MenuComponent();

        // Precondition: Log in as standard_user
        loginPage.login(UserCredentials.standardUser());
        inventoryPage = new InventoryPage();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Precondition failed: Inventory page did not load.");
    }

    @Test(description = "TC_SES_001: Successful Logout via Hamburger Menu")
    @Story("TC_SES_001")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies opening the burger drawer and clicking 'Logout' terminates the session and returns to login.")
    public void testLogoutFlow() {
        menuComponent.clickLogout();

        // Assert redirect to root URL
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.equals(ConfigReader.getBaseUrl()) || currentUrl.endsWith("/") || currentUrl.contains("index.html"),
                "User was not redirected to the login page post-logout. Current URL: " + currentUrl);

        // Assert login page elements are displayed
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page failed to load after logout.");
    }

    @Test(description = "TC_SES_002: Session Cookie Token Destruction Post-Logout")
    @Story("TC_SES_002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that logging out completely destroys the 'session-username' cookie.")
    public void testSessionTokenDestruction() {
        // Assert cookie is present initially
        Cookie activeCookie = driver.manage().getCookieNamed("session-username");
        Assert.assertNotNull(activeCookie, "Session cookie should exist while logged in.");
        Assert.assertEquals(activeCookie.getValue(), "standard_user");

        menuComponent.clickLogout();

        // Assert cookie is destroyed post-logout
        Cookie destroyedCookie = driver.manage().getCookieNamed("session-username");
        Assert.assertNull(destroyedCookie, "Security violation: 'session-username' cookie was not deleted post-logout.");
    }

    @Test(description = "TC_SES_003: Protected Route Access Post-Logout: Inventory")
    @Story("TC_SES_003")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies direct unauthenticated GET request to /inventory.html post-logout is blocked by route guard.")
    public void testDirectAccessGuardPostLogoutInventory() {
        menuComponent.clickLogout();

        String protectedUrl = ConfigReader.getBaseUrl() + "inventory.html";
        driver.get(protectedUrl);

        Assert.assertFalse(driver.getCurrentUrl().contains("inventory.html"),
                "Security vulnerability: Direct access to /inventory.html allowed post-logout!");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Route guard error message was not displayed.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.");
    }

    @Test(description = "TC_SES_004: Protected Route Access Post-Logout: Shopping Cart")
    @Story("TC_SES_004")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies direct unauthenticated GET request to /cart.html post-logout is blocked by route guard.")
    public void testDirectAccessGuardPostLogoutCart() {
        menuComponent.clickLogout();

        String protectedCartUrl = ConfigReader.getBaseUrl() + "cart.html";
        driver.get(protectedCartUrl);

        Assert.assertFalse(driver.getCurrentUrl().contains("cart.html"),
                "Security vulnerability: Direct access to /cart.html allowed post-logout!");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Route guard error message was not displayed.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/cart.html' when you are logged in.");
    }

    @Test(description = "TC_SES_006: Native Browser Back Button Invalidation Post-Logout")
    @Story("TC_SES_006")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking browser native Back button post-logout does not restore cached inventory session.")
    public void testBackButtonSessionDenial() {

        menuComponent.clickLogout();

        Assert.assertNull(
                driver.manage().getCookieNamed("session-username")
        );

        driver.navigate().back();

        Assert.assertFalse(
                driver.getCurrentUrl().contains("/inventory.html"),
                "Security defect: Browser Back button bypassed logout and displayed cached inventory!"
        );

        Assert.assertTrue(
                loginPage.isPageLoaded(),
                "Login page is not displayed after browser back navigation."
        );

        Assert.assertNull(
                loginPage.getSessionUsernameCookie(),
                "Session cookie must remain null after back-navigation attempt."
        );
    }

    @Test(description = "TC_SES_007: Re-Authentication with Same Credentials")
    @Story("TC_SES_007")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies user can log back in with standard_user credentials and re-establish session.")
    public void testReAuthenticationLifecycle() {
        menuComponent.clickLogout();

        loginPage.login(UserCredentials.standardUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "User was not redirected to /inventory.html upon re-login.");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getPageTitle(), "Products");
        Assert.assertEquals(loginPage.getSessionUsernameCookie(), "standard_user",
                "Session cookie was not recreated upon re-login.");
    }

    @Test(description = "TC_SES_008: Single-Product Cart Persistence Across Re-Login")
    @Story("TC_SES_008")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies a single product added to cart persists in cart and badge across logout/re-login.")
    public void testCartPersistenceAcrossReLogin() {
        String targetProduct = "Sauce Labs Backpack";

        inventoryPage.addProductToCart(targetProduct);
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

        menuComponent.clickLogout();

        loginPage.login(UserCredentials.standardUser());
        Assert.assertTrue(inventoryPage.isPageLoaded());

        // Verify badge persisted
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1,
                "Cart badge count did not persist across logout/re-login lifecycle.");

        // Verify item persisted inside cart table
        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();
        Assert.assertTrue(cartPage.isPageLoaded());
        Assert.assertEquals(cartPage.getCartItemCount(), 1);
        Assert.assertTrue(cartPage.isItemDisplayedInCart(targetProduct),
                "Persisted item '" + targetProduct + "' not found in cart after re-login.");
    }

    @Test(description = "TC_SES_009: Multi-Product Cart Persistence Across Re-Login")
    @Story("TC_SES_009")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies 3 distinct products persist in cart across logout and re-login.")
    public void testMultiItemCartPersistenceAcrossReLogin() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 3);

        menuComponent.clickLogout();

        loginPage.login(UserCredentials.standardUser());

        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 3,
                "Badge count did not persist as 3 post-re-login.");

        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();
        Assert.assertEquals(cartPage.getCartItemCount(), 3);

        List<String> items = cartPage.getAllCartItemNames();
        Assert.assertTrue(items.contains("Sauce Labs Backpack"));
        Assert.assertTrue(items.contains("Sauce Labs Bike Light"));
        Assert.assertTrue(items.contains("Sauce Labs Bolt T-Shirt"));
    }

    @Test(description = "TC_SES_015: Action Button Re-Hydration Post-Re-Login")
    @Story("TC_SES_015")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies catalog action buttons dynamically re-hydrate to red 'Remove' state for persisted cart items.")
    public void testButtonStateReHydrationPostReLogin() {
        String targetProduct = "Sauce Labs Backpack";

        inventoryPage.addProductToCart(targetProduct);
        Assert.assertTrue(inventoryPage.isProductRemoveButtonDisplayed(targetProduct));

        menuComponent.clickLogout();

        loginPage.login(UserCredentials.standardUser());
        Assert.assertTrue(inventoryPage.isPageLoaded());

        // Verify button re-hydrates to 'Remove'
        Assert.assertTrue(inventoryPage.isProductRemoveButtonDisplayed(targetProduct),
                "Catalog button failed to re-hydrate to 'Remove' state after re-login.");
        Assert.assertEquals(inventoryPage.getProductButtonText(targetProduct), "Remove");

        // Verify button color is red
        String buttonColor = inventoryPage.getProductButtonColor(targetProduct);
        Assert.assertTrue(buttonColor.contains("226") || buttonColor.contains("red"),
                "Re-hydrated Remove button did not show red styling. Found: " + buttonColor);
    }
}