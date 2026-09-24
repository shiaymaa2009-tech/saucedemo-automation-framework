package org.example.listeners;

import io.qameta.allure.Attachment;
import org.example.utils.ScreenshotUtils;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        ScreenshotUtils.captureAndSaveToDisk(result.getName());
        attachFailureScreenshot();
    }

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public byte[] attachFailureScreenshot() {
        return ScreenshotUtils.captureAsBytes();
    }
}