import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Distributor {
    String name;
    int id;
    double cost;

    public Distributor(String name, int id, double cost) {
        this.name = name;
        this.id = id;
        this.cost = cost;
    }

    public static List<Distributor> distributorsFromWorkbook(Workbook workbook) {
        List<Distributor> entries = new ArrayList<>();

        for (Sheet distributorsSheet : workbook) {
            Iterator<Row> rowIterator = distributorsSheet.iterator();

            //Skip first row that just has column titles
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                String name;
                double id, cost;
                name = null;
                id = cost = Double.NaN;

                if (cellIterator.hasNext()) {
                    Cell nameCell = cellIterator.next();
                    name = nameCell.getStringCellValue();
                }
                if (cellIterator.hasNext()) {
                    Cell idCell = cellIterator.next();
                    id = idCell.getNumericCellValue();
                }
                if (cellIterator.hasNext()) {
                    Cell costCell = cellIterator.next();
                    cost = costCell.getNumericCellValue();
                }

                if (name == null || Double.isNaN(id) || Double.isNaN(cost)) {
                    continue;
                }

                entries.add(new Distributor(name, (int) id, cost));
            }
        }

        return entries;
    }
}
