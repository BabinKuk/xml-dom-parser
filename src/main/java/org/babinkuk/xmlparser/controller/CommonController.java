package org.babinkuk.xmlparser.controller;
//
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.xmlparser.common.ApiResponse;
import org.babinkuk.xmlparser.common.Echo;
import org.babinkuk.xmlparser.common.ProducesJson;
import org.babinkuk.xmlparser.model.Persons;
import org.babinkuk.xmlparser.service.XmlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

@RestController
@RequestMapping("/api/v1")
public class CommonController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// services
	private XmlParserService xmlParserService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	public CommonController(XmlParserService xmlParserService) {
		this.xmlParserService = xmlParserService;
	}
	
	// expose GET "/echo"
	@GetMapping("/echo")
	@ProducesJson
	public Echo echo() {
		return new Echo();
	}
	
	// expose GET "/config"
	@GetMapping("/config")
	//@ProducesJson
	public ResponseEntity<ApiResponse> getAppConfig() {
		
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("application name", environment.getProperty("spring.application.name", "Unknown"));
		propertyMap.put("version", environment.getProperty("spring.application.version", "Unknown"));
		propertyMap.put("author", environment.getProperty("spring.application.author", "Unknown"));
		
		return new ApiResponse(HttpStatus.OK, null, propertyMap).toEntity();
	}
	
	@PostMapping("/validate")
	public ResponseEntity<ApiResponse> validateXML() throws ParserConfigurationException, SAXException, IOException {
		return ResponseEntity.of(Optional.ofNullable(xmlParserService.validateXml()));
	}
	
	@PostMapping("/parse")
	public ResponseEntity<ApiResponse> parseXml() throws ParserConfigurationException, SAXException, IOException {
		return ResponseEntity.of(Optional.ofNullable(xmlParserService.parseXml()));
	}
	
	@PostMapping("/write")
	public ResponseEntity<ApiResponse> writeXml(@RequestBody Persons person) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException, SAXException {
		return ResponseEntity.of(Optional.ofNullable(xmlParserService.writeXml(person)));
	}
	
}
