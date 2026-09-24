package org.example.utils;

import org.example.config.ConfigReader;
import org.example.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    public static void captureAndSaveToDisk(String name) {
        File screenshot = getScreenshotFile();
        if (screenshot == null) {
            return;
        }

        String screenshotDir = ConfigReader.getScreenshotDir();

        try {
            Files.createDirectories(Paths.get(screenshotDir));

            String fileName = name + "_"
                    + LocalDateTime.now().format(TIMESTAMP_FORMAT)
                    + ".png";

            Path destination = Paths.get(screenshotDir, fileName);
            Files.copy(screenshot.toPath(), destination);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot: " + e.getMessage(), e);
        }
    }

    public static byte[] captureAsBytes() {
        WebDriver driver = DriverManager.getDriver();
        if (driver instanceof TakesScreenshot) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        }
        return new byte[0];
    }

    private static File getScreenshotFile() {
        WebDriver driver = DriverManager.getDriver();
        if (driver instanceof TakesScreenshot) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        }
        return null;
    }
}