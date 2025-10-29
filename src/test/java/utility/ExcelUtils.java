package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
    private static XSSFSheet ExcelWSheet;
    private static XSSFWorkbook ExcelWBook;
    private static XSSFCell Cell;
    private static XSSFRow Row;


    public static void setExcelFile(String Path, String SheetName) throws Exception {
        try {
            FileInputStream ExcelFile = new FileInputStream(Path);

            ExcelWBook = new XSSFWorkbook(ExcelFile);
            ExcelWSheet = ExcelWBook.getSheet(SheetName);

        } catch (Exception e) {
            throw (e);
        }
    }




    public static String getCellData(int RowNum, int ColNum) throws Exception {
        try {
            Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
            String CellData = Cell.getStringCellValue();
            return CellData;
        } catch (Exception e) {
            return "";
        }
    }



public static String getCellData(int RowNum, String ColumnName) throws Exception {
    try {
        XSSFRow headerRow = ExcelWSheet.getRow(0);
        int colNum = -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String headerName = headerRow.getCell(i).getStringCellValue().trim();
            if (headerName.equalsIgnoreCase(ColumnName)) {
                colNum = i;
                break;
            }
        }


        if (colNum == -1) {
            throw new Exception("Column '" + ColumnName + "' not found in Excel sheet.");
        }



        XSSFRow currentRow = ExcelWSheet.getRow(RowNum);
        if (currentRow == null) return "";


        XSSFCell cell = currentRow.getCell(colNum);
        if (cell == null) return "";


        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                long longValue = (long) numericValue;
                if (numericValue == longValue) {
                    return String.valueOf(longValue);
                } else {
                    return String.valueOf(numericValue);
                }



            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            default:
                return "";
        }
    } catch (Exception e) {
        return "";
    }
}

//XXXXXXXXXXX
    public static void setCellData(String Result, int RowNum, int ColNum) throws Exception {
        try {
            Row = ExcelWSheet.getRow(RowNum);
            if (Row == null) {
                Row = ExcelWSheet.createRow(RowNum);
            }
            Cell = Row.getCell(ColNum);
            if (Cell == null) {
                Cell = Row.createCell(ColNum);
            }
            Cell.setCellValue(Result);

            FileOutputStream fileOut = new FileOutputStream(Constant.Path_TestData + Constant.File_TestData);
            ExcelWBook.write(fileOut);
            fileOut.flush();
            fileOut.close();

        } catch (Exception e) {
            throw (e);
        }


    }

    public static int getRowCount() {
        try {
            return ExcelWSheet.getPhysicalNumberOfRows();
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setResultByTestId(
            String excelPath,
            String sheetName,
            String testId,
            String targetColumn,
            String resultValue
    ) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;

        try {
            fis = new FileInputStream(excelPath);
            workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file.");
            }

            int totalRows = sheet.getPhysicalNumberOfRows();
            int idCol = -1;
            int resultCol = -1;

            XSSFRow header = sheet.getRow(0);
            for (int i = 0; i < header.getLastCellNum(); i++) {
                String headerName = header.getCell(i).getStringCellValue().trim();
                if (headerName.equalsIgnoreCase("ID")) {
                    idCol = i;
                } else if (headerName.equalsIgnoreCase(targetColumn)) {
                    resultCol = i;
                }
            }

            if (idCol == -1 || resultCol == -1) {
                throw new RuntimeException("columns not found : " + targetColumn);
            }

            for (int i = 1; i < totalRows; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;

                XSSFCell idCell = row.getCell(idCol);
                if (idCell == null) continue;

                String currentId = idCell.getStringCellValue().trim();
                if (currentId.equalsIgnoreCase(testId.trim())) {
                    XSSFCell resultCell = row.getCell(resultCol);
                    if (resultCell == null) {
                        resultCell = row.createCell(resultCol);
                    }
                    resultCell.setCellValue(resultValue);
                    break;
                }
            }

            fis.close();
            fos = new FileOutputStream(excelPath);
            workbook.write(fos);
            fos.flush();

        } catch (Exception e) {
        } finally {
            try {
                if (fos != null) fos.close();
                if (workbook != null) workbook.close();
                if (fis != null) fis.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static String getCellDataByColumnValue(String colName, String val, String returnColumn) throws Exception {
        try {
            XSSFRow headerRow = ExcelWSheet.getRow(0);
            int lookupColIndex = -1;
            int returnColIndex = -1;

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String headerName = headerRow.getCell(i).getStringCellValue().trim();
                if (headerName.equalsIgnoreCase(colName)) lookupColIndex = i;
                if (headerName.equalsIgnoreCase(returnColumn)) returnColIndex = i;
            }



            if (lookupColIndex == -1 || returnColIndex == -1) return "";

            for (int r = 1; r <= ExcelWSheet.getLastRowNum(); r++) {
                XSSFRow currentRow = ExcelWSheet.getRow(r);
                if (currentRow == null) continue;

                XSSFCell lookupCell = currentRow.getCell(lookupColIndex);
                if (lookupCell == null) continue;

                String cellValue = lookupCell.toString().trim();
                if (cellValue.equalsIgnoreCase(val.trim())) {
                    XSSFCell returnCell = currentRow.getCell(returnColIndex);
                    if (returnCell == null) return "";

                    switch (returnCell.getCellType()) {
                        case STRING:
                            return returnCell.getStringCellValue().trim();

                        case NUMERIC:
                            double numericValue = returnCell.getNumericCellValue();
                            long longValue = (long) numericValue;
                            return (numericValue == longValue)
                                    ? String.valueOf(longValue)
                                    : String.valueOf(numericValue);

                        case BOOLEAN:
                            return String.valueOf(returnCell.getBooleanCellValue());

                        case FORMULA:
                            try {
                                return returnCell.getStringCellValue();
                            } catch (Exception e) {
                                return String.valueOf(returnCell.getNumericCellValue());
                            }

                        case BLANK:
                        default:
                            return "";
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

}