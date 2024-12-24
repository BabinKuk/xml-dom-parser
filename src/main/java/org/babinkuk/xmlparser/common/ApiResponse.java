package org.babinkuk.xmlparser.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.babinkuk.xmlparser.model.Persons;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * information for REST API HTTP response payload
 * 
 * @author BabinKuk
 *
 */
@JsonInclude(value = Include.NON_EMPTY)
public class ApiResponse {
	
	private LocalDateTime timestamp = LocalDateTime.now();
	private HttpStatus status;
	private String message;
	private Map<String, Object> additionalDetails;
	private List<String> errors;
	private List<String> fieldErrors;
	private Persons persons;
	private String xmlResult;

	public ApiResponse() {
	}
	
	public ApiResponse(HttpStatus status, String message) {
		this(status, message, null);
	}
	
	public ApiResponse(HttpStatus status, String message, Map<String, Object> additionalDetails) {
		super();
		this.status = status;
		this.message = message;
		this.additionalDetails = additionalDetails;
	}
	
	public ResponseEntity<ApiResponse> toEntity() {
		return new ResponseEntity<>(this, this.getStatus());
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(Map<String, Object> additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<String> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

	public Persons getPersons() {
		return persons;
	}

	public void setPersons(Persons persons) {
		this.persons = persons;
	}

	public String getXmlResult() {
		return xmlResult;
	}

	public void setXmlResult(String xmlResult) {
		this.xmlResult = xmlResult;
	}
	
}
