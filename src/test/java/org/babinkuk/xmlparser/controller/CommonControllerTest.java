package org.babinkuk.xmlparser.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.xmlparser.common.ApiResponse;
import org.babinkuk.xmlparser.model.Address;
import org.babinkuk.xmlparser.model.Person;
import org.babinkuk.xmlparser.model.Persons;
import org.babinkuk.xmlparser.service.XmlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class CommonControllerTest {

	private final Logger log = LogManager.getLogger(getClass());
	
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	
	@Autowired
	private XmlParserService service;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMApper;
	
	@Test
	void validate() throws Exception {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("VALIDATE SUCCESS");
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/validate"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(response.getMessage())))
			;
		/*
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			.andExpect(jsonPath("$[0].title", is(COURSE)))
			.andExpect(jsonPath("$[1].title", is(COURSE_NEW)))
			;
		 */
	}
	
	@Test
	void write() throws Exception {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("WRITING SUCCESS");
		
		String xmlResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><persons><person><name>Gina</name><address><zip>52100</zip><city>Vinkuran</city></address></person></persons>";
		
		Address address = new Address();
		address.setCity("Vinkuran");
		address.setZip("52100");
		
		Person person = new Person();
		person.setName("Gina");
		person.setAddress(address);
		
		List<Person> personList = new ArrayList<Person>();
		personList.add(person);
		
		Persons persons = new Persons();
		persons.setPersonList(personList);
				
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/write")
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(persons)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(response.getMessage())))
			.andExpect(jsonPath("$.xmlResult", is(xmlResult)))
			;
	}
	
	@Test
	void parse() throws Exception {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("PARSING SUCCESS");
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/parse"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(response.getMessage())))
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.persons.personList", hasSize(2)))
			.andExpect(jsonPath("$.persons.personList[0].name", is("Baeldung")))
			.andExpect(jsonPath("$.persons.personList[0].address.city", is("New York")))
			.andExpect(jsonPath("$.persons.personList[0].address.zip", is("00001")))
			.andExpect(jsonPath("$.persons.personList[1].name", is("Gina")))
			.andExpect(jsonPath("$.persons.personList[1].address.city", is("Pula")))
			.andExpect(jsonPath("$.persons.personList[1].address.zip", is("52100")))
			;
	}
}
