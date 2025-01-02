package org.babinkuk.xmlparser.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.xmlparser.common.ApiResponse;
import org.babinkuk.xmlparser.model.Address;
import org.babinkuk.xmlparser.model.Person;
import org.babinkuk.xmlparser.model.Persons;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SpringBootTest
public class PersonXmlHandlerTest {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Test
	void parse() throws ParserConfigurationException, SAXException, IOException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		Persons result = handler.parseXml();
		
		assertNotNull(result,"result not null");
		assertNotNull(result.getPersonList(),"getPersonList not null");
		assertEquals(2, result.getPersonList().size(), "getPersonList().size()");
		
		assertTrue(result.getPersonList().stream().anyMatch(person ->
			StringUtils.equals(person.getName(), "Gina")
			&& StringUtils.equals(person.getAddress().getCity(), "Pula")
			&& StringUtils.equals(person.getAddress().getZip(), "52100")
		));
	}
	
	@Test
	void write() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
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
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		String result = handler.writeXml(persons);
		
		assertNotNull(result,"result not null");
		assertEquals(xmlResult, result, "xmlResult");
	}
	
	@Test
	void getPersons() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ResourceUtils.getFile("classpath:person.xml"));
		doc.getDocumentElement().normalize();
		
		NodeList nodeList = doc.getElementsByTagName("persons");
		
		Node first = nodeList.item(0);

	    assertEquals(1, nodeList.getLength());
	    assertEquals(Node.ELEMENT_NODE, first.getNodeType());
	    assertEquals("persons", first.getNodeName());
		
		Persons persons = handler.getPersons((Element) first);
		
		assertNotNull(persons,"result not null");
		assertNotNull(persons.getPersonList(),"getPersonList not null");
		assertEquals(2, persons.getPersonList().size(), "getPersonList().size()");
		
		assertTrue(persons.getPersonList().stream().anyMatch(person ->
			StringUtils.equals(person.getName(), "Gina")
			&& StringUtils.equals(person.getAddress().getCity(), "Pula")
			&& StringUtils.equals(person.getAddress().getZip(), "52100")
		));
	}
	
	@Test
	void getPerson() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ResourceUtils.getFile("classpath:person.xml"));
		doc.getDocumentElement().normalize();
		
		NodeList nodeList = doc.getElementsByTagName("person");
		
		Node personOneElement = nodeList.item(0);
		Node personTwoElement = nodeList.item(1);

	    assertEquals(2, nodeList.getLength());
	    assertEquals(Node.ELEMENT_NODE, personOneElement.getNodeType());
	    assertEquals("person", personOneElement.getNodeName());
	    assertEquals(Node.ELEMENT_NODE, personTwoElement.getNodeType());
	    assertEquals("person", personTwoElement.getNodeName());
		
		Person person = handler.getPerson((Element) personOneElement);
		
		assertNotNull(person,"result not null");
		assertEquals("Baeldung", person.getName(), "getName");
		assertEquals("New York", person.getAddress().getCity(), "getCity");
		assertEquals("00001", person.getAddress().getZip(), "getZip");
		
		person = handler.getPerson((Element) personTwoElement);
		
		assertNotNull(person,"result not null");
		assertEquals("Gina", person.getName(), "getName");
		assertEquals("Pula", person.getAddress().getCity(), "getCity");
		assertEquals("52100", person.getAddress().getZip(), "getZip");
	}
	
	@Test
	void getDocumentElement() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ResourceUtils.getFile("classpath:person.xml"));
		doc.getDocumentElement().normalize();
		
		Element personDocument = handler.getDocumentElement("persons", doc);

		assertNotNull(personDocument);
		assertEquals(Node.ELEMENT_NODE, personDocument.getNodeType());
		assertEquals("persons", personDocument.getNodeName());
	}
	
	@Test
	void createElement() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document newDoc = builder.newDocument();
		Element root = newDoc.createElement("users");
		newDoc.appendChild(root);
		
		Element elementPerson = handler.createElement(newDoc, root, "person");
		
		DOMSource dom = new DOMSource(newDoc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(dom, new StreamResult(System.out));
		
		assertEquals(1, newDoc.getChildNodes().getLength());
		assertEquals("users", newDoc.getChildNodes().item(0).getNodeName());
		assertEquals("person", root.getChildNodes().item(0).getNodeName());
	}
	
	@Test
	void createElementWithText() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document newDoc = builder.newDocument();
		Element root = newDoc.createElement("users");
		newDoc.appendChild(root);
		
		Element elementPerson = handler.createElement(newDoc, root, "person");
		Element elementName = handler.createElementWithText(newDoc, elementPerson, "name", "Gina");
		
		DOMSource dom = new DOMSource(newDoc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(dom, new StreamResult(System.out));
		
		assertEquals(1, newDoc.getChildNodes().getLength());
		assertEquals("users", newDoc.getChildNodes().item(0).getNodeName());
		assertEquals("person", root.getChildNodes().item(0).getNodeName());
		assertEquals("name", elementPerson.getChildNodes().item(0).getNodeName());
		assertEquals("Gina", elementPerson.getChildNodes().item(0).getTextContent());
	}
	
	@Test
	void getElementText() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document newDoc = builder.newDocument();
		Element root = newDoc.createElement("users");
		newDoc.appendChild(root);
		
		Element elementPerson = handler.createElement(newDoc, root, "person");
		Element elementName = handler.createElementWithText(newDoc, elementPerson, "name", "Gina");
		
		DOMSource dom = new DOMSource(newDoc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(dom, new StreamResult(System.out));
		
		assertEquals(1, newDoc.getChildNodes().getLength());
		assertEquals("users", newDoc.getChildNodes().item(0).getNodeName());
		assertEquals("person", root.getChildNodes().item(0).getNodeName());
		assertEquals("name", elementPerson.getChildNodes().item(0).getNodeName());
		assertEquals("Gina", elementPerson.getChildNodes().item(0).getTextContent());
		
		String elementText = handler.getElementText("name", elementPerson);
		
		assertEquals("Gina", elementText);
	}
	
	@Test
	void getElement() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document newDoc = builder.newDocument();
		Element root = newDoc.createElement("users");
		newDoc.appendChild(root);
		
		Element elementPerson = handler.createElement(newDoc, root, "person");
		Element elementName = handler.createElementWithText(newDoc, elementPerson, "name", "Gina");
		
		DOMSource dom = new DOMSource(newDoc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(dom, new StreamResult(System.out));
		
		assertEquals(1, newDoc.getChildNodes().getLength());
		assertEquals("users", newDoc.getChildNodes().item(0).getNodeName());
		assertEquals("person", root.getChildNodes().item(0).getNodeName());
		assertEquals("name", elementPerson.getChildNodes().item(0).getNodeName());
		assertEquals("Gina", elementPerson.getChildNodes().item(0).getTextContent());
		
		Element element = handler.getElement("name", elementPerson);
		
		assertEquals("name", element.getNodeName());
	}

	@Test
	void getChildElement() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document newDoc = builder.newDocument();
		Element root = newDoc.createElement("users");
		newDoc.appendChild(root);
		
		Element elementPerson = handler.createElement(newDoc, root, "person");
		Element elementName = handler.createElementWithText(newDoc, elementPerson, "name", "Gina");
		
		DOMSource dom = new DOMSource(newDoc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(dom, new StreamResult(System.out));
		
		assertEquals(1, newDoc.getChildNodes().getLength());
		assertEquals("users", newDoc.getChildNodes().item(0).getNodeName());
		assertEquals("person", root.getChildNodes().item(0).getNodeName());
		assertEquals("name", elementPerson.getChildNodes().item(0).getNodeName());
		assertEquals("Gina", elementPerson.getChildNodes().item(0).getTextContent());
		
		Element element = handler.getChildElement("name", elementPerson);
		
		assertEquals("name", element.getNodeName());
	}
}
