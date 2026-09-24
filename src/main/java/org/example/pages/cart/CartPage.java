package org.example.pages.cart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.example.components.FooterComponent;
import org.example.components.HeaderComponent;
import org.example.pages.inventory.InventoryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing the SauceDemo Shopping Cart view (/cart.html).
 * Encapsulates cart table enumeration, item removal, and navigation transitions.
 */
public class CartPage extends BasePage {

    private static final Logger log = LogManager.getLogger(CartPage.class);

    private final HeaderComponent headerComponent;
    private final FooterComponent footerComponent;

    // --- Locators ---
    private final By cartContainer = By.id("cart_contents_container");
    private final By cartItems = By.className("cart_item");
    private final By cartItemNames = By.className("inventory_item_name");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");

    public CartPage() {
        super();
        this.headerComponent = new HeaderComponent();
        this.footerComponent = new FooterComponent();
    }

    public boolean isPageLoaded() {
        return elementActions.isDisplayed(cartContainer)
                && browserActions.getCurrentUrl().contains("/cart.html");
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public List<String> getAllCartItemNames() {
        List<WebElement> elements = driver.findElements(cartItemNames);
        List<String> names = new ArrayList<>();
        for (WebElement el : elements) {
            names.add(el.getText().trim());
        }
        return names;
    }

    public boolean isItemDisplayedInCart(String productName) {
        By itemLink = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']");
        return elementActions.isDisplayed(itemLink);
    }

    public String getItemPrice(String productName) {
        By priceLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[@class='cart_item']//div[@class='inventory_item_price']");
        return elementActions.getText(priceLocator).trim();
    }

    public String getItemDescription(String productName) {
        By descLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[@class='cart_item']//div[@class='inventory_item_desc']");
        return elementActions.getText(descLocator).trim();
    }

    public String getItemQuantity(String productName) {
        By qtyLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[@class='cart_item']//div[@class='cart_quantity']");
        return elementActions.getText(qtyLocator).trim();
    }

    public CartPage removeItem(String productName) {
        log.info("Removing item from cart inside Cart view: '{}'", productName);
        By removeBtn = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[@class='cart_item']//button[contains(@id, 'remove')]");
        elementActions.click(removeBtn);
        return this;
    }

    public InventoryPage clickContinueShopping() {
        log.info("Clicking 'Continue Shopping' to return to catalog");
        elementActions.click(continueShoppingButton);
        return new InventoryPage();
    }

    public void clickCheckout() {
        log.info("Clicking 'Checkout' to begin checkout workflow");
        elementActions.click(checkoutButton);
    }

    public boolean isCheckoutButtonDisplayed() {
        return elementActions.isDisplayed(checkoutButton);
    }

    public boolean isContinueShoppingButtonDisplayed() {
        return elementActions.isDisplayed(continueShoppingButton);
    }
}