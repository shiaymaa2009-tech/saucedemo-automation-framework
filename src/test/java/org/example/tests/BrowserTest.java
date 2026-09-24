package org.example.tests;

import org.example.base.BaseTest;
import org.testng.annotations.Test;

public class BrowserTest extends BaseTest {

    @Test
    public void openSauceDemo() {
        System.out.println(driver.getTitle());
    }
}