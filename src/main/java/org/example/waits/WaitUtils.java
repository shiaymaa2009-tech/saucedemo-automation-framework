package org.example.waits;

import org.example.config.ConfigReader;
import org.example.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    private final int explicitWaitSeconds;

    public WaitUtils() {
        this.explicitWaitSeconds = ConfigReader.getExplicitWait();
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(explicitWaitSeconds));
    }

    public WebElement waitForVisibility(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForVisibility(WebElement element) {
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForClickability(By locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickability(WebElement element) {
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement waitForPresence(By locator) {
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean waitForInvisibility(By locator) {
        return getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitForUrlContains(String fraction) {
        return getWait().until(ExpectedConditions.urlContains(fraction));
    }

    public boolean waitForTitleContains(String fraction) {
        return getWait().until(ExpectedConditions.titleContains(fraction));
    }
}