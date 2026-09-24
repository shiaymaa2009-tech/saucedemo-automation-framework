package org.example.base;

import org.example.config.ConfigReader;
import org.example.driver.DriverFactory;
import org.example.driver.DriverManager;
import org.example.listeners.TestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);
        driver.get(ConfigReader.getBaseUrl());
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
        driver = null;
    }
}