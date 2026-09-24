package org.example.tests.inventory;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.base.BaseTest;
import org.example.config.ConfigReader;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.example.pages.inventory.InventoryPage;
import org.example.pages.product.ProductDetailsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Epic("SauceDemo E-Commerce")
@Feature("Module 2: Catalog, Sorting, Deep UI/UX & Asset Audit")
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void setUpInventorySession() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(UserCredentials.standardUser());

        inventoryPage = new InventoryPage();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Precondition failed: Inventory page did not load.");
    }

    @Test(description = "TC_CAT_001: 6-Item Catalog Grid Completeness & Asset Rendering")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies 6 cards render with titles, non-zero prices, descriptions, and non-broken images.")
    public void testCatalogRenderingCompleteness() {
        Assert.assertEquals(inventoryPage.getItemCount(), 6, "Inventory grid must strictly contain exactly 6 items.");

        List<String> names = inventoryPage.getAllProductNames();
        for (String name : names) {
            Assert.assertFalse(name.trim().isEmpty(), "Product name must not be blank.");
            Assert.assertTrue(inventoryPage.isProductImageRenderedProperly(name),
                    "Image failed to render with valid natural dimensions for product: " + name);
        }

        List<Double> prices = inventoryPage.getAllProductPrices();
        for (Double price : prices) {
            Assert.assertTrue(price > 0.0, "Product price must be greater than $0.00. Found: " + price);
        }
    }

    @Test(description = "TC_CAT_002: Alphabetical Sort: Name (A to Z)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies sorting by Name (A to Z) arranges products in case-insensitive ascending alphabetical order.")
    public void testSortNameAtoZ() {
        inventoryPage.selectSortOption("az");

        List<String> actualNames = inventoryPage.getAllProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames, String.CASE_INSENSITIVE_ORDER);

        Assert.assertEquals(actualNames, expectedNames, "Catalog failed to sort alphabetically (A to Z).");
        Assert.assertEquals(actualNames.get(0), "Sauce Labs Backpack");
        Assert.assertEquals(actualNames.get(actualNames.size() - 1), "Test.allTheThings() T-Shirt (Red)");
    }

    @Test(description = "TC_CAT_003: Reverse Sort: Name (Z to A)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies sorting by Name (Z to A) arranges products in case-insensitive descending alphabetical order.")
    public void testSortNameZtoA() {
        inventoryPage.selectSortOption("za");

        List<String> actualNames = inventoryPage.getAllProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER));

        Assert.assertEquals(actualNames, expectedNames, "Catalog failed to sort reverse alphabetically (Z to A).");
        Assert.assertEquals(actualNames.get(0), "Test.allTheThings() T-Shirt (Red)");
        Assert.assertEquals(actualNames.get(actualNames.size() - 1), "Sauce Labs Backpack");
    }

    @Test(description = "TC_CAT_004: Numerical Price Sort: Low to High")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies numerical ascending price order from $7.99 to $49.99.")
    public void testSortPriceLowToHigh() {
        inventoryPage.selectSortOption("lohi");

        List<Double> actualPrices = inventoryPage.getAllProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);

        Assert.assertEquals(actualPrices, expectedPrices, "Catalog failed to sort prices ascending.");
        Assert.assertEquals(actualPrices.get(0), 7.99);
        Assert.assertEquals(actualPrices.get(actualPrices.size() - 1), 49.99);
    }

    @Test(description = "TC_CAT_005: Numerical Price Sort: High to Low")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies numerical descending price order from $49.99 down to $7.99.")
    public void testSortPriceHighToLow() {
        inventoryPage.selectSortOption("hilo");

        List<Double> actualPrices = inventoryPage.getAllProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        expectedPrices.sort(Collections.reverseOrder());

        Assert.assertEquals(actualPrices, expectedPrices, "Catalog failed to sort prices descending.");
        Assert.assertEquals(actualPrices.get(0), 49.99);
        Assert.assertEquals(actualPrices.get(actualPrices.size() - 1), 7.99);
    }

    @Test(description = "TC_CAT_006: Tied-Price Deterministic Grouping ($15.99 Items)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that the two items with identical prices ($15.99) remain contiguously grouped.")
    public void testTiedPriceSorting() {
        inventoryPage.selectSortOption("lohi");
        List<Double> prices = inventoryPage.getAllProductPrices();

        int firstIndex = prices.indexOf(15.99);
        int lastIndex = prices.lastIndexOf(15.99);

        Assert.assertTrue(firstIndex != -1 && lastIndex != -1, "Tied price items of $15.99 not found.");
        Assert.assertEquals(lastIndex - firstIndex, 1, "The two $15.99 items must be directly adjacent in the list.");
    }

    @Test(description = "TC_CAT_007: Image Thumbnail Click Zone Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that clicking the product image thumbnail routes to the correct product details page.")
    public void testImageThumbnailClickNavigation() {
        String targetItem = "Sauce Labs Bike Light";
        ProductDetailsPage detailsPage = inventoryPage.clickProductImage(targetItem);

        Assert.assertTrue(detailsPage.isPageLoaded(), "Details page failed to load after clicking image thumbnail.");
        Assert.assertEquals(detailsPage.getProductName(), targetItem);
    }

    @Test(description = "TC_CAT_008: Button State & Visual CSS Color Transition on Card")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking 'Add to cart' flips text to 'Remove', changes color to red, and updates badge.")
    public void testButtonStateAndColorTransition() {
        String targetItem = "Sauce Labs Backpack";
        Assert.assertEquals(inventoryPage.getProductButtonText(targetItem), "Add to cart");

        inventoryPage.addProductToCart(targetItem);

        // Verify button flipped to 'Remove'
        Assert.assertEquals(inventoryPage.getProductButtonText(targetItem), "Remove");
        Assert.assertTrue(inventoryPage.isProductRemoveButtonDisplayed(targetItem));
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

        // Verify button text color turned red: rgb(226, 35, 26)
        String buttonColor = inventoryPage.getProductButtonColor(targetItem);
        Assert.assertTrue(buttonColor.contains("226") || buttonColor.contains("red"),
                "Remove button text color should turn red, found: " + buttonColor);

        // Toggle back to Add to cart
        inventoryPage.removeProductFromCart(targetItem);
        Assert.assertEquals(inventoryPage.getProductButtonText(targetItem), "Add to cart");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0);
    }

    @Test(description = "TC_CAT_009: Sequential Multi-Item Cart Badge Incrementation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies sequential badge progression 0 -> 1 -> 2 -> 3 as items are added.")
    public void testSequentialBadgeIncrement() {
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0);

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 2);

        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 3);
    }

    @Test(description = "TC_CAT_010: Rapid Cart Button Flapping Stability")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies rapid sequential toggle clicks on the same item card do not break DOM state.")
    public void testRapidButtonToggleStability() {
        String targetItem = "Sauce Labs Onesie";
        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(targetItem);
            Assert.assertEquals(inventoryPage.getProductButtonText(targetItem), "Remove");
            Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

            inventoryPage.removeProductFromCart(targetItem);
            Assert.assertEquals(inventoryPage.getProductButtonText(targetItem), "Add to cart");
            Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0);
        }
    }

    @Test(description = "TC_CAT_011: Image Thumbnail Hover & Pointer Cursor UX")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies hovering over image thumbnail displays a pointer cursor indicating clickability.")
    public void testImageHoverAndCursorUX() {
        String targetItem = "Sauce Labs Fleece Jacket";
        inventoryPage.hoverOverProductImage(targetItem);
        String cursor = inventoryPage.getProductImageCursorStyle(targetItem);
        Assert.assertEquals(cursor, "pointer", "Product image wrapper must exhibit pointer cursor.");
    }

    @Test(description = "TC_CAT_012: Viewport Scroll to Footer & Social Links Verification")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies smooth scrolling to footer, copyright text display, and external social media links via FooterComponent.")
    public void testScrollToFooterAndSocialMediaLinks() {
        inventoryPage.getFooterComponent().scrollToFooter();

        Assert.assertTrue(inventoryPage.getFooterComponent().isDisplayed(), "Footer container should be visible after scroll.");
        String copyright = inventoryPage.getFooterComponent().getCopyrightText();
        Assert.assertTrue(copyright.contains("Sauce Labs"), "Copyright text should mention 'Sauce Labs'. Found: " + copyright);

        String twitterHref = inventoryPage.getFooterComponent().getTwitterHref();
        Assert.assertTrue(twitterHref.contains("twitter") || twitterHref.contains("x.com"), "Twitter/X link malformed.");
        Assert.assertTrue(inventoryPage.getFooterComponent().getFacebookHref().contains("facebook.com"), "Facebook link malformed.");
        Assert.assertTrue(inventoryPage.getFooterComponent().getLinkedInHref().contains("linkedin.com"), "LinkedIn link malformed.");

        inventoryPage.scrollToTop();
    }

    @Test(description = "TC_CAT_013: App State Reset via Menu Purges Catalog Buttons")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies 'Reset App State' from burger drawer resets added items back to 'Add to cart'.")
    public void testResetAppStatePurgesCatalogButtons() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 2);

        inventoryPage.resetAppState();

        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0, "Cart badge must reset to 0.");
        Assert.assertTrue(inventoryPage.isProductAddToCartButtonDisplayed("Sauce Labs Backpack"));
        Assert.assertTrue(inventoryPage.isProductAddToCartButtonDisplayed("Sauce Labs Bike Light"));
    }

    @Test(description = "TC_CAT_014: Problem User Broken Asset Audit (Intentional Defect Detection)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Audits problem_user catalog assets, verifying that every product card loads the broken sl-404 dog asset.")
    public void testProblemUserBrokenAssetAudit() {
        // Log out standard user and authenticate as problem_user
        driver.manage().deleteAllCookies();
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage();
        loginPage.login(UserCredentials.problemUser());

        InventoryPage problemInventory = new InventoryPage();
        List<String> imageSources = problemInventory.getAllProductImageSources();
        Assert.assertEquals(imageSources.size(), 6);

        // Problem user intentionally serves sl-404 image for all items
        for (String src : imageSources) {
            Assert.assertTrue(src.contains("sl-404"), "Problem user must serve the broken sl-404 image asset.");
        }
    }

    @Test(description = "TC_CAT_011: Cart Icon Reflects Added Product Count")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the cart badge on the inventory page accurately reflects the number of products added.")
    public void testCartBadgeReflectsAddedProducts() {

        Assert.assertEquals(
                inventoryPage.getHeaderComponent().getCartBadgeCount(),
                0,
                "Cart badge should initially be 0."
        );

        inventoryPage.addProductToCart("Sauce Labs Backpack");

        Assert.assertEquals(
                inventoryPage.getHeaderComponent().getCartBadgeCount(),
                1,
                "Cart badge should display 1 after adding one product."
        );

        inventoryPage.addProductToCart("Sauce Labs Bike Light");

        Assert.assertEquals(
                inventoryPage.getHeaderComponent().getCartBadgeCount(),
                2,
                "Cart badge should display 2 after adding two products."
        );

        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");

        Assert.assertEquals(
                inventoryPage.getHeaderComponent().getCartBadgeCount(),
                3,
                "Cart badge should display 3 after adding three products."
        );
    }
}