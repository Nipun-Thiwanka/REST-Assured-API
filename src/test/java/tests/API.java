package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

public class API {
	public String newResourceId;
    @BeforeTest
    public void setup() {
        RestAssured.baseURI = "https://api.restful-api.dev";
    }

    @Test
    public void GetRequest() {
        Response response = get("/objects");
        response.then().statusCode(200);
        response.then().body("name", hasItems("Google Pixel 6 Pro", "Apple iPhone 12 Mini, 256GB, Blue"));
    }
    
    @Test
    public void GetSpecificItem() {
        Response response = get("/objects");
        response.then().statusCode(200);
        List<Map<String, Object>> responseList = response.jsonPath().getList("");
        Map<String, Object> specificItem = responseList.stream()
            .filter(item -> "13".equals(item.get("id")))
            .findFirst()
            .orElse(null);
        assertNotNull(specificItem);
        assertEquals(specificItem.get("id"), "13");
        assertEquals(specificItem.get("name"), "Apple iPad Air");
    }
    
    @Test
    public void PostRequest() {
        String requestBody = "{" +
                "\"name\": \"Apple MacBook Pro 16\"," +
                "\"data\": {" +
                "    \"year\": 2019," +
                "    \"price\": 1849.99," +
                "    \"CPU model\": \"Intel Core i9\"," +
                "    \"Hard disk size\": \"1 TB\"" +
                "}" +
                "}";

        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .post("/objects");

        response.then().statusCode(200);

        // Extract the "id" from the response and store it in the newResourceId variable
        newResourceId = response.jsonPath().getString("id");
        
     // Print the value of newResourceId
        System.out.println("New Resource ID: " + newResourceId);
    }

    @Test
    public void PutRequest() {
        // Check if the newResourceId is not null or empty
        if (newResourceId != null && !newResourceId.isEmpty()) {

            String requestBody = "{" +
            		 "\"id\": \"" + newResourceId + "\"," +
                    "\"name\": \"Apple MacBook Pro 16\"," +
                    "\"data\": {" +
                    "    \"year\": 2019," +
                    "    \"price\": 2049.99," +
                    "    \"CPU model\": \"Intel Core i9\"," +
                    "    \"Hard disk size\": \"1 TB\"," +
                    "    \"color\": \"silver\"" +
                    "}" +
                    "}";

            String putUrl = "/objects/" + newResourceId;

            Response response = given()
                    .contentType("application/json")
                    .body(requestBody)
                    .put(putUrl);


            response.then().statusCode(200); 

            response.then().body("id", equalTo(newResourceId));
            response.then().body("name", equalTo("Apple MacBook Pro 16"));
            response.then().body("data.year", equalTo(2019));
            response.then().body("data.price", equalTo(2049.99f));
            response.then().body("data['CPU model']", equalTo("Intel Core i9"));
            response.then().body("data['Hard disk size']", equalTo("1 TB"));
            response.then().body("data.color", equalTo("silver"));
            response.then().body("updatedAt", notNullValue());
        } else {
            System.out.println("No new resource ID available for the PUT request.");
        }
    }

    
    @Test
    public void DeleteRequest() {
        if (newResourceId != null && !newResourceId.isEmpty()) {
            given()
                .delete("/objects/" + newResourceId)
                .then()
                .statusCode(200);

        } else {
            System.out.println("No new resource ID available for the DELETE request.");
        }
    }
}

    
    
    

