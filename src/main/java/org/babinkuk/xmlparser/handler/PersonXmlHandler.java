package org.babinkuk.xmlparser.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.xmlparser.model.Address;
import org.babinkuk.xmlparser.model.Person;
import org.babinkuk.xmlparser.model.Persons;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Implementation for validating, parsing and writing XML document using DOM
 * 
 */
public class PersonXmlHandler {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private static final String PERSONS = "persons";
	private static final String PERSON = "person";
	private static final String NAME = "name";
	private static final String ADDRESS = "address";
	private static final String ZIP = "zip";
	private static final String CITY = "city";
	
	private static final String XSD_INPUT_PATH = "classpath:person.xsd";
	private static final String XML_INPUT_PATH = "classpath:person.xml";
	
	private Persons persons;
	
	SchemaFactory schemaFactory;
	DocumentBuilderFactory factory;
	
	private File xsd;
	private File input;
	private Source xsdSchema;
	private Schema schema;
	private DocumentBuilder builder;
	private Document document;
	
	public PersonXmlHandler() throws SAXException, FileNotFoundException, ParserConfigurationException {
		
		schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory = DocumentBuilderFactory.newInstance();
		
		xsd = getFile(XSD_INPUT_PATH);
		input = getFile(XML_INPUT_PATH);
		
		xsdSchema = new StreamSource(xsd);
		schema = schemaFactory.newSchema(xsdSchema);
			
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		factory.setValidating(true);
		
		builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}
	
	private File getFile(String location) throws FileNotFoundException {
	    return ResourceUtils.getFile(location);
	}
	
	public void validateXml() throws SAXException, ParserConfigurationException, IOException {
		
		Validator validator = schema.newValidator();
		
		try {
			validator.validate(new StreamSource(input));
		} catch (SAXException | IOException e) {
			log.error(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public Persons parseXml() throws SAXException, ParserConfigurationException, IOException {
		
		XmlErrorHandler errorHandler = new XmlErrorHandler();
		
		builder.setErrorHandler(errorHandler);
		
		document = builder.parse(input);
		
		if (!errorHandler.getErrorList().isEmpty()) {
			errorHandler.getErrorList().forEach(err -> log.error(err.getMessage()));
		}
		
		document.normalizeDocument();
		
		Element personDocument = getDocumentElement(PERSONS, document);
		
		return getPersons(personDocument);
	}
	
	public String writeXml(Persons personInput) throws ParserConfigurationException, TransformerException {
		
		String xmlResult = null;
		
		if (personInput != null) {
			Element personDocument = document.createElement(PERSONS);
			document.appendChild(personDocument);
			
			if (personInput.getPersonList() != null) {
				personInput.getPersonList().forEach(
					person -> {
						//log.info(person);
						Element elementPerson = createElement(document, personDocument, PERSON);
						Element elementName = createElementWithText(document, elementPerson, NAME, person.getName());
						Element elementAddress = createElement(document, elementPerson, ADDRESS);
						Element elementZip = createElementWithText(document, elementAddress, ZIP, person.getAddress().getZip());
						Element elementCity = createElementWithText(document, elementAddress, CITY, person.getAddress().getCity());
					}
				);
			}
			
			Source source = new DOMSource(document);
			
			// serialize document
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// Ne treba transformer.transform(source, new StreamResult(System.out));
			
			StringWriter stringWriter = new StringWriter();
			transformer.transform(source, new StreamResult(stringWriter));
			xmlResult = stringWriter.toString();
		}
				
		return xmlResult;
	}
	
	public Persons getPersons(Element element) throws SAXException, ParserConfigurationException, IOException {
		if (element != null) {
			persons = new Persons();
			persons.setPersonList(new ArrayList<Person>());
		}
		
		NodeList nodeList = element.getChildNodes();
		int n = nodeList.getLength();
		Node current;
		
		for (int i=0; i<n; i++) {
			current = nodeList.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				//log.info(current.getNodeName() + " : " + current.getTextContent());
				if (current.getNodeName().equals(PERSON)) {
					persons.getPersonList().add(getPerson((Element) current));
				}
			}
		}
		
		return persons;
	}
	
	public Person getPerson(Element element) {
		Person person = new Person();
		
		person.setName(getElementText(NAME, element));
		
		Element addressElement = getChildElement(ADDRESS, element);
		if (addressElement != null) {
			Address address = new Address();
			
			address.setZip(getElementText(ZIP, addressElement));
			address.setCity(getElementText(CITY, addressElement));
			
			person.setAddress(address);
		}
		
		return person;
	}

	/**
	 * XML utility method
	 * 
	 * @param tag
	 * @param document
	 * @return
	 */
	public Element getDocumentElement(String tag, Document document) {
		if (tag == null || document == null) {
			return null;
		}
		
		NodeList nodeList = document.getElementsByTagName(tag);
		
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			if (node instanceof Element) {
				return (Element) node;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * XML utility method
	 * 
	 * @param tag
	 * @param element
	 * @return
	 */
	public Element getElement(String tag, Element element) {
		if (tag == null || element == null) {
			return null;
		}
		
		NodeList nodeList = element.getElementsByTagName(tag);
		
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			if (node instanceof Element) {
				return (Element) node;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * XML utility method
	 * 
	 * @param element
	 * @return
	 */
	public String getText(Element element) {
		String text = null;
		
		if (element != null) {
			NodeList nodeList = element.getChildNodes();
			
			if (nodeList.getLength() > 0) {
				Node node = nodeList.item(0);
				if (node instanceof Text) {
					text = ((Text) node).getNodeValue();
				}
			}
		}
		
		return text;
	}
	
	/**
	 * XML utility method
	 * 
	 * @param tag
	 * @param element
	 * @return
	 */
	public Element getChildElement(String tag, Element element) {
		Element result = null;
		if (tag == null || element == null) {
			return null;
		}
		
		if (element != null && tag != null) {
			NodeList nodeList = element.getChildNodes();
			
			if (nodeList.getLength() > 0) {
				int n = nodeList.getLength();
				for (int i=0; i<n; i++) {
					Node current = nodeList.item(i);
					if (current.getNodeType() == Node.ELEMENT_NODE) {
						//log.info(current.getNodeName() + " : " + current.getTextContent());
						if (current.getNodeName().equals(tag)) {
							//log.info("NASAO: " + current.getNodeName() + " : " + current.getTextContent());
							result = (Element) current;
						}
					}
				}
			}
		}
			
		return result;
	}
	
	/**
	 * XML utility method
	 * 
	 * @param element
	 * @return
	 */
	public String getElementText(String tag, Element parentElement) {
		String text = null;
		
		if (parentElement != null && tag != null) {
			Element foundElement = getElement(tag, parentElement);
			
			if (foundElement != null) {
				text = getText(foundElement);
			}
		}
		
		return text;
	}
	
	/**
	 * XML utility method
	 * 
	 * @param document
	 * @param parentElement
	 * @param elementName
	 * @return element
	 */
	public Element createElement(Document document, Node parentElement, String elementName) {
		Element element = document.createElement(elementName);
		parentElement.appendChild(element);
		return element;
	}
	
	/**
	 * XML utility method
	 * 
	 * @param document
	 * @param parentElement
	 * @param elementName
	 * @return element
	 */
	public Element createElementWithText(Document document, Node parentElement, String elementName, String text) {
		if (text == null) {
			return null;
		}
		
		Element element = document.createElement(elementName);
		element.appendChild(document.createTextNode(text != null ? text : ""));
		parentElement.appendChild(element);
		return element;
	}
}