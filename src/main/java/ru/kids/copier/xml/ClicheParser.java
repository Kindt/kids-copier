package ru.kids.copier.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ru.kids.copier.process.Cliche;

public class ClicheParser {

	private ClicheParser() {

	}

	public static Cliche parse(File file) {
		Cliche cliche = new Cliche();
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
							processElement(startElement, cliche, reader);
						}
					}
				} finally {
					reader.close();
				}
			}
		} catch (IOException | XMLStreamException exc) {
			JOptionPane.showMessageDialog(null, "Ошибка разбора файла клише:" + file.getName());
			exc.printStackTrace();
		}
		return cliche;
	}

	private static void processElement(StartElement startElement, Cliche cliche,
			XMLEventReader reader) throws XMLStreamException {
		String startElementName = startElement.getName().getLocalPart();
		XMLEvent xmlEvent = null;
		switch (startElementName) {
		case ("formula"):
			Attribute nameAttr = startElement.getAttributeByName(new QName("name"));
			Attribute formulaAttr = startElement.getAttributeByName(new QName("formula"));
			Attribute isLoopAttr = startElement.getAttributeByName(new QName("isLoop"));
			Attribute isUniqueAttr = startElement.getAttributeByName(new QName("isUnique"));
			if (nameAttr != null && formulaAttr != null) {
				boolean isLoop = getBooleanValue(isLoopAttr);
				boolean isUnique = getBooleanValue(isUniqueAttr);
				cliche.putCalcFormulas(nameAttr.getValue(), formulaAttr.getValue(), isLoop, isUnique);
			}
			break;
		case ("loopText"):
			nameAttr = startElement.getAttributeByName(new QName("name"));
			Attribute amountCopyesAttr = startElement.getAttributeByName(new QName("amountCopyes"));
			if (nameAttr != null && amountCopyesAttr != null) {
				xmlEvent = reader.nextEvent();
				while (!xmlEvent.isCharacters())
					xmlEvent = reader.nextEvent();
				cliche.putLoopTexts(new StringBuilder("${").append(nameAttr.getValue()).append("}").toString(), xmlEvent.asCharacters().getData(),
						Long.parseLong(amountCopyesAttr.getValue()));
			}
			break;
		case ("amountCopyes"):
			xmlEvent = reader.nextEvent();
			cliche.setAmountCopyes(Integer.parseInt(xmlEvent.asCharacters().getData()));
			break;
		case ("fileNameMask"):
			xmlEvent = reader.nextEvent();
			cliche.setFileNameMask(xmlEvent.asCharacters().getData());
			break;
		case ("toSubFolder"):
			xmlEvent = reader.nextEvent();
			cliche.setToSubFolder(Boolean.parseBoolean(xmlEvent.asCharacters().getData()));
			break;
		case ("encoding"):
			xmlEvent = reader.nextEvent();
			cliche.setEncoding(xmlEvent.asCharacters().getData());
			break;
		case ("xmlVersion"):
			xmlEvent = reader.nextEvent();
			cliche.setXmlVersion(xmlEvent.asCharacters().getData());
			break;
		case ("mainText"):
			xmlEvent = reader.nextEvent();
			while (!xmlEvent.isCharacters())
				xmlEvent = reader.nextEvent();
			cliche.setMainText(xmlEvent.asCharacters().getData());
			break;
		default:
			break;
		}
	}

	private static boolean getBooleanValue(Attribute val) {
		return val == null ? Boolean.FALSE : Boolean.parseBoolean(val.getValue());
	}
}
