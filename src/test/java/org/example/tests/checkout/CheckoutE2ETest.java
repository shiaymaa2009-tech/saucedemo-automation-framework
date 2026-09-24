package org.example.tests.checkout;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.base.BaseTest;
import org.example.models.CheckoutCustomer;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.example.pages.cart.CartPage;
import org.example.pages.checkout.CheckoutCompletePage;
import org.example.pages.checkout.CheckoutStepOnePage;
import org.example.pages.checkout.CheckoutStepTwoPage;
import org.example.pages.inventory.InventoryPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Epic("SauceDemo E-Commerce")
@Feature("Module 5: End-to-End Checkout Flow (TS-SD-CHK-001)")
public class CheckoutE2ETest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void setUpCheckoutSession() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(UserCredentials.standardUser());

        inventoryPage = new InventoryPage();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Precondition failed: Inventory page did not load.");
    }

    /**
     * Primary Scope of Work - Step 4: End-to-End Checkout
     * Fulfills TC_CHK_001, TC_CHK_002, TC_CHK_010, TC_CHK_011, TC_CHK_012, TC_CHK_013, TC_CHK_014.
     */
    @Test(description = "Step 4: End-to-End Checkout Happy Path with Totals & Confirmation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Executes full checkout workflow: add product, checkout, fill customer, verify product/subtotal/tax/total, finish, confirm order, and verify cart is purged.")
    public void testEndToEndCheckout() {
        String targetProduct = "Sauce Labs Backpack";
        double expectedItemPrice = 29.99;
        double expectedTax = 2.40;
        double expectedTotal = 32.39;

        // 1. Add a product to the cart
        inventoryPage.addProductToCart(targetProduct);
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1);

        // 2. Open the cart and select Checkout
        inventoryPage.getHeaderComponent().clickCart();
        CartPage cartPage = new CartPage();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page failed to load.");
        cartPage.clickCheckout();

        // 3. Enter valid customer information & Continue
        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        Assert.assertTrue(stepOne.isPageLoaded(), "Step One (Customer Info) page failed to load.");
        stepOne.fillCustomerInformation(CheckoutCustomer.standardCustomer());
        CheckoutStepTwoPage stepTwo = stepOne.clickContinue();

        // 4. Continue to Overview page and verify selected product and order totals
        Assert.assertTrue(stepTwo.isPageLoaded(), "Step Two (Overview) page failed to load.");

        // 4a. Verify the selected products
        Assert.assertEquals(stepTwo.getItemCount(), 1, "Overview should list exactly 1 product.");
        Assert.assertTrue(stepTwo.getAllItemNames().contains(targetProduct), "Target product not in overview.");

        // 4b. Verify the subtotal
        Assert.assertEquals(stepTwo.getSubtotal(), expectedItemPrice, 0.001, "Item subtotal mismatch.");

        // 4c. Verify the tax
        Assert.assertEquals(stepTwo.getTax(), expectedTax, 0.001, "Tax calculation mismatch.");

        // 4d. Verify the total
        Assert.assertEquals(stepTwo.getTotal(), expectedTotal, 0.001, "Total amount mismatch.");

        // 5. Click Finish
        CheckoutCompletePage completePage = stepTwo.clickFinish();

        // 6. Verify the order confirmation
        Assert.assertTrue(completePage.isPageLoaded(), "Order Complete page failed to load.");
        Assert.assertEquals(completePage.getConfirmationHeader(), "Thank you for your order!",
                "Order confirmation header mismatch.");

        // 7. Verify cart is purged post-completion
        Assert.assertFalse(completePage.getHeaderComponent().isCartBadgeDisplayed(),
                "Cart badge must be purged from DOM post-order.");
        completePage.getHeaderComponent().clickCart();
        CartPage emptyCart = new CartPage();
        Assert.assertEquals(emptyCart.getCartItemCount(), 0, "Cart should contain 0 items post-order.");
    }

    /**
     * TC_CHK_009: Multi-Item Order Summary Listing
     */
    @Test(description = "TC_CHK_009: Multi-Item Order Summary Listing on Step Two Overview")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that multiple products added to cart are accurately enumerated on Step Two Overview.")
    public void testOrderSummaryItemEnumeration() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");

        inventoryPage.getHeaderComponent().clickCart();
        new CartPage().clickCheckout();

        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        stepOne.fillCustomerInformation(CheckoutCustomer.standardCustomer());
        CheckoutStepTwoPage stepTwo = stepOne.clickContinue();

        Assert.assertEquals(stepTwo.getItemCount(), 2);
        List<String> items = stepTwo.getAllItemNames();
        Assert.assertTrue(items.contains("Sauce Labs Backpack"));
        Assert.assertTrue(items.contains("Sauce Labs Bike Light"));
    }

    /**
     * TC_CHK_010, 011, 012: Mathematical Totals and 8% Tax Accuracy
     */
    @Test(description = "TC_CHK_010, 011, 012: Multi-Item Subtotal, 8% Tax & Total Mathematical Addition")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies mathematical addition of multiple items, 8% tax calculation formula, and Subtotal + Tax == Total.")
    public void testMathematicalTotalAccuracy() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");       // $29.99
        inventoryPage.addProductToCart("Sauce Labs Bike Light");      // $9.99
        inventoryPage.addProductToCart("Sauce Labs Fleece Jacket");   // $49.99

        inventoryPage.getHeaderComponent().clickCart();
        new CartPage().clickCheckout();

        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        stepOne.fillCustomerInformation(CheckoutCustomer.standardCustomer());
        CheckoutStepTwoPage stepTwo = stepOne.clickContinue();

        // 1. Verify calculated sum matches displayed subtotal ($89.97)
        List<Double> itemPrices = stepTwo.getAllItemPrices();
        double calculatedSum = 0.0;
        for (Double price : itemPrices) {
            calculatedSum += price;
        }
        double displayedSubtotal = stepTwo.getSubtotal();
        Assert.assertEquals(displayedSubtotal, calculatedSum, 0.001, "Displayed subtotal does not match sum of items.");

        // 2. Verify 8% tax calculation: round(Subtotal * 0.08, 2) ($7.20)
        double expectedTax = Math.round(displayedSubtotal * 0.08 * 100.0) / 100.0;
        double displayedTax = stepTwo.getTax();
        Assert.assertEquals(displayedTax, expectedTax, 0.01, "Tax calculation does not match 8% formula.");

        // 3. Verify Total formula: Subtotal + Tax == Total ($97.17)
        double expectedTotal = Math.round((displayedSubtotal + displayedTax) * 100.0) / 100.0;
        double displayedTotal = stepTwo.getTotal();
        Assert.assertEquals(displayedTotal, expectedTotal, 0.001, "Grand total does not equal Subtotal + Tax.");
    }

    /**
     * TC_CHK_003: Missing First Name Validation
     */
    @Test(description = "TC_CHK_003: Missing First Name Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies error banner when First Name is empty: 'Error: First Name is required'.")
    public void testMissingFirstNameValidation() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.getHeaderComponent().clickCart();
        new CartPage().clickCheckout();

        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        stepOne.enterLastName("Doe").enterPostalCode("12345");
        stepOne.clickContinueExpectingFailure();

        Assert.assertTrue(stepOne.isErrorMessageDisplayed());
        Assert.assertEquals(stepOne.getErrorMessage(), "Error: First Name is required");
    }

    /**
     * TC_CHK_004: Missing Last Name Validation
     */
    @Test(description = "TC_CHK_004: Missing Last Name Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies error banner when Last Name is empty: 'Error: Last Name is required'.")
    public void testMissingLastNameValidation() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.getHeaderComponent().clickCart();
        new CartPage().clickCheckout();

        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        stepOne.enterFirstName("John").enterPostalCode("12345");
        stepOne.clickContinueExpectingFailure();

        Assert.assertTrue(stepOne.isErrorMessageDisplayed());
        Assert.assertEquals(stepOne.getErrorMessage(), "Error: Last Name is required");
    }

    /**
     * TC_CHK_005: Missing Postal Code Validation
     */
    @Test(description = "TC_CHK_005: Missing Postal Code Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies error banner when Postal Code is empty: 'Error: Postal Code is required'.")
    public void testMissingPostalCodeValidation() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.getHeaderComponent().clickCart();
        new CartPage().clickCheckout();

        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        stepOne.enterFirstName("John").enterLastName("Doe");
        stepOne.clickContinueExpectingFailure();

        Assert.assertTrue(stepOne.isErrorMessageDisplayed());
        Assert.assertEquals(stepOne.getErrorMessage(), "Error: Postal Code is required");
    }

    /**
     * TC_CHK_007: All Fields Empty Validation Priority
     */
    @Test(description = "TC_CHK_007: All Required Fields Empty Priority Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that when all fields are empty, First Name has priority error display.")
    public void testAllFieldsEmptyValidationPriority() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.getHeaderComponent().clickCart();
        new CartPage().clickCheckout();

        CheckoutStepOnePage stepOne = new CheckoutStepOnePage();
        stepOne.clickContinueExpectingFailure();

        Assert.assertTrue(stepOne.isErrorMessageDisplayed());
        Assert.assertEquals(stepOne.getErrorMessage(), "Error: First Name is required");
    }
}