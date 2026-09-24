package org.example.actions;

import org.example.driver.DriverManager;

public class BrowserActions {

    public void openUrl(String url) {
        DriverManager.getDriver().get(url);
    }

    public void refresh() {
        DriverManager.getDriver().navigate().refresh();
    }

    public void back() {
        DriverManager.getDriver().navigate().back();
    }

    public void forward() {
        DriverManager.getDriver().navigate().forward();
    }

    public void deleteCookies() {
        DriverManager.getDriver().manage().deleteAllCookies();
    }

    public String getCurrentUrl() {
        return DriverManager.getDriver().getCurrentUrl();
    }

    public String getPageTitle() {
        return DriverManager.getDriver().getTitle();
    }
}