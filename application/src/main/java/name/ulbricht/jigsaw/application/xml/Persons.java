package name.ulbricht.jigsaw.application.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root mapped to XML using JAXB annotations.
 */
@XmlRootElement(name = "persons")
public final class Persons {

	private List<Person> persons = new ArrayList<>();

	@XmlElement(name = "person")
	public List<Person> getPersons() {
		return this.persons;
	}

	public void setPersons(final List<Person> persons) {
		this.persons = persons;
	}
}
