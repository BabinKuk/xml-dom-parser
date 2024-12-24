package org.babinkuk.xmlparser.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class Persons {
	
	private List<Person> personList;
	
	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}
	
	public List<Person> getPersonList() {
		return this.personList;
	}

	@Override
	public String toString() {
		return "Person [" + personList + "]";
	}
}
