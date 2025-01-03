package org.babinkuk.xmlparser.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.xmlparser.common.ApiResponse;
import org.babinkuk.xmlparser.handler.PersonXmlHandler;
import org.babinkuk.xmlparser.model.Persons;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service
public class XmlParserServiceImpl implements XmlParserService {

	private final Logger log = LogManager.getLogger(getClass());
	
	public XmlParserServiceImpl() {
	}
	
	@Override
	public ApiResponse validateXml() throws ParserConfigurationException, IOException, SAXException {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("VALIDATE SUCCESS");
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		try {
			handler.validateXml();
		} catch (SAXException | IOException e) {
			response.setMessage("VALIDATE FAILED");
			log.error(e.getMessage());
			//e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	public ApiResponse parseXml() throws ParserConfigurationException, SAXException, IOException {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("PARSING SUCCESS");
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		Persons persons = handler.parseXml();
		
		response.setPersons(persons);
		
        return response;
	}

	@Override
	public ApiResponse writeXml(Persons person) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, FileNotFoundException, SAXException {
		
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.OK);
		response.setMessage("WRITING SUCCESS");
		
		PersonXmlHandler handler = new PersonXmlHandler();
		
		String xmlResult = handler.writeXml(person);
				
		response.setXmlResult(xmlResult);
		
		return response;
	}
}