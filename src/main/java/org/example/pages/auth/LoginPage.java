package org.example.pages.auth;

import org.example.base.BasePage;
import org.example.models.UserCredentials;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class LoginPage extends BasePage {

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessageContainer = By.cssSelector("[data-test='error']");
    private final By errorCloseButton = By.cssSelector("button[data-test='error-button']");
    private final By errorIcons = By.cssSelector(".error_icon");

    public LoginPage() {
        super();
    }

    /**
     * Verifies that the Login page is loaded and ready for user interaction.
     */
    public boolean isPageLoaded() {
        return elementActions.isDisplayed(loginButton)
                && elementActions.isDisplayed(usernameInput);
    }

    public void enterUsername(String username) {
        elementActions.type(usernameInput, username);
    }

    public void enterPassword(String password) {
        elementActions.type(passwordInput, password);
    }

    public void clickLogin() {
        elementActions.click(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public void login(UserCredentials credentials) {
        login(credentials.getUsername(), credentials.getPassword());
    }

    public String getErrorMessage() {
        return elementActions.getText(errorMessageContainer);
    }

    public boolean isErrorMessageDisplayed() {
        return elementActions.isDisplayed(errorMessageContainer);
    }

    public void clickErrorCloseButton() {
        elementActions.click(errorCloseButton);
    }

    // --- Deep State & Visual Verification Helpers ---

    public boolean isUsernameInputInErrorState() {
        try {
            String classes = driver.findElement(usernameInput).getAttribute("class");
            return classes != null && classes.contains("input_error");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordInputInErrorState() {
        try {
            String classes = driver.findElement(passwordInput).getAttribute("class");
            return classes != null && classes.contains("input_error");
        } catch (Exception e) {
            return false;
        }
    }

    public int getErrorIconCount() {
        return driver.findElements(errorIcons).size();
    }

    public String getSessionUsernameCookie() {
        Cookie cookie = driver.manage().getCookieNamed("session-username");
        return (cookie != null) ? cookie.getValue() : null;
    }
    public String getPasswordInputType() {
        return driver.findElement(passwordInput).getAttribute("type");
    }

    public void clickPasswordVisibilityToggle() {
        WebElement passwordField = driver.findElement(passwordInput);

        int xOffset = (passwordField.getSize().getWidth() / 2) - 10;
        int yOffset = 0;

        new Actions(driver)
                .moveToElement(passwordField, xOffset, yOffset)
                .click()
                .perform();
    }
}
/*package org.example.pages.auth;

import org.example.base.BasePage;
import org.example.models.UserCredentials;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessageContainer = By.cssSelector("[data-test='error']");
    private final By errorCloseButton = By.cssSelector("button[data-test='error-button']");

    public void enterUsername(String username) {
        elementActions.type(usernameInput, username);
    }

    public void enterPassword(String password) {
        elementActions.type(passwordInput, password);
    }

    public void clickLogin() {
        elementActions.click(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public void login(UserCredentials credentials) {
        login(credentials.getUsername(), credentials.getPassword());
    }

    public String getErrorMessage() {
        return elementActions.getText(errorMessageContainer);
    }

    public boolean isErrorMessageDisplayed() {
        return elementActions.isDisplayed(errorMessageContainer);
    }

    public void clickErrorCloseButton() {
        elementActions.click(errorCloseButton);
    }
}*/