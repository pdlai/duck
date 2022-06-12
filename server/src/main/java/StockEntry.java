import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StockEntry {
    public String name;
    public int stock;
    public int capacity;
    public int id;

    public StockEntry(String name, int stock, int capacity, int id) {
        this.name = name;
        this.stock = stock;
        this.capacity = capacity;
        this.id = id;
    }

    public static List<StockEntry> entriesFromWorkbook(Workbook inventoryBook) {
        // normally we'd have an abstraction to read from a datasource instead
        Sheet inventorySheet = inventoryBook.iterator().next();
        Iterator<Row> rowIterator = inventorySheet.iterator();

        //Skip first row that just has column titles
        rowIterator.next();

        List<StockEntry> entries = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();

            String name = null;
            double stock, capacity, id;
            stock = capacity = id = Double.NaN;

            // TODO: cleanup
            Iterator<Cell> cellIterator = currentRow.iterator();
            if (cellIterator.hasNext()) {
                Cell nameCell = cellIterator.next();
                name = nameCell.getStringCellValue();
            }
            if (cellIterator.hasNext()) {
                Cell stockCell = cellIterator.next();
                stock = stockCell.getNumericCellValue();
            }
            if (cellIterator.hasNext()) {
                Cell capacityCell = cellIterator.next();
                capacity = capacityCell.getNumericCellValue();
            }
            if (cellIterator.hasNext()) {
                Cell idCell = cellIterator.next();
                id = idCell.getNumericCellValue();
            }

            if (name == null || Double.isNaN(stock) || Double.isNaN(capacity) || Double.isNaN(id)) {
                continue;
            }

            entries.add(new StockEntry(name, (int) stock,(int) capacity, (int) id));
        }

        return entries;
    }
}
