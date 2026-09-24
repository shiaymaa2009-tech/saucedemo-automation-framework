package org.example.pages.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.example.components.FooterComponent;
import org.example.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing Checkout Step Two: Overview & Summary (/checkout-step-two.html).
 */
public class CheckoutStepTwoPage extends BasePage {

    private static final Logger log = LogManager.getLogger(CheckoutStepTwoPage.class);

    private final HeaderComponent headerComponent;
    private final FooterComponent footerComponent;

    // --- Locators ---
    private final By summaryContainer = By.id("checkout_summary_container");
    private final By cartItems = By.className("cart_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By itemPrices = By.className("inventory_item_price");
    private final By subtotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.className("summary_total_label");
    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");

    public CheckoutStepTwoPage() {
        super();
        this.headerComponent = new HeaderComponent();
        this.footerComponent = new FooterComponent();
    }

    public boolean isPageLoaded() {
        return elementActions.isDisplayed(summaryContainer)
                && browserActions.getCurrentUrl().contains("/checkout-step-two.html");
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public int getItemCount() {
        return driver.findElements(cartItems).size();
    }

    public List<String> getAllItemNames() {
        List<WebElement> elements = driver.findElements(itemNames);
        List<String> names = new ArrayList<>();
        for (WebElement el : elements) {
            names.add(el.getText().trim());
        }
        return names;
    }

    public List<Double> getAllItemPrices() {
        List<WebElement> elements = driver.findElements(itemPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement el : elements) {
            String rawPrice = el.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(rawPrice));
        }
        return prices;
    }

    /**
     * Extracts numeric subtotal from string: "Item total: $29.99" -> 29.99
     */
    public double getSubtotal() {
        String rawText = elementActions.getText(subtotalLabel);
        return parsePrice(rawText);
    }

    /**
     * Extracts numeric tax from string: "Tax: $2.40" -> 2.40
     */
    public double getTax() {
        String rawText = elementActions.getText(taxLabel);
        return parsePrice(rawText);
    }

    /**
     * Extracts numeric grand total from string: "Total: $32.39" -> 32.39
     */
    public double getTotal() {
        String rawText = elementActions.getText(totalLabel);
        return parsePrice(rawText);
    }

    private double parsePrice(String text) {
        String cleaned = text.substring(text.indexOf('$') + 1).trim();
        return Double.parseDouble(cleaned);
    }

    public CheckoutCompletePage clickFinish() {
        log.info("Clicking 'Finish' to complete order");
        elementActions.click(finishButton);
        return new CheckoutCompletePage();
    }
}