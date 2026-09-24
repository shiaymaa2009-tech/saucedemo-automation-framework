package org.example.pages.inventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.example.components.FooterComponent;
import org.example.components.HeaderComponent;
import org.example.pages.product.ProductDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise Page Object for the SauceDemo Catalog / Inventory Page (/inventory.html).
 * Encapsulates catalog interactions, dynamic scraping, 4-way sorting, button CSS auditing,
 * image thumbnail navigation, and state reset actions.
 */
public class InventoryPage extends BasePage {

    private static final Logger log = LogManager.getLogger(InventoryPage.class);

    private final HeaderComponent headerComponent;
    private final FooterComponent footerComponent;

    // --- Locators: Catalog Grid ---
    private final By inventoryContainer = By.id("inventory_container");
    private final By inventoryList = By.className("inventory_list");
    private final By inventoryItems = By.className("inventory_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By itemPrices = By.className("inventory_item_price");
    private final By itemDescriptions = By.className("inventory_item_desc");
    private final By itemImages = By.cssSelector(".inventory_item_img img");
    private final By sortDropdown = By.className("product_sort_container");
    private final By activeSortOption = By.className("active_option");

    // --- Locators: Hamburger Menu & Reset Actions ---
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By resetAppStateLink = By.id("reset_sidebar_link");
    private final By closeMenuButton = By.id("react-burger-cross-btn");

    public InventoryPage() {
        super();
        this.headerComponent = new HeaderComponent();
        this.footerComponent = new FooterComponent();
    }

    public boolean isPageLoaded() {
        return elementActions.isDisplayed(inventoryContainer)
                && browserActions.getCurrentUrl().contains("/inventory.html");
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public int getItemCount() {
        return driver.findElements(inventoryItems).size();
    }

    public List<String> getAllProductNames() {
        List<WebElement> elements = driver.findElements(itemNames);
        List<String> names = new ArrayList<>();
        for (WebElement el : elements) {
            names.add(el.getText().trim());
        }
        return names;
    }

    public List<Double> getAllProductPrices() {
        List<WebElement> elements = driver.findElements(itemPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement el : elements) {
            String rawPrice = el.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(rawPrice));
        }
        return prices;
    }

    public List<String> getAllProductImageSources() {
        List<WebElement> elements = driver.findElements(itemImages);
        List<String> sources = new ArrayList<>();
        for (WebElement el : elements) {
            sources.add(el.getAttribute("src"));
        }
        return sources;
    }

    public InventoryPage selectSortOption(String sortValue) {
        log.info("Selecting catalog sort option by value: '{}'", sortValue);
        WebElement dropdownElement = waitUtils.waitForVisibility(sortDropdown);
        Select select = new Select(dropdownElement);
        select.selectByValue(sortValue);
        waitUtils.waitForVisibility(inventoryList);
        return this;
    }

    public String getActiveSortText() {
        return elementActions.getText(activeSortOption).trim();
    }

    public ProductDetailsPage clickProductTitle(String productName) {
        log.info("Navigating to product details via title: '{}'", productName);
        By productLink = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']");
        elementActions.click(productLink);
        return new ProductDetailsPage();
    }

    public ProductDetailsPage clickProductImage(String productName) {
        log.info("Navigating to product details via image thumbnail for: '{}'", productName);
        By imageLink = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//div[contains(@class, 'inventory_item_img')]//a");
        elementActions.click(imageLink);
        return new ProductDetailsPage();
    }

    public InventoryPage addProductToCart(String productName) {
        log.info("Adding '{}' to cart from inventory", productName);
        By addToCartBtn = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//button[contains(@id, 'add-to-cart')]");
        elementActions.click(addToCartBtn);
        return this;
    }

    public InventoryPage removeProductFromCart(String productName) {
        log.info("Removing '{}' from cart from inventory", productName);
        By removeBtn = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//button[contains(@id, 'remove')]");
        elementActions.click(removeBtn);
        return this;
    }

    public boolean isProductRemoveButtonDisplayed(String productName) {
        By removeBtn = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//button[contains(@id, 'remove')]");
        return elementActions.isDisplayed(removeBtn);
    }

    public boolean isProductAddToCartButtonDisplayed(String productName) {
        By addBtn = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//button[contains(@id, 'add-to-cart')]");
        return elementActions.isDisplayed(addBtn);
    }

    public String getProductButtonText(String productName) {
        By buttonLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//button");
        return elementActions.getText(buttonLocator).trim();
    }

    public String getProductButtonColor(String productName) {
        By buttonLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//button");
        return driver.findElement(buttonLocator).getCssValue("color");
    }

    public String getProductPrice(String productName) {
        By priceLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//div[contains(@class, 'inventory_item_price')]");
        return elementActions.getText(priceLocator).trim();
    }

    public String getProductDescription(String productName) {
        By descLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item_label')]//div[contains(@class, 'inventory_item_desc')]");
        return elementActions.getText(descLocator).trim();
    }

    public boolean isProductImageRenderedProperly(String productName) {
        By imgLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//div[contains(@class, 'inventory_item_img')]//img");
        WebElement imgElement = waitUtils.waitForVisibility(imgLocator);
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                    "return (typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0 && arguments[0].naturalHeight > 0);",
                    imgElement
            );
        } catch (Exception e) {
            return false;
        }
    }

    public InventoryPage hoverOverProductImage(String productName) {
        By imgLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//div[contains(@class, 'inventory_item_img')]//img");
        WebElement imgElement = waitUtils.waitForVisibility(imgLocator);
        new Actions(driver).moveToElement(imgElement).perform();
        return this;
    }

    public String getProductImageCursorStyle(String productName) {
        By imgLinkLocator = By.xpath("//div[contains(@class, 'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class, 'inventory_item')]//div[contains(@class, 'inventory_item_img')]//a");
        return driver.findElement(imgLinkLocator).getCssValue("cursor");
    }

    public InventoryPage scrollToTop() {
        log.info("Scrolling back to top of catalog");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo({top: 0, behavior: 'smooth'});");
        waitUtils.waitForVisibility(inventoryContainer);
        return this;
    }

    public InventoryPage resetAppState() {
        log.info("Triggering 'Reset App State' from burger menu");
        elementActions.click(menuButton);
        waitUtils.waitForClickability(resetAppStateLink);
        elementActions.click(resetAppStateLink);
        elementActions.click(closeMenuButton);
        waitUtils.waitForInvisibility(resetAppStateLink);
        return this;
    }
}