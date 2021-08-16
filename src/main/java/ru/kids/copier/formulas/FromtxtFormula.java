package ru.kids.copier.formulas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class FromtxtFormula extends FromxmlFormula {

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");

		if (args.length != 1)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		File file = new File(args[0].trim().replace("'", "").toLowerCase());

		if (!file.exists())
			throw new InitGeneratorValueException("File not found. (" + file.getAbsolutePath() + ")");

		initQref(file);
	}

	private void initQref(File file) throws InitGeneratorValueException {
		try (FileReader fr = new FileReader(file)) {
			try (BufferedReader reader = new BufferedReader(fr)) {
				String line;
				while ((line = reader.readLine()) != null)
					qrefValues.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InitGeneratorValueException(e.getLocalizedMessage());
		}
	}
}
