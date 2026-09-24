package org.example.tests.auth;

import io.qameta.allure.*;
import org.example.base.BaseTest;
import org.example.components.HeaderComponent;
import org.example.config.ConfigReader;
import org.example.data.TestDataFactory;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Authentication, Authorization & Session Management")
@Feature("Login Module (TS-SD-LGN-001)")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private HeaderComponent headerComponent;

    @BeforeMethod
    public void initPages() {
        loginPage = new LoginPage();
        headerComponent = new HeaderComponent();
    }

    @Test(description = "TC_LGN_001: Valid Standard User Login with Cookie & Header Verification")
    @Story("TC_LGN_001")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify valid login redirects to /inventory.html, displays 'Products' title, and creates session-username cookie.")
    public void testValidStandardUserLogin() {
        loginPage.login(UserCredentials.standardUser());

        // Deep Assertion 1: URL redirection
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "User was not redirected to /inventory.html after valid login.");

        // Deep Assertion 2: Header Title Verification
        Assert.assertEquals(headerComponent.getPageTitle(), "Products",
                "Header title does not display 'Products'.");

        // Deep Assertion 3: Backend Session Cookie Verification
        Assert.assertEquals(loginPage.getSessionUsernameCookie(), "standard_user",
                "Browser cookie 'session-username' was not created with value 'standard_user'.");
    }

    @Test(
            dataProvider = "invalidLoginData",
            dataProviderClass = TestDataFactory.class,
            description = "TC_LGN_002, 003, 004: Invalid Credentials Rejection with Error Styling"
    )
    @Story("TC_LGN_002_003_004")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that invalid credentials display error banner, red input borders, and error icons.")
    public void testInvalidCredentialsRejection(String username, String password, String expectedErrorMessage) {
        loginPage.login(username, password);

        // Deep Assertion 1: Error Banner Visibility
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error banner was not displayed for credentials: [" + username + " / " + password + "]");

        // Deep Assertion 2: Verbatim Error Message Text
        Assert.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage,
                "Error message text does not match expected string.");

        // Deep Assertion 3: Input Field Error Styling (.input_error class)
        Assert.assertTrue(loginPage.isUsernameInputInErrorState(),
                "Username input was not highlighted with error styling.");
        Assert.assertTrue(loginPage.isPasswordInputInErrorState(),
                "Password input was not highlighted with error styling.");

        // Deep Assertion 4: Red 'X' Error Icons Rendered
        Assert.assertTrue(loginPage.getErrorIconCount() >= 2,
                "Expected at least 2 error icons on the input fields.");
    }

    @Test(description = "TC_LGN_005: Locked-Out Account Access Denial")
    @Story("TC_LGN_005")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that locked_out_user is blocked from entering inventory with exact lockout message.")
    public void testLockedOutAccountDenial() {
        loginPage.login(UserCredentials.lockedOutUser());

        // 1. Assert user remains on login page and is strictly blocked from inventory
        Assert.assertFalse(driver.getCurrentUrl().contains("/inventory.html"),
                "Security failure: Locked-out user was allowed to navigate to inventory!");

        // 2. Assert error banner visibility and exact lockout text
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error banner was not displayed.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: Sorry, this user has been locked out.",
                "Lockout error message text is incorrect.");
    }

    @Test(
            dataProvider = "emptyCredentialData",
            dataProviderClass = TestDataFactory.class,
            description = "TC_LGN_006, 007, 008: Mandatory Field Validation & Priority Check"
    )
    @Story("TC_LGN_006_007_008")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that omitting credentials enforces prioritized required field messages and error styling.")
    public void testEmptyCredentialsValidation(String username, String password, String expectedErrorMessage) {
        loginPage.login(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error banner did not appear.");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage,
                "Missing field validation message does not match expected hierarchy.");

        Assert.assertTrue(loginPage.isUsernameInputInErrorState(),
                "Username field should show red error styling.");
    }

    @Test(description = "TC_LGN_009: Valid Login: Problem User Persona with Defect Verification")
    @Story("TC_LGN_009")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify problem_user authenticates and renders the known image defect (sl-404).")
    public void testProblemUserPersonaLogin() {
        loginPage.login(UserCredentials.problemUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Problem user was not redirected to inventory catalog.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");

        // Verify the problem_user defect is present (asset name contains 'sl-404' regardless of webpack hash)
        String firstImageSrc = driver.findElement(By.cssSelector(".inventory_item_img img")).getAttribute("src");
        Assert.assertTrue(firstImageSrc.contains("sl-404"),
                "Problem user defect not captured: expected broken image asset containing 'sl-404', but was: " + firstImageSrc);
    }

    @Test(description = "TC_LGN_010: Valid Login: Performance Glitch User (Latency Profile)")
    @Story("TC_LGN_010")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that performance_glitch_user delay is absorbed by dynamic explicit wait without timeout.")
    public void testPerformanceGlitchUserLogin() {
        loginPage.login(UserCredentials.performanceGlitchUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Performance glitch user timed out or failed to redirect.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
        Assert.assertEquals(loginPage.getSessionUsernameCookie(), "performance_glitch_user");
    }

    @Test(description = "TC_LGN_011: Valid Login: Error User Persona")
    @Story("TC_LGN_011")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that error_user authenticates and lands on inventory catalog.")
    public void testErrorUserPersonaLogin() {
        loginPage.login(UserCredentials.errorUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Error user failed to authenticate.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_012: Valid Login: Visual User Persona")
    @Story("TC_LGN_012")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that visual_user authenticates and lands on inventory catalog.")
    public void testVisualUserPersonaLogin() {
        loginPage.login(UserCredentials.visualUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Visual user failed to authenticate.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_013: Unauthenticated Direct Route Guard Check")
    @Story("TC_LGN_013")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify direct GET request to /inventory.html without active session redirects with error.")
    public void testUnauthenticatedRouteGuardCheck() {
        String inventoryUrl = ConfigReader.getBaseUrl() + "inventory.html";
        driver.get(inventoryUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Route guard failed to display error banner on unauthorized access.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.",
                "Route guard error message text is incorrect.");
        Assert.assertNull(loginPage.getSessionUsernameCookie(),
                "Unauthorized user should not have a session cookie.");
    }

    @Test(description = "TC_LGN_017: Form Submission via Keyboard 'Enter' Key")
    @Story("TC_LGN_017")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that pressing the keyboard 'Enter' key inside the password field submits the login form.")
    public void testFormSubmissionViaKeyboardEnterKey() {
        loginPage.enterUsername("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce" + Keys.ENTER);

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Form submission via Enter key failed to redirect user.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_018: Error Dismissal and Form Recovery Flow")
    @Story("TC_LGN_018")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify clicking 'X' dismisses error banner, and subsequent valid login succeeds.")
    public void testErrorDismissalAndFormRecovery() {
        loginPage.clickLogin(); // Trigger empty username error
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error banner did not appear.");

        loginPage.clickErrorCloseButton();
        Assert.assertFalse(loginPage.isErrorMessageDisplayed(), "Error banner was not dismissed.");

        // Form recovery
        loginPage.login(UserCredentials.standardUser());
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "User failed to log in after dismissing error banner.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_021: Password Masking")
    @Story("TC_LGN_021")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that entered password characters remain masked before using the visibility toggle.")
    public void testPasswordInputMasking() {
        loginPage.enterPassword("secret_sauce");

        Assert.assertEquals(
                loginPage.getPasswordInputType(),
                "password",
                "Password field is not masked after entering the password."
        );
    }

    // =========================================================================
    // ADVANCED EDGE CASES: SECURITY GUARDS, OVERLAYS & SESSION BOUNDARIES
    // =========================================================================

    @Test(description = "TC_LGN_SEC_01: Stale Session Invalidation & Deep-Link URL Guard")
    @Story("TC_LGN_SECURITY")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that if session tokens are invalidated/cleared and a user pastes the direct authenticated URL, it forces redirect to login.")
    public void testStaleSessionDirectUrlAccessBlocked() {
        // 1. Authenticate with standard_user
        loginPage.login(UserCredentials.standardUser());
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"), "Initial login failed.");

        // 2. Simulate closing browser/session expiry by clearing all session cookies
        driver.manage().deleteAllCookies();

        // 3. Attempt to paste/navigate directly to the protected inventory URL
        String protectedUrl = ConfigReader.getBaseUrl() + "inventory.html";
        driver.get(protectedUrl);

        // 4. Assert route guard halts access and redirects back to login page
        Assert.assertTrue(driver.getCurrentUrl().equals(ConfigReader.getBaseUrl()) || driver.getCurrentUrl().endsWith("/"),
                "Security violation: Stale session was allowed to view protected inventory URL!");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Route guard error message was not displayed.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.",
                "Incorrect route guard message displayed.");
    }

    @Test(description = "TC_LGN_SEC_02: Browser Native Back-Button Invalidation Post-Logout")
    @Story("TC_LGN_SECURITY")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that clicking browser native 'Back' post-logout does not render cached inventory data.")
    public void testBrowserBackButtonSessionDenialPostLogout() {
        // 1. Authenticate
        loginPage.login(UserCredentials.standardUser());
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"));

        // 2. Perform logout via hamburger menu
        org.example.components.MenuComponent menu = new org.example.components.MenuComponent();
        menu.clickLogout();
        Assert.assertTrue(driver.getCurrentUrl().equals(ConfigReader.getBaseUrl()) || driver.getCurrentUrl().endsWith("/"),
                "Logout did not redirect to login page.");

        // 3. Trigger native browser Back button
        driver.navigate().back();

        // 4. Assert system rejects entry to cached inventory page
        Assert.assertFalse(driver.getCurrentUrl().contains("/inventory.html"),
                "Security vulnerability: Browser back button bypassed logout and displayed cached inventory view!");
        Assert.assertNull(loginPage.getSessionUsernameCookie(),
                "Session cookie must remain destroyed after back-navigation attempt.");
    }

    @Test(description = "TC_LGN_UX_01: Error Overlay Integrity - Error Banner and Icons Do Not Block Inputs")
    @Story("TC_LGN_UX_COLLISION")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that when red error banners and SVG 'X' icons render, they do NOT overlay or intercept user clicks/typing on inputs.")
    public void testErrorOverlayDoesNotBlockFormControls() {
        // 1. Trigger error state with invalid input so red banner and 'X' icons appear
        loginPage.login("invalid_user", "invalid_password");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error banner did not appear.");
        Assert.assertTrue(loginPage.getErrorIconCount() >= 2, "Error icons did not appear.");

        // 2. Attempt to directly click, clear, and type into Username and Password through the error icons
        // If an overlay or z-index bug exists, Selenium will throw ElementClickInterceptedException!
        try {
            loginPage.enterUsername("standard_user");
            loginPage.enterPassword("secret_sauce");
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            Assert.fail("UI Defect: Error icons or error banner overlaid input fields and blocked pointer interaction!");
        }

        // 3. Verify that the login button is also clickable and not obscured by the error banner
        loginPage.clickLogin();

        // 4. Assert recovery succeeds into inventory
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Form submission failed after retyping credentials through error state.");
    }

    @Test(description = "TC_LGN_SEC_03: Cross-User Session and Cookie Isolation")
    @Story("TC_LGN_SECURITY")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that logging out of standard_user and logging into problem_user completely overwrites session tokens without state bleed.")
    public void testCrossUserSessionIsolation() {
        // 1. Authenticate standard_user
        loginPage.login(UserCredentials.standardUser());
        Assert.assertEquals(loginPage.getSessionUsernameCookie(), "standard_user", "Initial cookie mismatch.");

        // 2. Logout
        org.example.components.MenuComponent menu = new org.example.components.MenuComponent();
        menu.clickLogout();

        // 3. Authenticate with a different persona: problem_user
        loginPage.login(UserCredentials.problemUser());
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"));

        // 4. Assert session cookie is strictly updated to problem_user with zero standard_user residual data
        Assert.assertEquals(loginPage.getSessionUsernameCookie(), "problem_user",
                "Session isolation failed: Cookie was not updated to problem_user!");
    }


    @Test(description = "TC_LGN_022: Password Visibility Toggle")
    @Story("TC_LGN_022")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the password visibility toggle changes the password field from masked to visible and back to masked.")
    public void testPasswordVisibilityToggle() {
        loginPage.enterPassword("secret_sauce");

        Assert.assertEquals(
                loginPage.getPasswordInputType(),
                "password",
                "Password should initially be masked."
        );

        loginPage.clickPasswordVisibilityToggle();

        Assert.assertEquals(
                loginPage.getPasswordInputType(),
                "text",
                "Password was not revealed after clicking the visibility toggle."
        );

        loginPage.clickPasswordVisibilityToggle();

        Assert.assertEquals(
                loginPage.getPasswordInputType(),
                "password",
                "Password was not masked again after clicking the visibility toggle."
        );
    }

    @Test(description = "TC_LGN_023: Password Toggle Interaction During Error State")
    @Story("TC_LGN_023")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the password visibility toggle remains interactive when the login error message is displayed.")
    public void testPasswordToggleInteractionDuringErrorState() {
        loginPage.enterUsername("invalid_user");
        loginPage.enterPassword("invalid_password");
        loginPage.clickLogin();

        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "Error banner was not displayed."
        );

        Assert.assertEquals(
                loginPage.getPasswordInputType(),
                "password",
                "Password should remain masked after invalid login."
        );

        // This click should reach the password visibility toggle.
        // If the error overlay covers/intercepts it, the test should fail.
        loginPage.clickPasswordVisibilityToggle();

        Assert.assertEquals(
                loginPage.getPasswordInputType(),
                "text",
                "Password visibility toggle could not be used while the error message was displayed."
        );
    }
}
/*package org.example.tests.auth;

import io.qameta.allure.*;
import org.example.base.BaseTest;
import org.example.components.HeaderComponent;
import org.example.config.ConfigReader;
import org.example.data.TestDataFactory;
import org.example.models.UserCredentials;
import org.example.pages.auth.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Authentication, Authorization & Session Management")
@Feature("Login Module (TS-SD-LGN-001)")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private HeaderComponent headerComponent;

    @BeforeMethod
    public void initPages() {
        loginPage = new LoginPage();
        headerComponent = new HeaderComponent();
    }

    @Test(description = "TC_LGN_001: Valid Standard User Login")
    @Story("TC_LGN_001")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that standard_user authenticates successfully and redirects to /inventory.html with 'Products' title.")
    public void testValidStandardUserLogin() {
        loginPage.login(UserCredentials.standardUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "User was not redirected to /inventory.html after valid login.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products",
                "Header title does not display 'Products'.");
    }

    @Test(
            dataProvider = "invalidLoginData",
            dataProviderClass = TestDataFactory.class,
            description = "TC_LGN_002, 003, 004: Invalid Credentials Rejection (Data-Driven from Excel)"
    )
    @Story("TC_LGN_002_003_004")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that invalid username, invalid password, or both are rejected with exact error banner.")
    public void testInvalidCredentialsRejection(String username, String password, String expectedErrorMessage) {
        loginPage.login(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error banner was not displayed for credentials: [" + username + " / " + password + "]");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage,
                "Error message text does not match expected string.");
    }

    @Test(description = "TC_LGN_005: Locked-Out Account Access Denial")
    @Story("TC_LGN_005")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that locked_out_user is blocked with 'Epic sadface: Sorry, this user has been locked out.'")
    public void testLockedOutAccountDenial() {
        loginPage.login(UserCredentials.lockedOutUser());

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error banner was not displayed for locked out user.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: Sorry, this user has been locked out.",
                "Lockout error message text is incorrect.");
    }

    @Test(
            dataProvider = "emptyCredentialData",
            dataProviderClass = TestDataFactory.class,
            description = "TC_LGN_006, 007, 008: Mandatory Field Validation & Empty Hierarchy (Data-Driven from Excel)"
    )
    @Story("TC_LGN_006_007_008")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that omitting username, password, or both enforces prioritized required field validation.")
    public void testEmptyCredentialsValidation(String username, String password, String expectedErrorMessage) {
        loginPage.login(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error banner was not displayed for empty input: [" + username + " / " + password + "]");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage,
                "Missing field validation message does not match expected hierarchy.");
    }

    @Test(description = "TC_LGN_009: Valid Login: Problem User Persona")
    @Story("TC_LGN_009")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that problem_user authenticates and initializes profile on /inventory.html.")
    public void testProblemUserPersonaLogin() {
        loginPage.login(UserCredentials.problemUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Problem user was not redirected to inventory catalog.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_010: Valid Login: Performance Glitch User (Latency Profile)")
    @Story("TC_LGN_010")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that performance_glitch_user delay is absorbed by dynamic explicit wait without timeout.")
    public void testPerformanceGlitchUserLogin() {
        loginPage.login(UserCredentials.performanceGlitchUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Performance glitch user timed out or failed to redirect.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_011: Valid Login: Error User Persona")
    @Story("TC_LGN_011")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that error_user authenticates and lands on inventory catalog.")
    public void testErrorUserPersonaLogin() {
        loginPage.login(UserCredentials.errorUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Error user failed to authenticate.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_012: Valid Login: Visual User Persona")
    @Story("TC_LGN_012")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that visual_user authenticates and lands on inventory catalog.")
    public void testVisualUserPersonaLogin() {
        loginPage.login(UserCredentials.visualUser());

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Visual user failed to authenticate.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_013: Unauthenticated Direct Route Guard Check")
    @Story("TC_LGN_013")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that direct GET request to /inventory.html without active session redirects to login with error.")
    public void testUnauthenticatedRouteGuardCheck() {
        String inventoryUrl = ConfigReader.getBaseUrl() + "inventory.html";
        driver.get(inventoryUrl);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Route guard failed to display error banner on unauthorized access.");
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.",
                "Route guard error message text is incorrect.");
    }

    @Test(description = "TC_LGN_017: Form Submission via Keyboard 'Enter' Key")
    @Story("TC_LGN_017")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that pressing the keyboard 'Enter' key inside the password field submits the login form.")
    public void testFormSubmissionViaKeyboardEnterKey() {
        loginPage.enterUsername("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce" + Keys.ENTER);

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Form submission via Enter key failed to redirect user.");
        Assert.assertEquals(headerComponent.getPageTitle(), "Products");
    }

    @Test(description = "TC_LGN_018: Error Dismissal and Form Recovery Flow")
    @Story("TC_LGN_018")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that clicking 'X' dismisses the error banner, and subsequent valid login succeeds.")
    public void testErrorDismissalAndFormRecovery() {
        loginPage.clickLogin(); // Trigger empty username error
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error banner did not appear.");

        loginPage.clickErrorCloseButton();
        Assert.assertFalse(loginPage.isErrorMessageDisplayed(), "Error banner was not dismissed.");

        // Form recovery
        loginPage.login(UserCredentials.standardUser());
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "User failed to log in after dismissing error banner.");
    }

    @Test(description = "TC_LGN_021: Password Masking and Input Security")
    @Story("TC_LGN_021")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that password input possesses HTML attribute type='password' to shield password observation.")
    public void testPasswordInputMasking() {
        String inputType = driver.findElement(By.id("password")).getAttribute("type");
        Assert.assertEquals(inputType, "password",
                "Password field is unmasked (HTML type attribute is not 'password').");
    }
}*/