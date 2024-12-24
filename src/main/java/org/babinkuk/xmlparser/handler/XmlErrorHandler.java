package org.babinkuk.xmlparser.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlErrorHandler implements ErrorHandler, Serializable {

	public class XmlError {
		protected int lineNumber;
		protected String message;
		
		public XmlError(int lineNumber, String message) {
			this.lineNumber = lineNumber;
			this.message = message;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public String getMessage() {
			return message;
		}
	}
	
	protected List<XmlError> errorList;
	
	public XmlErrorHandler() {
		errorList = new ArrayList<XmlError>();
	}
	
	public List<XmlError> getErrorList() {
		return errorList;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		errorList.add(new XmlError(exception.getLineNumber(), exception.getMessage()));
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		errorList.add(new XmlError(exception.getLineNumber(), exception.getMessage()));
	}

}
