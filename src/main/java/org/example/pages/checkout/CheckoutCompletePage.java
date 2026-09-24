package org.example.pages.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.example.components.FooterComponent;
import org.example.components.HeaderComponent;
import org.example.pages.inventory.InventoryPage;
import org.openqa.selenium.By;

/**
 * Page Object representing the Order Confirmation Page (/checkout-complete.html).
 */
public class CheckoutCompletePage extends BasePage {

    private static final Logger log = LogManager.getLogger(CheckoutCompletePage.class);

    private final HeaderComponent headerComponent;
    private final FooterComponent footerComponent;

    // --- Locators ---
    private final By completeContainer = By.id("checkout_complete_container");
    private final By completeHeader = By.className("complete-header");
    private final By completeText = By.className("complete-text");
    private final By backHomeButton = By.id("back-to-products");

    public CheckoutCompletePage() {
        super();
        this.headerComponent = new HeaderComponent();
        this.footerComponent = new FooterComponent();
    }

    public boolean isPageLoaded() {
        return elementActions.isDisplayed(completeContainer)
                && browserActions.getCurrentUrl().contains("/checkout-complete.html");
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public String getConfirmationHeader() {
        return elementActions.getText(completeHeader).trim();
    }

    public String getConfirmationText() {
        return elementActions.getText(completeText).trim();
    }

    public InventoryPage clickBackHome() {
        log.info("Clicking 'Back Home' to return to catalog");
        elementActions.click(backHomeButton);
        return new InventoryPage();
    }
}