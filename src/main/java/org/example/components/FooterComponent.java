package org.example.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/**
 * Reusable Component Object representing the global application footer.
 * Appears across Catalog, Product Details, Cart, and Checkout pages.
 */
public class FooterComponent extends BasePage {

    private static final Logger log = LogManager.getLogger(FooterComponent.class);

    // --- Locators (Resilient to class refactoring & rebranding) ---
    private final By footerContainer = By.className("footer");
    private final By twitterLink = By.cssSelector("footer a[href*='twitter'], footer a[href*='x.com'], [data-test='social-twitter']");
    private final By facebookLink = By.cssSelector("footer a[href*='facebook'], [data-test='social-facebook']");
    private final By linkedinLink = By.cssSelector("footer a[href*='linkedin'], [data-test='social-linkedin']");
    private final By footerCopy = By.className("footer_copy");

    public FooterComponent() {
        super();
    }

    /**
     * Smoothly scrolls the viewport until the footer is visible.
     */
    public FooterComponent scrollToFooter() {
        log.info("Scrolling smoothly to global footer component");
        WebElement footerEl = waitUtils.waitForPresence(footerContainer);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", footerEl);
        waitUtils.waitForVisibility(footerCopy);
        return this;
    }

    public boolean isDisplayed() {
        return elementActions.isDisplayed(footerContainer);
    }

    public String getCopyrightText() {
        return elementActions.getText(footerCopy).trim();
    }

    public String getTwitterHref() {
        return waitUtils.waitForPresence(twitterLink).getAttribute("href");
    }

    public String getFacebookHref() {
        return waitUtils.waitForPresence(facebookLink).getAttribute("href");
    }

    public String getLinkedInHref() {
        return waitUtils.waitForPresence(linkedinLink).getAttribute("href");
    }
}