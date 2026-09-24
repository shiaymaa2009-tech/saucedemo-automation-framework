package org.example.components;

import org.example.base.BasePage;
import org.openqa.selenium.By;

public class HeaderComponent extends BasePage {

    private final By shoppingCartLink = By.cssSelector("a.shopping_cart_link");
    private final By cartBadge = By.cssSelector("span.shopping_cart_badge");
    private final By pageTitle = By.cssSelector("span.title");

    public String getPageTitle() {
        return elementActions.getText(pageTitle);
    }

    public int getCartBadgeCount() {
        if (!isCartBadgeDisplayed()) {
            return 0;
        }
        return Integer.parseInt(elementActions.getText(cartBadge).trim());
    }

    public boolean isCartBadgeDisplayed() {
        return elementActions.isDisplayed(cartBadge);
    }

    public void clickCart() {
        elementActions.click(shoppingCartLink);
    }
}