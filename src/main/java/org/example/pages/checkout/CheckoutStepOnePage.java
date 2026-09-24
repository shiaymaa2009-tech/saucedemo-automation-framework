package org.example.pages.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.base.BasePage;
import org.example.components.FooterComponent;
import org.example.components.HeaderComponent;
import org.example.models.CheckoutCustomer;
import org.openqa.selenium.By;

/**
 * Page Object representing Checkout Step One: Customer Information (/checkout-step-one.html).
 */
public class CheckoutStepOnePage extends BasePage {

    private static final Logger log = LogManager.getLogger(CheckoutStepOnePage.class);

    private final HeaderComponent headerComponent;
    private final FooterComponent footerComponent;

    // --- Locators ---
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public CheckoutStepOnePage() {
        super();
        this.headerComponent = new HeaderComponent();
        this.footerComponent = new FooterComponent();
    }

    public boolean isPageLoaded() {
        return elementActions.isDisplayed(firstNameInput)
                && browserActions.getCurrentUrl().contains("/checkout-step-one.html");
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        elementActions.type(firstNameInput, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        elementActions.type(lastNameInput, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        elementActions.type(postalCodeInput, postalCode);
        return this;
    }

    public void fillCustomerInformation(CheckoutCustomer customer) {
        enterFirstName(customer.getFirstName());
        enterLastName(customer.getLastName());
        enterPostalCode(customer.getPostalCode());
    }

    public CheckoutStepTwoPage clickContinue() {
        log.info("Submitting customer information and continuing to Step Two");
        elementActions.click(continueButton);
        return new CheckoutStepTwoPage();
    }

    public CheckoutStepOnePage clickContinueExpectingFailure() {
        elementActions.click(continueButton);
        return this;
    }

    public String getErrorMessage() {
        return elementActions.getText(errorMessage).trim();
    }

    public boolean isErrorMessageDisplayed() {
        return elementActions.isDisplayed(errorMessage);
    }
}