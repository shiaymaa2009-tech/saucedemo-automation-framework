package org.example.pages.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.example.components.FooterComponent;
import org.example.components.HeaderComponent;
import org.example.pages.inventory.InventoryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Enterprise Page Object representing the Product Details view (/inventory-item.html?id=X).
 * Handles verbatim attribute assertions, image rendering, hover simulation,
 * button color assertions, and bi-directional navigation.
 */
public class ProductDetailsPage extends BasePage {

    private static final Logger log = LogManager.getLogger(ProductDetailsPage.class);

    private final HeaderComponent headerComponent;
    private final FooterComponent footerComponent;

    // --- Locators ---
    private final By backToProductsButton = By.id("back-to-products");
    private final By productName = By.className("inventory_details_name");
    private final By productDescription = By.className("inventory_details_desc");
    private final By productPrice = By.className("inventory_details_price");
    private final By productImage = By.cssSelector(".inventory_details_img");
    private final By productImageContainer = By.className("inventory_details_img_container");
    private final By addToCartButton = By.cssSelector("button[id^='add-to-cart']");
    private final By removeButton = By.cssSelector("button[id^='remove']");

    public ProductDetailsPage() {
        super();
        this.headerComponent = new HeaderComponent();
        this.footerComponent = new FooterComponent();
    }

    public boolean isPageLoaded() {
        return elementActions.isDisplayed(backToProductsButton)
                && browserActions.getCurrentUrl().contains("/inventory-item.html");
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public String getProductName() {
        return elementActions.getText(productName).trim();
    }

    public String getProductDescription() {
        return elementActions.getText(productDescription).trim();
    }

    public String getProductPrice() {
        return elementActions.getText(productPrice).trim();
    }

    public String getProductImageSource() {
        WebElement img = waitUtils.waitForVisibility(productImage);
        return img.getAttribute("src");
    }

    public boolean isImageRenderedProperly() {
        WebElement imgElement = waitUtils.waitForVisibility(productImage);
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                    "return (typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0 && arguments[0].naturalHeight > 0);",
                    imgElement
            );
        } catch (Exception e) {
            log.error("Failed to inspect product image natural dimensions: {}", e.getMessage());
            return false;
        }
    }

    public boolean hoverOverHeroImageAndVerifyDimensions() {
        log.info("Hovering mouse cursor over product hero image");
        WebElement imgElement = waitUtils.waitForVisibility(productImage);
        new Actions(driver).moveToElement(imgElement).perform();

        int width = imgElement.getSize().getWidth();
        int height = imgElement.getSize().getHeight();
        log.info("Inspected hero image rendered viewport dimensions: {}x{} px", width, height);
        return width > 150 && height > 150;
    }

    public String getHeroImageCursorStyle() {
        WebElement img = waitUtils.waitForVisibility(productImage);
        return img.getCssValue("cursor");
    }

    public ProductDetailsPage clickAddToCart() {
        log.info("Clicking 'Add to cart' on Details Page");
        elementActions.click(addToCartButton);
        return this;
    }

    public ProductDetailsPage clickRemove() {
        log.info("Clicking 'Remove' on Details Page");
        elementActions.click(removeButton);
        return this;
    }

    public boolean isRemoveButtonDisplayed() {
        return elementActions.isDisplayed(removeButton);
    }

    public boolean isAddToCartButtonDisplayed() {
        return elementActions.isDisplayed(addToCartButton);
    }

    public String getActionButtonColor() {
        By buttonLocator = isRemoveButtonDisplayed() ? removeButton : addToCartButton;
        return driver.findElement(buttonLocator).getCssValue("color");
    }

    public InventoryPage clickBackToProducts() {
        log.info("Navigating back to inventory catalog via '#back-to-products'");
        elementActions.click(backToProductsButton);
        return new InventoryPage();
    }
}