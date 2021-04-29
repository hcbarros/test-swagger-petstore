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
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
class TesteAPI {
	
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
	
		ResponseEntity<String> responseEntityStr =  createUser(1, "massuncao", "Maria", 
				"Assunção", "mariassun@gmail.com", "massun123", "999999999");
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
	}

	
	@Order(2)
	@Test
	public void shouldCreatePet() throws JsonMappingException, JsonProcessingException {

		ResponseEntity<String> respPet = createPet(1, 1, "dog", "Brutus", 
				"https://images.dog.ceo/breeds/akita/Akita_Inu_dog.jpg", 1, "tag1", "available");
		
		JsonNode root = objectMapper.readTree(respPet.getBody());
		
		assertEquals(respPet.getStatusCodeValue(), 200);
		assertEquals(root.get("name").asText(), "Brutus");
	}
	
	
	@Order(3)
	@Test
	public void shouldSellPet() throws JsonMappingException, JsonProcessingException, ParseException {
		
		ResponseEntity<String> responseEntityStr = 
				sellPet(1, 1, 1, LocalDateTime.now().toString(), "placed");
		
		JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
	    assertEquals(root.get("status").asText(), "placed");
	    assertEquals(root.get("petId").asInt(), 1);
	    
	    responseEntityStr = createPet(1, 1, "dog", "Brutus", 
				"https://images.dog.ceo/breeds/akita/Akita_Inu_dog.jpg", 1, "massuncao", "sold");
	    
	    JsonNode pet = objectMapper.readTree(responseEntityStr.getBody());
	   
	    assertEquals(pet.path("tags").path(0).path("name").asText(), "massuncao");
	}
	
	
	@Order(4)
	@Test
	public void shouldChangeOrder() throws JsonMappingException, JsonProcessingException {
				
		JsonNode root = getOrder(1);
		
		ResponseEntity<String> responseEntityStr = sellPet(root.get("id").asInt(), 
				root.get("petId").asInt(), 
				root.get("quantity").asInt(), 
				root.get("shipDate").asText(), "delivered");
		
		root = objectMapper.readTree(responseEntityStr.getBody());
		
		assertEquals(responseEntityStr.getStatusCodeValue(), 200);
	    assertEquals(root.get("status").asText(), "delivered");
	}
	
	
	@Order(5)
	@Test
	public void shouldToValidChangedOrder() throws JsonMappingException, JsonProcessingException {
				
		JsonNode root = getOrder(1);
	    assertEquals(root.get("status").asText(), "delivered");
	}
	
	
	@Order(6)
	@Test
	public void shouldCreateEntities() throws JsonMappingException, JsonProcessingException {
	
		ResponseEntity<String> respUser;
		
		for(int i = 1; i <= 10; i++) {
			
			if(i < 6) {
				respUser =  createUser(i, "username"+i, "firstname"+i, 
						"lastname"+i, "email"+i+"@gmail.com", "pass"+1, "phone"+i);
		
				assertEquals(respUser.getStatusCodeValue(), 200);
			}
			
			ResponseEntity<String> respPet = createPet(i, i%2 == 0 ? 1 : 2, 
					i%2 == 0 ? "dog" : "cat", "pet"+i, "image"+i, 1, "tag"+i, "available");
			
			JsonNode petRoot = objectMapper.readTree(respPet.getBody());
		
			assertEquals(respPet.getStatusCodeValue(), 200);
			assertEquals(petRoot.get("name").asText(), "pet"+i);
		}
	}
	
	
	@Order(7)
	@Test
	public void shouldSellAllPets() throws JsonMappingException, JsonProcessingException {
		
		for(int i = 1; i <= 10; i++) {
			
			ResponseEntity<String> responseEntityStr = 
					sellPet(i, i, 1, LocalDateTime.now().toString(), "placed");
			
			JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
			
			assertEquals(responseEntityStr.getStatusCodeValue(), 200);
		    assertEquals(root.get("petId").asInt(), i);
		    
		    responseEntityStr = createPet(i, i%2 == 0 ? 1 : 2, i%2 == 0 ? "dog" : "cat", 
		    		"pet"+i, "image"+i, i < 6 ? i : i - 5, 
		    				"username"+(i < 6 ? i : i - 5), "sold");
		    
		    JsonNode pet = objectMapper.readTree(responseEntityStr.getBody());
		   
		    assertEquals(pet.path("tags").path(0).path("name").asText(), 
		    			"username"+(i < 6 ? i : i - 5));
		}
	}
	
	
	@Order(8)
	@Test
	public void shouldChangeStatusOrderAllPets() throws JsonMappingException, JsonProcessingException {
		
		String status = "";
		
		for(int i = 1; i <= 10; i++) {
			
			JsonNode root = getOrder(i);
			
			ResponseEntity<String> responseEntityStr = sellPet(root.get("id").asInt(), 
					root.get("petId").asInt(), 
					root.get("quantity").asInt(), 
					root.get("shipDate").asText(), i < 6 ? "delivered" : "approved");
			
			root = objectMapper.readTree(responseEntityStr.getBody());
			
			status = i < 6 ? "delivered" : "approved";
			assertEquals(responseEntityStr.getStatusCodeValue(), 200);
		    assertEquals(root.get("status").asText(), status);
		}
	}
	
	
	@Order(9)
	@Test
	public void shouldToValidAllChangedOrder() throws JsonMappingException, JsonProcessingException {
				
		String status = "";
		
		for(int i = 1; i <= 10; i++) {
			
			JsonNode root = getOrder(i);
			status = i < 6 ? "delivered" : "approved";
			assertEquals(root.get("status").asText(), status);
		}
	}
	
	
	private ResponseEntity<String> createUser(
			int id, String username, String firstname, String lastname, 
			String email, String password, String phone) {
		
		obj.put("id", id);
		obj.put("username", username);
		obj.put("firstname", firstname);
		obj.put("lastname", lastname);
		obj.put("email", email);
		obj.put("password", password);
		obj.put("phone", phone);
		obj.put("userStatus", 0);
		
		HttpEntity<String> request =
				new HttpEntity<String>(obj.toString(), headers);
			
		return restTemplate
						.postForEntity("https://petstore.swagger.io/v2/user",
				    		  request, String.class);
	}
	
	
	private ResponseEntity<String> createPet(
			int id, int idcategory, String categoryname, String name,
			String image, int idtag, String tagname, String status) {
		
		obj.put("id", id);		
		
		JSONObject category = new JSONObject();
		category.put("id", idcategory);
		category.put("name", categoryname);
		obj.put("category", category);
		
		obj.put("name", name);
		
		JSONArray arrayImg = new JSONArray();
		arrayImg.add(image);
		obj.put("photoUrls", arrayImg);
		
		JSONObject tag = new JSONObject();
		tag.put("id", idtag);
		tag.put("name", tagname);
		JSONArray arrayTags = new JSONArray();
		arrayTags.add(tag);
		obj.put("tags", arrayTags);
				
		obj.put("status", status);
	
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
				
		return restTemplate.postForEntity("https://petstore.swagger.io/v2/pet",
			    		  request, String.class);
		
	}
	
	
	private ResponseEntity<String> sellPet(
			int id, int petId, int quantity, String shipDate, String status) {
		
		obj.put("id", id);
		obj.put("petId", petId);
		obj.put("quantity", quantity);
		obj.put("shipDate", shipDate);
		obj.put("status", status);
		obj.put("complete", true);
		
		HttpEntity<String> request =
			new HttpEntity<String>(obj.toString(), headers);
		
		return restTemplate.postForEntity("https://petstore.swagger.io/v2/store/order",
			    		  request, String.class);
	}
	
	
	private JsonNode getOrder(int numOrder) throws JsonMappingException, JsonProcessingException {
		
		ResponseEntity<String> order = 
			      restTemplate.getForEntity("https://petstore.swagger.io/v2/store/order/"+numOrder,
			    		   String.class);
		
		return objectMapper.readTree(order.getBody());
	}
}
