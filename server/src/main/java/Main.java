import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;

import java.util.Iterator;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        Workbook distributorsBook = new ExcelReader().read("Distributors.xlsx");
        Workbook inventoryBook = new ExcelReader().read("Inventory.xlsx");

        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
            (request, response) -> {
                response.header("Access-Control-Allow-Headers",
                    "content-type");

                response.header("Access-Control-Allow-Methods",
                    "GET, POST");

            return "OK";
        });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {

            LowStockResponse stockResponse = new LowStockResponse();

            Sheet inventorySheet = inventoryBook.iterator().next();
            Iterator<Row> rowIterator = inventorySheet.iterator();

            //Skip first row that just has column titles
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                if (cellIterator.hasNext()) {
                    Cell nameCell = cellIterator.next();
                    Cell stockCell = cellIterator.next();
                    Cell capacityCell = cellIterator.next();
                    Cell idCell = cellIterator.next();

                    String name = nameCell.getStringCellValue();
                    double stock = stockCell.getNumericCellValue();
                    double capacity = capacityCell.getNumericCellValue();
                    double id = idCell.getNumericCellValue();

                    if (stock / capacity < 0.25){
                        stockResponse.items.add(new LowStockEntry(name, (int) stock,(int) capacity, (int) id));
                    }
                }
            }

            return stockResponse;
        }, new JsonTransformer());

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {

            double totalCost = 0;
            RestockCostResponse costResponse = new RestockCostResponse(totalCost);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(request.body());
            jsonNode.fields().forEachRemaining( field -> {
                System.out.println(field.getKey());
                System.out.println(field.getValue().intValue());
            });

            Iterator<Sheet> distributorsIterator = distributorsBook.iterator();
            while (distributorsIterator.hasNext()){

                Sheet distributorsSheet = distributorsIterator.next();
                Iterator<Row> rowIterator = distributorsSheet.iterator();

                //Skip first row that just has column titles
                rowIterator.next();
                while (rowIterator.hasNext()) {
                    Row currentRow = rowIterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();

                    if (cellIterator.hasNext()) {
                        Cell nameCell = cellIterator.next();
                        Cell idCell = cellIterator.next();
                        Cell costCell = cellIterator.next();

//                    String name = nameCell.getStringCellValue();
//                    double id = idCell.getNumericCellValue();
//                    double cost = costCell.getNumericCellValue();

                        System.out.println(123);
                    }
                }
            }

            return costResponse;
        }, new JsonTransformer());

    }
}
