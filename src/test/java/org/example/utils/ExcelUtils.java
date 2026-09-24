package org.example.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class ExcelUtils {

    private ExcelUtils() {
    }

    /**
     * Reads all rows from a sheet tab.
     */
    public static Object[][] getSheetData(String resourcePath, String sheetName) {
        return getFilteredData(resourcePath, sheetName, null);
    }

    /**
     * Reads rows from a sheet filtered by ScenarioType (Column Index 1).
     * Extracts:
     * - Column Index 2: Username
     * - Column Index 3: Password
     * - Column Index 5: ExpectedErrorMessage
     *
     * Returns: Object[][] with 3 parameters matching LoginTest methods.
     */
    public static Object[][] getFilteredData(String resourcePath, String sheetName, String scenarioTypeFilter) {
        try (InputStream is = ExcelUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Excel file not found on classpath: " + resourcePath);
            }

            try (Workbook workbook = WorkbookFactory.create(is)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalArgumentException("Sheet tab '" + sheetName + "' not found in " + resourcePath);
                }

                DataFormatter formatter = new DataFormatter();
                int totalRows = sheet.getLastRowNum();
                List<Object[]> dataList = new ArrayList<>();

                // Skip header row (row index 0)
                for (int i = 1; i <= totalRows; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }

                    // In our 6-column AllCredentials sheet:
                    // Col 0 = TestCaseID
                    // Col 1 = ScenarioType (e.g. VALID, INVALID, EMPTY)
                    // Col 2 = Username
                    // Col 3 = Password
                    // Col 4 = ExpectedResult
                    // Col 5 = ExpectedErrorMessage
                    String scenarioType = (row.getCell(1) == null) ? "" : formatter.formatCellValue(row.getCell(1)).trim();
                    String username     = (row.getCell(2) == null) ? "" : formatter.formatCellValue(row.getCell(2)).trim();
                    String password     = (row.getCell(3) == null) ? "" : formatter.formatCellValue(row.getCell(3)).trim();
                    String expectedErr  = (row.getCell(5) == null) ? "" : formatter.formatCellValue(row.getCell(5)).trim();

                    // Filter by ScenarioType if a filter is provided
                    if (scenarioTypeFilter == null || scenarioType.equalsIgnoreCase(scenarioTypeFilter)) {
                        dataList.add(new Object[]{ username, password, expectedErr });
                    }
                }

                Object[][] result = new Object[dataList.size()][3];
                for (int i = 0; i < dataList.size(); i++) {
                    result[i] = dataList.get(i);
                }
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel data from: " + resourcePath + " [Sheet: " + sheetName + "]", e);
        }
    }
}