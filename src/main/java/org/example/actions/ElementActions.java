package org.example.actions;

import org.example.driver.DriverManager;
import org.example.waits.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ElementActions {

    private final WaitUtils waitUtils = new WaitUtils();

    // --- Locator-Based Methods (Primary) ---

    public void click(By locator) {
        waitUtils.waitForClickability(locator).click();
    }

    public void type(By locator, String text) {
        WebElement element = waitUtils.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void clear(By locator) {
        waitUtils.waitForVisibility(locator).clear();
    }

    public String getText(By locator) {
        return waitUtils.waitForVisibility(locator).getText();
    }

    public boolean isDisplayed(By locator) {
        try {
            return !DriverManager.getDriver().findElements(locator).isEmpty()
                    && DriverManager.getDriver().findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // --- WebElement-Based Overloads ---

    public void click(WebElement element) {
        waitUtils.waitForClickability(element).click();
    }

    public void type(WebElement element, String text) {
        WebElement el = waitUtils.waitForVisibility(element);
        el.clear();
        el.sendKeys(text);
    }

    public void clear(WebElement element) {
        waitUtils.waitForVisibility(element).clear();
    }

    public String getText(WebElement element) {
        return waitUtils.waitForVisibility(element).getText();
    }
}