import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelReader {

    private static final String FILE_BASE = System.getProperty("user.dir") + "\\resources\\";

    public Workbook read(String path) {

        String filePath = FILE_BASE + path;

        try {

            FileInputStream excelFile = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Iterator<Sheet> sheetIterator = workbook.iterator();

            while (sheetIterator.hasNext()) {

                Sheet datatypeSheet = sheetIterator.next();
                Iterator<Row> iterator = datatypeSheet.iterator();

                while (iterator.hasNext()) {

                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();

                    while (cellIterator.hasNext()) {

                        Cell currentCell = cellIterator.next();
                        //getCellTypeEnum shown as deprecated for version 3.15
                        //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                        if (currentCell.getCellType() == CellType.STRING) {
                            System.out.print(currentCell.getStringCellValue() + "--");
                        } else if (currentCell.getCellType() == CellType.NUMERIC) {
                            System.out.print(currentCell.getNumericCellValue() + "--");
                        }

                    }
                    System.out.println();

                }
                System.out.println();
            }
            return workbook;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
