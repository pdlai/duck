import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Path FILE_BASE = Paths.get(System.getProperty("user.dir"), "resources");
        Workbook distributorsBook = new XSSFWorkbook(FILE_BASE.resolve("Distributors.xlsx").toFile());
        Workbook inventoryBook = new XSSFWorkbook(FILE_BASE.resolve("Inventory.xlsx").toFile());

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

        // Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            List<StockEntry> inventory = StockEntry.entriesFromWorkbook(inventoryBook);
            LowStockResponse stockResponse = new LowStockResponse();

            inventory.forEach(entry -> {
                if (((double)entry.stock / entry.capacity) < 0.25) {
                    stockResponse.items.add(entry);
                }
            });

            return stockResponse;
        }, mapper::writeValueAsString);

        // Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            HashMap<String, Integer> quantities = new HashMap<>();
            HashMap<String, Double> lowestCosts = new HashMap<>();
            try {
                JsonNode jsonNode = mapper.readTree(request.body());
                jsonNode.fields().forEachRemaining(field -> {
                    quantities.put(field.getKey(), field.getValue().intValue());
                    lowestCosts.put(field.getKey(), Double.NaN);
                });
            } catch (Exception e) {
                response.status(403);
                response.body(e.getMessage());
                return response;
            }

            double totalCost = 0;

            List<Distributor> distributors = Distributor.distributorsFromWorkbook(distributorsBook);
            distributors.forEach(distributor -> {
                String name = distributor.name;
                double cost = distributor.cost;

                if (lowestCosts.containsKey(name)) {
                    if (Double.isNaN(lowestCosts.get(name))) {
                        lowestCosts.put(name, cost);
                    } else {
                        if (cost < lowestCosts.get(name)) {
                            lowestCosts.put(name, cost);
                        }
                    }
                }
            });

            for (String name : quantities.keySet()) {
                totalCost += quantities.get(name) * lowestCosts.get(name);
            }

            return new RestockCostResponse(totalCost);
        }, mapper::writeValueAsString);
    }
}
