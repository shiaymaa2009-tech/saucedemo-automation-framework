package org.example.data;

import org.example.utils.ExcelUtils;
import org.testng.annotations.DataProvider;

public final class TestDataFactory {

    private static final String LOGIN_EXCEL_PATH = "testdata/LoginData.xlsx";
    private static final String SHEET_NAME = "AllCredentials";

    private TestDataFactory() {
    }

    @DataProvider(name = "invalidLoginData")
    public static Object[][] getInvalidLoginData() {
        return ExcelUtils.getFilteredData(LOGIN_EXCEL_PATH, SHEET_NAME, "INVALID");
    }

    @DataProvider(name = "emptyCredentialData")
    public static Object[][] getEmptyCredentialData() {
        return ExcelUtils.getFilteredData(LOGIN_EXCEL_PATH, SHEET_NAME, "EMPTY");
    }

    @DataProvider(name = "allCredentialsData")
    public static Object[][] getAllCredentialsData() {
        return ExcelUtils.getSheetData(LOGIN_EXCEL_PATH, SHEET_NAME);
    }
}