package org.example.base;

import org.example.actions.BrowserActions;
import org.example.actions.ElementActions;
import org.example.driver.DriverManager;
import org.example.waits.WaitUtils;
import org.openqa.selenium.WebDriver;

public class BasePage {

    protected WebDriver driver;
    protected ElementActions elementActions;
    protected BrowserActions browserActions;
    protected WaitUtils waitUtils;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.elementActions = new ElementActions();
        this.browserActions = new BrowserActions();
        this.waitUtils = new WaitUtils();
    }
}