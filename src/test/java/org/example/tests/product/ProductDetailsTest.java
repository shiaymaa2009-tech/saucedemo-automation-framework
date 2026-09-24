package org.example.tests.product;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.base.BaseTest;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.example.pages.inventory.InventoryPage;
import org.example.pages.product.ProductDetailsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("SauceDemo E-Commerce")
@Feature("Module 3: Product Details (TS-SD-DTL-001)")
public class ProductDetailsTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void setUpInventorySession() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(UserCredentials.standardUser());

        inventoryPage = new InventoryPage();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Precondition failed: Inventory page did not load.");
    }

    @Test(description = "TC_DTL_001: Product Details Drilldown Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking product title navigates to details URL containing 'inventory-item.html?id='.")
    public void testProductDetailsNavigation() {
        String targetItem = "Sauce Labs Backpack";
        ProductDetailsPage detailsPage = inventoryPage.clickProductTitle(targetItem);

        Assert.assertTrue(detailsPage.isPageLoaded(), "Product details page did not load successfully.");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory-item.html?id="),
                "URL does not contain expected inventory-item query parameter.");
        Assert.assertEquals(detailsPage.getProductName(), targetItem, "Product title mismatch on details page.");
    }

    @Test(description = "TC_DTL_003: Verbatim Product Data Integrity")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies product title, price, and description on details view match the catalog card verbatim.")
    public void testVerbatimProductDataIntegrity() {
        String targetItem = "Sauce Labs Backpack";
        String catalogPrice = inventoryPage.getProductPrice(targetItem);
        String catalogDesc = inventoryPage.getProductDescription(targetItem);

        ProductDetailsPage detailsPage = inventoryPage.clickProductTitle(targetItem);

        Assert.assertEquals(detailsPage.getProductName(), targetItem, "Product title mismatch.");
        Assert.assertEquals(detailsPage.getProductPrice(), catalogPrice, "Product price mismatch.");

        // Normalize whitespace so text wrapping across responsive containers does not cause a false discrepancy
        String detailsDescNormalized = detailsPage.getProductDescription().replaceAll("\\s+", " ").trim();
        String catalogDescNormalized = catalogDesc.replaceAll("\\s+", " ").trim();
        Assert.assertEquals(detailsDescNormalized, catalogDescNormalized,
                "Product description on details page does not match catalog card.");
    }

    @Test(description = "TC_DTL_004: Hero Image Asset Rendering")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the product details hero image renders with positive natural dimensions (non-broken asset).")
    public void testHeroImageAssetRendering() {
        ProductDetailsPage detailsPage = inventoryPage.clickProductTitle("Sauce Labs Fleece Jacket");
        Assert.assertTrue(detailsPage.isImageRenderedProperly(),
                "Product hero image failed to render or returned 0x0 natural dimensions.");
    }

    @Test(description = "TC_DTL_005: Add to Cart from Details Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking 'Add to cart' on details page increments badge to 1 and flips button to 'Remove'.")
    public void testAddToCartFromDetailsPage() {
        ProductDetailsPage detailsPage = inventoryPage.clickProductTitle("Sauce Labs Bike Light");
        Assert.assertEquals(detailsPage.getHeaderComponent().getCartBadgeCount(), 0, "Initial cart badge should be 0.");

        detailsPage.clickAddToCart();
        Assert.assertEquals(detailsPage.getHeaderComponent().getCartBadgeCount(), 1, "Cart badge count must increment to 1.");
        Assert.assertTrue(detailsPage.isRemoveButtonDisplayed(), "Action button did not flip to 'Remove'.");

        detailsPage.clickRemove();
        Assert.assertEquals(detailsPage.getHeaderComponent().getCartBadgeCount(), 0, "Cart badge must reset to 0 after removal.");
        Assert.assertTrue(detailsPage.isAddToCartButtonDisplayed(), "Action button did not revert to 'Add to cart'.");
    }

    @Test(description = "TC_DTL_007: Return Navigation via 'Back to products'")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that clicking the '#back-to-products' button safely returns the user to the catalog grid.")
    public void testReturnNavigationViaBackToProducts() {
        ProductDetailsPage detailsPage = inventoryPage.clickProductTitle("Sauce Labs Onesie");
        InventoryPage returnedInventory = detailsPage.clickBackToProducts();

        Assert.assertTrue(returnedInventory.isPageLoaded(), "Failed to return to Inventory catalog.");
        Assert.assertEquals(returnedInventory.getItemCount(), 6, "Inventory catalog grid must render all 6 items upon return.");
    }

    @Test(description = "TC_DTL_008: Bidirectional State Sync: Details -> Catalog")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that an item added to cart on the details page reflects with a red 'Remove' button on the catalog card.")
    public void testBidirectionalStateSync() {
        String targetItem = "Sauce Labs Bolt T-Shirt";
        ProductDetailsPage detailsPage = inventoryPage.clickProductTitle(targetItem);
        detailsPage.clickAddToCart();
        Assert.assertEquals(detailsPage.getHeaderComponent().getCartBadgeCount(), 1, "Badge count must be 1 on details page.");

        InventoryPage returnedInventory = detailsPage.clickBackToProducts();

        Assert.assertTrue(returnedInventory.isProductRemoveButtonDisplayed(targetItem),
                "Catalog card for '" + targetItem + "' must display 'Remove' button.");
        Assert.assertEquals(returnedInventory.getHeaderComponent().getCartBadgeCount(), 1,
                "Cart badge count must persist as 1 when returning to catalog.");
    }
}