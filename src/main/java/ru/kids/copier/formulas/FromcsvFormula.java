package ru.kids.copier.formulas;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import ru.kids.copier.exceptions.InitGeneratorValueException;

public class FromcsvFormula extends FromxmlFormula {
	
	private String separateDefaultSimbolCode = "&#044;";

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		
		boolean flag = false;
		if(formulaArgs.contains("\\,")) {
			formulaArgs = formulaArgs.replace("\\,", separateDefaultSimbolCode);
			flag = true;
		}
		
		String[] args = formulaArgs.split(",");

		if (args.length != 5)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		File file = new File(args[0].trim().replace("'", "").toLowerCase());

		if (!file.exists())
			throw new InitGeneratorValueException("File not found. (" + file.getAbsolutePath() + ")");

		int column = 0;
		if (args.length > 1)
			column = Integer.parseInt(args[1].trim().replace("'", ""));

		char separator = ',';
		if (args.length > 2) {
			if(flag && args[2].trim().replace("'", "").equals(separateDefaultSimbolCode))
				separator = ',';
			else
				separator = args[2].trim().replace("'", "").charAt(0);
		}

		char qoute = '"';
		if (args.length > 3) {
			if(flag && args[3].trim().replace("'", "").equals(separateDefaultSimbolCode))
				qoute = ',';
			else
				qoute = args[3].trim().replace("'", "").charAt(0);
		}
		
		int startRow = 0;
		if (args.length > 4)
			startRow = Integer.parseInt(args[4].trim().replace("'", ""));
		
		initQref(file, column, startRow, separator, qoute);
	}

	private void initQref(File file, int column, int startRow, char separator, char qoute) throws InitGeneratorValueException {
		try (CSVReader reader = new CSVReader(new FileReader(file), separator, qoute, startRow)) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) 
					qrefValues.add(nextLine[column]);
		} catch (IOException e) {
			e.printStackTrace();
			throw new InitGeneratorValueException(e.getLocalizedMessage());
		}
	}
}
