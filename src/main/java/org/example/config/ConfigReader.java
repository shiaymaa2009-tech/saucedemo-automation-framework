package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Could not find " + CONFIG_FILE + " on the classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    private ConfigReader() {
    }

    public static String getProperty(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }
        String propValue = properties.getProperty(key);
        if (propValue == null) {
            throw new IllegalArgumentException("Property key '" + key + "' not defined in " + CONFIG_FILE);
        }
        return propValue.trim();
    }

    public static String getBaseUrl() {
        return getProperty("baseUrl");
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicitWait"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("pageLoadTimeout"));
    }

    public static String getScreenshotDir() {
        return getProperty("screenshotDir");
    }
}