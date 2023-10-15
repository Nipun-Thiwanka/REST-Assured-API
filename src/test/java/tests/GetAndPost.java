package tests;

import org.json.simple.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

public class GetAndPost {

    @BeforeTest
    public void setup() {
        RestAssured.baseURI = "https://api.restful-api.dev";
    }

    @Test
    public void testGet(){
        //baseURI = "https://api.restful-api.dev";

        given().
            get("/objects").
        then().
            statusCode(200).
            body("data.Capacity", hasItem("64 GB")).
            body("name", hasItem("Beats Studio3 Wireless"));
    }

    @Test
    public void testPost(){

        JSONObject requestPayload = new JSONObject();
        JSONObject dataPayload = new JSONObject();

        dataPayload.put("year", 2019);
        dataPayload.put("price", Double.toString(1849.99));
        dataPayload.put("CPU model", "Intel Core i9");
        dataPayload.put("Hard disk size", "1 TB");

        requestPayload.put("name", "Apple MacBook Pro 16");
        requestPayload.put("data", dataPayload);

        // Send a POST request to the URL with the JSON payload and store the response
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestPayload.toJSONString()) // Convert the JSON object to a string
            .when()
                .post("/objects");

        // Check if the response status code is 200 (or the expected code)
        response.then().statusCode(200);

        // Print the response body
        String responseBody = response.asString();
        System.out.println("Response Body: " + responseBody);

        // If you want to work with the JSON response data, you can parse it as needed
        // Example: Get a specific field from the response JSON
        String someValue = response.jsonPath().get("someField");
        System.out.println("Value from Response: " + someValue);
    }

    @Test
    
    public void testPut(){
        String requestBody = "{\"name\": \"Apple MacBook Pro 16\", " +
                            "\"data\": {\"year\": 2019, " +
                            "\"price\": 2049.99, " +
                            "\"CPU model\": \"Intel Core i9\", " +
                            "\"Hard disk size\": \"1 TB\", " +
                            "\"color\": \"silver\"}}";

        // Perform the PUT request and print the response
        RestAssured.given()
                .baseUri(baseURI) // Use the defined baseUrl
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/objects/7") // Append the endpoint to the baseUrl
                .then()
                .log().all(); // This will print the response details
    }

    @Test
    public void testDeleteObject() {
        String objectEndpoint = "/objects/6"; // Define the endpoint URL here
        RestAssured.baseURI = "https://api.restful-api.dev";
        RestAssured
            .given()
            .when()
            .delete(objectEndpoint)
            .then()
            .statusCode(405); // Adjust this based on the expected status code
    }

}


