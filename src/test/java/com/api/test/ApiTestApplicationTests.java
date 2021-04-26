package com.api.test;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

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
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private HttpHeaders headers;
	
	private JSONObject obj;
	
	
	@BeforeEach
	public void runBeforeAllTestMethods() {
		restTemplate = new RestTemplate();
		obj = new JSONObject();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Order(1)
	@Test
	public void shouldCreateUser() throws JsonMappingException, JsonProcessingException {
	
		obj.put("id", 1);
		obj.put("username", "massuncao");
		obj.put("firstname", "Maria");
		obj.put("lastname", "Assunção");
		obj.put("email", "mariassun@gmail.com");
		obj.put("password", "massun123");
		obj.put("phone", "999999999");
		obj.put("userStatus", 0);
		
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		ResponseEntity<String> responseEntityStr = 
			      restTemplate.postForEntity("https://petstore.swagger.io/v2/user/",
			    		  request, String.class);
		
		JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
	    assertNotNull(root.path("username").asText());
	}

	@Order(2)
	@Test
	public void shouldCreatePet() throws JsonMappingException, JsonProcessingException {

		JSONObject category = new JSONObject();
		category.put("id", 1);
		category.put("name", "dog");
		JSONObject tag = new JSONObject();
		tag.put("id", 1);
		tag.put("name", "tag1");
		
		JSONArray arrayImg = new JSONArray();
		arrayImg.add("https://images.dog.ceo/breeds/akita/Akita_Inu_dog.jpg");
		JSONArray arrayTags = new JSONArray();
		arrayTags.add(tag);
		
		obj.put("id", 1);
		obj.put("category", category);		
		obj.put("name", "Brutus");
		obj.put("photoUrls", arrayImg);
		obj.put("tags", arrayTags);
		obj.put("status", "available");
	
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		ResponseEntity<String> responseEntityStr = 
			      restTemplate.postForEntity("https://petstore.swagger.io/v2/pet",
			    		  request, String.class);
		
		JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
		assertEquals(root.get("name").asText(), "Brutus");
	}
	
	
	@Order(3)
	@Test
	public void shouldSellPet() throws JsonMappingException, JsonProcessingException {
				
		obj.put("id", 1);
		obj.put("petId", 1);
		obj.put("quantity", 1);
		obj.put("shipDate", LocalDateTime.now().toString());
		obj.put("status", "placed");
		obj.put("complete", true);
		
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		ResponseEntity<String> responseEntityStr = 
			      restTemplate.postForEntity("https://petstore.swagger.io/v2/store/order",
			    		  request, String.class);
		
		JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
	    assertEquals(root.get("status").asText(), "placed");
	    assertEquals(root.get("petId").asInt(), 1);
	}
	
	@Order(4)
	@Test
	public void shouldChangeOrder() throws JsonMappingException, JsonProcessingException {
				
		ResponseEntity<String> order1 = 
			      restTemplate.getForEntity("https://petstore.swagger.io/v2/store/order/1",
			    		   String.class);
		
		JsonNode root = objectMapper.readTree(order1.getBody());
		
		obj.put("id", root.get("id").asInt());
		obj.put("petId", root.get("petId").asInt());
		obj.put("quantity", root.get("quantity").asInt());
		obj.put("shipDate", root.get("shipDate").asText());
		obj.put("status", "delivered");
		obj.put("complete", true);
		
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		ResponseEntity<String> responseEntityStr = 
			      restTemplate.postForEntity("https://petstore.swagger.io/v2/store/order",
			    		  request, String.class);
		
		root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
	    assertEquals(root.get("status").asText(), "delivered");
	}
	
	@Order(5)
	@Test
	public void shouldToValidChangedOrder() throws JsonMappingException, JsonProcessingException {
				
		ResponseEntity<String> order1 = 
			      restTemplate.getForEntity("https://petstore.swagger.io/v2/store/order/1",
			    		   String.class);
		
		JsonNode root = objectMapper.readTree(order1.getBody());
		
		assertEquals(order1.getStatusCodeValue(), 200);
	    assertEquals(root.get("status").asText(), "delivered");
	}
	
	

	
}
