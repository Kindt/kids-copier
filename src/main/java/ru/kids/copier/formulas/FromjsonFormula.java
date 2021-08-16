package ru.kids.copier.formulas;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class FromjsonFormula extends FromxmlFormula {

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");

		if (args.length != 2)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		File file = new File(args[0].trim().replace("'", "").toLowerCase());

		if (!file.exists())
			throw new InitGeneratorValueException("File not found. (" + file.getAbsolutePath() + ")");

		String tagName = args[1].trim().replace("'", "");

		if (tagName.isEmpty())
			throw new InitGeneratorValueException("Incorrect name of tag.");

		initQref(file, tagName);
	}

	private void initQref(File file, String tagName) throws InitGeneratorValueException {
		JsonFactory jsonFactory = new JsonFactory();
		try (JsonParser jsonParser = jsonFactory.createParser(file)) {
			JsonToken jsonToken = jsonParser.nextToken();
			String fieldName = "";
			while (jsonParser.hasCurrentToken()) {
				switch (jsonToken) {
				case FIELD_NAME:
					fieldName = jsonParser.getText();
					break;

				case VALUE_STRING:
				case VALUE_NUMBER_INT:
				case VALUE_NUMBER_FLOAT:
				case VALUE_TRUE:
				case VALUE_FALSE:
					if (fieldName.equals(tagName)) {
						qrefValues.add(jsonParser.getText());
						fieldName = "";
					}
					break;
				default:
					break;
				}

				jsonToken = jsonParser.nextToken();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InitGeneratorValueException(e.getLocalizedMessage());
		}

	}
}
