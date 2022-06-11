import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
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

                String name;
                double stock, capacity, id;
                name = null;
                stock = capacity = id = Double.NaN;

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
                    break;
                }

                if (stock / capacity < 0.25){
                    stockResponse.items.add(new LowStockEntry(name, (int) stock,(int) capacity, (int) id));
                }
            }

            return stockResponse;
        }, new JsonTransformer());

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {

            double totalCost = 0;
            RestockCostResponse costResponse = new RestockCostResponse(totalCost);

            HashMap<String, Integer> quantities = new HashMap<>();
            HashMap<String, Double> lowestCosts = new HashMap<>();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(request.body());
            jsonNode.fields().forEachRemaining( field -> {
                quantities.put(field.getKey(), field.getValue().intValue());
                lowestCosts.put(field.getKey(), Double.NaN);
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
                        break;
                    }

                    if ( lowestCosts.containsKey(name) ) {
                        if ( Double.isNaN(lowestCosts.get(name)) ) {
                            lowestCosts.put(name, cost);
                        } else {
                            if (cost < lowestCosts.get(name)){
                                lowestCosts.put(name,cost);
                            }
                        }
                    }
                }
            }

            for (String name : quantities.keySet()) {
                totalCost += quantities.get(name) * lowestCosts.get(name);
            }

            costResponse.cost = totalCost;
            return costResponse;
        }, new JsonTransformer());

    }
}
