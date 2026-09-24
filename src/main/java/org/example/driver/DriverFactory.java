package org.example.driver;

import org.example.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        WebDriver driver;
        String browser = ConfigReader.getBrowser().toLowerCase();

        switch (browser) {
            case "edge":
                driver = createEdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        applyTimeouts(driver);
        return driver;
    }

    public static EdgeOptions createEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        options.addArguments("--disable-notifications");
       options.addArguments("--guest");
        return options;
    }

    private static WebDriver createEdgeDriver() {
        return new EdgeDriver(createEdgeOptions());
    }

    private static void applyTimeouts(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    }
}