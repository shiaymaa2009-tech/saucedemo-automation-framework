# SauceDemo Automation Framework

A Selenium-based test automation framework for the SauceDemo web application, built with Java, Maven, TestNG, and Page Object Model (POM).

The framework is designed to support maintainable, reusable, and data-driven UI automation with logging, screenshots, and Allure reporting.

## Tech Stack

* Java
* Selenium WebDriver
* TestNG
* Maven
* Page Object Model (POM)
* Allure Reports
* Log4j2
* Apache POI
* Git / GitHub

## Project Structure

```text
Saucedemo_Automation_Framework/
│
├── .gitignore
├── pom.xml
├── README.md
├── AUTOMATION_FEASIBILITY_ANALYSIS.md
│
├── manual-tests/
│   └── SauceDemo_All_Test_Cases_Organized.xlsx
│
├── logs/
│   └── .gitkeep
│
├── screenshots/
│   └── failures/
│       └── .gitkeep
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── org.example/
    │   │       ├── actions/
    │   │       ├── base/
    │   │       ├── components/
    │   │       ├── config/
    │   │       ├── driver/
    │   │       ├── models/
    │   │       ├── pages/
    │   │       └── waits/
    │   │
    │   └── resources/
    │       ├── config.properties
    │       └── log4j2.xml
    │
    └── test/
        ├── java/
        │   └── org.example/
        │       ├── base/
        │       ├── data/
        │       ├── listeners/
        │       ├── tests/
        │       └── utils/
        │
        └── resources/
            ├── suites/
            │   └── testng.xml
            └── testdata/
                ├── LoginData.xlsx
                └── user_personas.json
```

## Framework Design

The framework follows the Page Object Model to separate test logic from page interactions.

### Main Components

**Pages**

Contain locators and page-specific actions.

Examples:

* `LoginPage`
* `InventoryPage`
* `ProductDetailsPage`
* `CartPage`
* `CheckoutStepOnePage`
* `CheckoutStepTwoPage`
* `CheckoutCompletePage`

**Actions**

Reusable browser and element interactions.

* `BrowserActions`
* `ElementActions`

**Driver**

Responsible for WebDriver creation and management.

* `DriverFactory`
* `DriverManager`

**Base Classes**

Provide common setup and reusable functionality.

* `BasePage`
* `BaseTest`

**Utilities**

Reusable framework utilities such as:

* Excel data handling
* Screenshots
* Explicit waits
* Test data generation

**Listeners**

`TestListener` handles test execution events and supports reporting/logging behavior.

## Test Coverage

The automation suite covers major SauceDemo workflows, including:

* Login
* Inventory / Product Listing
* Product Details
* Shopping Cart
* Checkout
* Logout
* Session behavior
* Browser-related scenarios

Both positive and negative scenarios are included where applicable.

## Test Data

Test data is stored separately from test logic.

### Excel

```text
src/test/resources/testdata/LoginData.xlsx
```

Used for login-related data-driven tests.

### JSON

```text
src/test/resources/testdata/user_personas.json
```

Contains user/persona test data used by the framework.

## Manual Test Cases

The manual test cases are provided in:

```text
manual-tests/SauceDemo_All_Test_Cases_Organized.xlsx
```

The file contains the organized manual test coverage used as the basis for the automation implementation.

## Requirements

Before running the project on another machine, install:

* JDK compatible with the project's Maven configuration
* Git
* IntelliJ IDEA or another Java IDE
* Maven, or use the Maven installation bundled with IntelliJ
* A supported web browser

The project dependencies are managed through `pom.xml`.

## Setup

Clone the repository:

```bash
git clone <repository-url>
```

Open the project in IntelliJ IDEA.

Allow Maven to load and download the required dependencies from `pom.xml`.

Verify that the project SDK is configured with the required Java version.

## Running the Tests

### Run the complete TestNG suite

From the project root:

```bash
mvn test -DsuiteXmlFile=src/test/resources/suites/testng.xml
```

### Run tests from IntelliJ

Open:

```text
src/test/resources/suites/testng.xml
```

and run the TestNG suite.

Individual test classes can also be executed directly from IntelliJ.

## Reports

The framework supports Allure reporting.

After test execution, Allure results are generated in:

```text
allure-results/
```

An Allure report can then be generated from the results using the Allure command-line tool.

The generated report folders are intentionally excluded from Git because they are build/test artifacts and can be recreated from the test results.

## Logs

Runtime logs are written to:

```text
logs/
```

The directory is included in the repository structure, while generated log files are not required to be version-controlled.

## Failure Screenshots

Screenshots captured for failed tests are stored under:

```text
screenshots/failures/
```

The directory is included in the repository structure, while generated screenshots are treated as test execution artifacts.

## Configuration

Framework configuration is stored in:

```text
src/main/resources/config.properties
```

Logging configuration is stored in:

```text
src/main/resources/log4j2.xml
```

Machine-specific paths should not be hardcoded into the framework so that the project can be cloned and executed on another machine.

## Git Repository

Generated and machine-specific files such as the following are excluded from version control:

```text
.idea/
target/
allure-results/
allure-report/
```

These files are generated locally when the project is opened, built, or executed.

## Notes

* Maven manages the project dependencies.
* The framework uses reusable Page Objects and utility classes to reduce duplication.
* Test data is separated from automation logic.
* Failure screenshots and logs are generated during execution.
* Allure reports can be regenerated from test results.
* The repository contains the source code and configuration required to reproduce the automation framework on another machine.
