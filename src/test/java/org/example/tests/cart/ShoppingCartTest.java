package org.example.tests.cart;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.base.BaseTest;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.example.pages.cart.CartPage;
import org.example.pages.inventory.InventoryPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Epic("SauceDemo E-Commerce")
@Feature("Module 4: Shopping Cart & Badge Transitions (TS-SD-CRT-001)")
public class ShoppingCartTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void setUpCartSession() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(UserCredentials.standardUser());

        inventoryPage = new InventoryPage();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Precondition failed: Inventory page did not load.");
    }

    @Test(description = "TC_CRT_001: Add Single Item from Inventory")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies adding a single item from catalog flips button to 'Remove' and increments cart badge to 1.")
    public void testAddSingleProductToCart() {
        String targetProduct = "Sauce Labs Backpack";
        inventoryPage.addProductToCart(targetProduct);

        Assert.assertTrue(inventoryPage.isProductRemoveButtonDisplayed(targetProduct),
                "Product button did not flip to 'Remove'.");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1,
                "Cart badge count must be 1.");
    }

    @Test(description = "TC_CRT_003: Add Multiple Products to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies adding three distinct products increments badge to 3 and displays all 3 items in cart view.")
    public void testAddMultipleProductsToCart() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");

        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 3);

        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();

        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page failed to load.");
        Assert.assertEquals(cartPage.getCartItemCount(), 3, "Cart should contain exactly 3 items.");

        List<String> cartNames = cartPage.getAllCartItemNames();
        Assert.assertTrue(cartNames.contains("Sauce Labs Backpack"));
        Assert.assertTrue(cartNames.contains("Sauce Labs Bike Light"));
        Assert.assertTrue(cartNames.contains("Sauce Labs Bolt T-Shirt"));
    }

    @Test(description = "TC_CRT_004: Sequential Badge Count Progression")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies sequential transitions of header badge counter (0 -> 1 -> 2 -> 3).")
    public void testSequentialBadgeIncrement() {
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0);

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 2);

        inventoryPage.addProductToCart("Sauce Labs Onesie");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 3);
    }

    @Test(description = "TC_CRT_005: Remove Product Inside Cart View")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies removing an item inside /cart.html deletes the row and decrements badge count from 2 to 1.")
    public void testRemoveItemInsideCart() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");

        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();
        Assert.assertEquals(cartPage.getCartItemCount(), 2);

        // Remove Backpack inside cart view
        cartPage.removeItem("Sauce Labs Backpack");

        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart item count should drop to 1.");
        Assert.assertFalse(cartPage.isItemDisplayedInCart("Sauce Labs Backpack"), "Backpack should no longer be in cart.");
        Assert.assertTrue(cartPage.isItemDisplayedInCart("Sauce Labs Bike Light"), "Bike Light must remain in cart.");
        Assert.assertEquals(cartPage.getHeaderComponent().getCartBadgeCount(), 1, "Badge must decrement to 1.");
    }

    @Test(description = "TC_CRT_006: Remove Product from Inventory View")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking 'Remove' on catalog card resets button to 'Add to cart' and deletes badge.")
    public void testRemoveItemDirectlyFromInventory() {
        String targetProduct = "Sauce Labs Fleece Jacket";
        inventoryPage.addProductToCart(targetProduct);
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

        inventoryPage.removeProductFromCart(targetProduct);
        Assert.assertTrue(inventoryPage.isProductAddToCartButtonDisplayed(targetProduct),
                "Button did not revert to 'Add to cart'.");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0,
                "Cart badge should be purged.");
    }

    @Test(description = "TC_CRT_009: Zero-Badge DOM Element Deletion")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that when cart count reaches 0, the .shopping_cart_badge element is removed from DOM.")
    public void testZeroBadgeDOMDeletion() {
        String targetProduct = "Sauce Labs Backpack";
        inventoryPage.addProductToCart(targetProduct);
        Assert.assertTrue(inventoryPage.getHeaderComponent().isCartBadgeDisplayed());

        inventoryPage.removeProductFromCart(targetProduct);

        // Assert that the badge is physically absent/not displayed (does NOT show '0')
        Assert.assertFalse(inventoryPage.getHeaderComponent().isCartBadgeDisplayed(),
                "Badge element should not be displayed in DOM when cart is empty.");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0);
    }

    @Test(description = "TC_CRT_010: Empty Cart Table Layout")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies navigating to /cart.html with zero items renders 0 rows while keeping navigation buttons active.")
    public void testEmptyCartLayout() {
        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();

        Assert.assertTrue(cartPage.isPageLoaded());
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart table should contain 0 items.");
        Assert.assertTrue(cartPage.isContinueShoppingButtonDisplayed(), "'Continue Shopping' button must be displayed.");
        Assert.assertTrue(cartPage.isCheckoutButtonDisplayed(), "'Checkout' button must be displayed.");
    }

    @Test(description = "TC_CRT_011: Cart Contents Verbatim Attribute Match")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the price, quantity, and name in the cart match the catalog card verbatim.")
    public void testCartItemVerbatimIntegrity() {
        String targetProduct = "Sauce Labs Backpack";
        String catalogPrice = inventoryPage.getProductPrice(targetProduct);

        inventoryPage.addProductToCart(targetProduct);
        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();

        Assert.assertTrue(cartPage.isItemDisplayedInCart(targetProduct));
        Assert.assertEquals(cartPage.getItemQuantity(targetProduct), "1", "Default item quantity must be 1.");
        Assert.assertEquals(cartPage.getItemPrice(targetProduct), catalogPrice, "Item price in cart does not match catalog.");
    }

    @Test(description = "TC_CRT_016: Application State Reset via Menu Purges Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies triggering 'Reset App State' from burger drawer purges cart and resets catalog buttons.")
    public void testResetAppStatePurgesCart() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 2);

        inventoryPage.resetAppState();

        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0, "Badge count must reset to 0.");
        Assert.assertTrue(inventoryPage.isProductAddToCartButtonDisplayed("Sauce Labs Backpack"));

        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart view should contain 0 items post reset.");
    }
}