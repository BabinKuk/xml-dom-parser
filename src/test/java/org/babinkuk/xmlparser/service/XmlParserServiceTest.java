package org.babinkuk.xmlparser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.xmlparser.common.ApiResponse;
import org.babinkuk.xmlparser.model.Address;
import org.babinkuk.xmlparser.model.Person;
import org.babinkuk.xmlparser.model.Persons;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.xml.sax.SAXException;

@SpringBootTest
public class XmlParserServiceTest {

	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private XmlParserService service;
	
	@Test
	void validate() throws ParserConfigurationException, SAXException, IOException {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("VALIDATE SUCCESS");
		
		ApiResponse result = service.validateXml();
		
		assertNotNull(result,"result not null");
		assertEquals(response.getStatus(), result.getStatus(), "result status");
		assertEquals(response.getMessage(), result.getMessage(), "result message");
	}
	
	@Test
	void write() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
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
		
		ApiResponse result = service.writeXml(persons);
		
		assertNotNull(result,"result not null");
		assertEquals(response.getStatus(), result.getStatus(), "result status");
		assertEquals(response.getMessage(), result.getMessage(), "result message");
		assertNotNull(result.getXmlResult());
		assertEquals(xmlResult, result.getXmlResult(), "xmlResult");
	}
	
	@Test
	void parse() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("PARSING SUCCESS");
		
		ApiResponse result = service.parseXml();
		
		assertNotNull(result,"result not null");
		assertEquals(response.getStatus(), result.getStatus(), "result status");
		assertEquals(response.getMessage(), result.getMessage(), "result message");
		assertNotNull(result.getPersons(),"getPersons not null");
		assertEquals(2, result.getPersons().getPersonList().size(), "getPersonList().size()");
		
		assertTrue(result.getPersons().getPersonList().stream().anyMatch(person ->
			StringUtils.equals(person.getName(), "Gina")
			&& StringUtils.equals(person.getAddress().getCity(), "Pula")
			&& StringUtils.equals(person.getAddress().getZip(), "52100")
		));
	}
}
