package ru.kids.copier.formulas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ru.kids.copier.exceptions.GenerateValueException;
import ru.kids.copier.exceptions.InitGeneratorValueException;

public class FromxmlFormula extends FormulasAbstract {

	private static final Random rnd = new Random();

	protected List<String> qrefValues = new ArrayList<>();

	private int count = 0;

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");

		if (args.length < 2 || args.length > 3)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		File file = new File(args[0].trim().replace("'", "").toLowerCase());

		if (!file.exists())
			throw new InitGeneratorValueException("File not found. (" + file.getAbsolutePath() + ")");

		String tagName = args[1].trim().replace("'", "");

		if (tagName.isEmpty())
			throw new InitGeneratorValueException("Incorrect name of tag.");

		String attrName = args.length > 2 ? args[2].trim().replace("'", "") : "";

		initQref(file, tagName, attrName);
	}

	private void initQref(File file, String tagName, String attrName) throws InitGeneratorValueException {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		try {
			try (FileInputStream fis = new FileInputStream(file)) {
				XMLEventReader reader = xmlInputFactory.createXMLEventReader(fis);
				try {
					while (reader.hasNext()) {
						XMLEvent xmlEvent = reader.nextEvent();
						if (xmlEvent.isStartElement()) {
							StartElement startElement = xmlEvent.asStartElement();
							String startElementName = startElement.getName().getLocalPart();
							if (startElementName.equalsIgnoreCase(tagName)) {
								if (!attrName.isEmpty()) {
									Attribute attr = startElement.getAttributeByName(new QName(attrName));
									if (attr == null)
										throw new InitGeneratorValueException("The attribute (" + attrName
												+ ") was not found in the tag (" + tagName + ")");

									qrefValues.add(attr.getValue());
								} else {
									xmlEvent = reader.nextEvent();
									qrefValues.add(xmlEvent.asCharacters().getData());
								}
							}
						}
					}
				} finally {
					reader.close();
				}
			}
		} catch (IOException | XMLStreamException exc) {
			exc.printStackTrace();
			throw new InitGeneratorValueException(exc.getLocalizedMessage());
		}
	}

	@Override
	public String getValue() throws GenerateValueException {
		String val = "";
		if (isUnique) {
			count++;
			if (count >= qrefValues.size())
				throw new GenerateValueException(
						"The maximum number of unique values has been exceeded. Please correct the formula description. ("
								+ formulaAll + ")");

			val = qrefValues.get(count - 1);
		} else
			val = qrefValues.get(rnd.nextInt(qrefValues.size()));

		return val;
	}
}
