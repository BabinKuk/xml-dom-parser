package org.babinkuk.xmlparser.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.babinkuk.xmlparser.common.ApiResponse;
import org.babinkuk.xmlparser.model.Persons;
import org.xml.sax.SAXException;

public interface XmlParserService {
	
	/**
	 * validate xml
	 * @throws ParseException 
	 * @throws RecordUnrecognizedException 
	 * 
	 */
	public ApiResponse validateXml() throws ParserConfigurationException, SAXException, IOException;
	
	/**
	 * parse xml
	 * @throws ParseException 
	 * @throws RecordUnrecognizedException 
	 * 
	 */
	public ApiResponse parseXml() throws ParserConfigurationException, SAXException, IOException;
	
	/**
	 * construct xml
	 * @throws ParseException 
	 * @throws RecordUnrecognizedException 
	 * 
	 */
	public ApiResponse writeXml(Persons person)  throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, FileNotFoundException, SAXException;
}
