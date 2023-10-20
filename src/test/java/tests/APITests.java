package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static io.restassured.RestAssured.given;
import assertions.ApiAssertions;
import base.BaseTest;

public class APITests extends BaseTest {

	@Test
    public void getObjectByIdTest() {
        try {
            // Read expected values, including the endpoint path, from the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode expectedValues = objectMapper.readTree(Files.newBufferedReader(Paths.get("src/test/resources/Json/GET/expectedValues.json")));
            String endpointPath = expectedValues.get("endpointPath").asText();
            String expectedName = expectedValues.get("expectedName").asText();
            String expectedColor = expectedValues.get("expectedColor").asText();
            // Make the API request using the endpoint path
            Response response = makeGetRequest(endpointPath);
            ApiAssertions.assertObject(response, expectedName, expectedColor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
	@Test
	public void postObjectTest() {
	    String requestBody = readJsonFromFile("src\\test\\resources\\Json\\POST\\postObject.json");

	    try {
	        // Read expected values from the JSON file
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode expectedValues = objectMapper.readTree(Files.newBufferedReader(Paths.get("src/test/resources/Json/POST/expectedValues.json")));

	        String endpointPath = expectedValues.get("endpointPath").asText();
	        String expectedName = expectedValues.get("expectedName").asText();
	        String expectedDescription = expectedValues.get("expectedDescription").asText();

	        // Make the API request using the endpoint path
	        Response response = makePostRequest(endpointPath, requestBody);

	        ApiAssertions.assertPostObject(response, expectedName, expectedDescription);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	@Test
	public void putObjectTest() {
	    try {
	        // Read expected values, including endpoint paths, from the JSON file
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode expectedValues = objectMapper.readTree(Files.newBufferedReader(Paths.get("src\\test\\resources\\Json\\PUT\\expectedValues.json")));

	        String postEndpointPath = expectedValues.get("postEndpointPath").asText();
	        String putEndpointPath = expectedValues.get("putEndpointPath").asText();
	        String expectedName = expectedValues.get("expectedName").asText();
	        String expectedDescription = expectedValues.get("expectedDescription").asText();

	        // Make the POST request using the post endpoint path
	        String postRequestBody = readJsonFromFile("src\\test\\resources\\Json\\POST\\postObject.json");
	        Response postResponse = makePostRequest(postEndpointPath, postRequestBody);

	        // Extract the new resource ID
	        String newResourceId = postResponse.jsonPath().getString("id");
	        System.out.println("New Resource ID (POST): " + newResourceId);

	        // Make the PUT request using the put endpoint path
	        String putRequestBody = readJsonFromFile("src\\test\\resources\\Json\\PUT\\putObject.json");
	        Response putResponse = makePutRequest(putEndpointPath + "/" + newResourceId, putRequestBody);

	        // Use the assertion class to assert the response
	        ApiAssertions.assertPutObject(putResponse, expectedName, expectedDescription);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	@Test
	public void deleteObjectTest() {
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode endpointArray = objectMapper.readTree(Files.newBufferedReader(Paths.get("src\\test\\resources\\Json\\DELETE\\expectedValues.json")));

	        for (JsonNode endpointPair : endpointArray) {
	            String postEndpoint = endpointPair.get("postEndpoint").asText();
	            String deleteEndpoint = endpointPair.get("deleteEndpoint").asText();

	            // Perform POST request to create an object
	            String requestBody = readJsonFromFile("src\\test\\resources\\Json\\POST\\postObject.json");
	            Response postResponse = makePostRequest(postEndpoint, requestBody);
	            newResourceId = postResponse.jsonPath().getString("id");

	            // Perform DELETE request for the created object
	            Response deleteResponse = makeDeleteRequest(deleteEndpoint + "/" + newResourceId);

	            // Use the assertion class to assert the deleteResponse
	            ApiAssertions.assertDeleteObject(deleteResponse);

	            // Check if the resource no longer exists
	            ApiAssertions.assertResourceNotExists(deleteEndpoint + "/" + newResourceId);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

    private Response makeGetRequest(String endpoint) {
        return given().when().get(endpoint);
    }
    

    private Response makePostRequest(String endpoint, String requestBody) {
        return given().contentType("application/json").body(requestBody).post(endpoint);
    }

    private Response makePutRequest(String endpoint, String requestBody) {
        return given().contentType("application/json").body(requestBody).put(endpoint);
    }

    private Response makeDeleteRequest(String endpoint) {
        return given().delete(endpoint);
    }

    private String readJsonFromFile(String filePath) {
        // Read JSON data from a file and return it as a string
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
