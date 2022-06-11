import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExcelReader {

    private static final String FILE_BASE = System.getProperty("user.dir") + "\\resources\\";

    public Workbook read(String path) {

        String filePath = FILE_BASE + path;

        try {

            FileInputStream excelFile = new FileInputStream(filePath);
            return new XSSFWorkbook(excelFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
