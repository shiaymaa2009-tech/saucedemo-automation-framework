package org.example.components;

import org.example.base.BasePage;
import org.openqa.selenium.By;

public class MenuComponent extends BasePage {

    private final By burgerMenuButton = By.id("react-burger-menu-btn");
    private final By closeButton = By.id("react-burger-cross-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By resetAppStateLink = By.id("reset_sidebar_link");
    private final By allItemsLink = By.id("inventory_sidebar_link");

    public MenuComponent() {
        super();
    }

    public void openMenu() {
        elementActions.click(burgerMenuButton);
        waitUtils.waitForClickability(logoutLink);
    }

    public void closeMenu() {
        elementActions.click(closeButton);
        waitUtils.waitForInvisibility(logoutLink);
    }

    public void clickLogout() {
        openMenu();
        elementActions.click(logoutLink);
    }

    public void clickResetAppState() {
        openMenu();
        elementActions.click(resetAppStateLink);
    }

    public void clickAllItems() {
        openMenu();
        elementActions.click(allItemsLink);
    }

    public boolean isMenuOpen() {
        return elementActions.isDisplayed(logoutLink);
    }
}