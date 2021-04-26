package com.api.test;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
class ApiTestApplicationTests {
	
	private RestTemplate restTemplate;
	
	private HttpHeaders headers; 
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private JSONObject obj;
	
	@BeforeEach
	public void runBeforeAllTestMethods() {
		restTemplate = new RestTemplate();
		obj = new JSONObject();
	}

	@Order(1)
	@Test
	public void shouldCreateUser() throws JsonMappingException, JsonProcessingException {
	
//		JSONParser jsonParser = new JSONParser();
//		JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("data.json"));
//
//		Iterator<JSONObject> iterator = jsonArray.iterator();
//		JSONObject obj = (JSONObject) iterator.next();

				
		obj.put("id", 1);
		obj.put("username", "Chiquinha");
		obj.put("firstname", "Silva");
		obj.put("lastname", "Pereira");
		obj.put("email", "chiquinha@gmail.com");
		obj.put("password", "chiquinha123");
		obj.put("phone", "999999999");
		obj.put("userStatus", 0);
		
		HttpHeaders headers = headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		ResponseEntity<String> responseEntityStr = 
			      restTemplate.postForEntity("https://petstore.swagger.io/v2/user/",
			    		  request, String.class);
		
		JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertNotNull(responseEntityStr.getBody());
	    assertNotNull(root.path("username").asText());
	}

	@Order(2)
	@Test
	public void shouldCreatePet() throws JsonMappingException, JsonProcessingException {
	
//		JSONParser jsonParser = new JSONParser();
//		JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("data.json"));
//
//		Iterator<JSONObject> iterator = jsonArray.iterator();
//		JSONObject obj = (JSONObject) iterator.next();

				
		obj.put("id", 1);
		obj.put("username", "Chiquinha");
		obj.put("firstname", "Silva");
		obj.put("lastname", "Pereira");
		obj.put("email", "chiquinha@gmail.com");
		obj.put("password", "chiquinha123");
		obj.put("phone", "999999999");
		obj.put("userStatus", 0);
		
		HttpHeaders headers = headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		ResponseEntity<String> responseEntityStr = 
			      restTemplate.postForEntity("https://petstore.swagger.io/v2/user/",
			    		  request, String.class);
		
		JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertNotNull(responseEntityStr.getBody());
	    assertNotNull(root.path("username").asText());
	}

	
}
