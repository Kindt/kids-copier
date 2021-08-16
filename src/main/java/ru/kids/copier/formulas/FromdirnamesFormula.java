package ru.kids.copier.formulas;

import java.io.File;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class FromdirnamesFormula extends FromxmlFormula {

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");

		if (args.length != 2)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		File file = new File(args[0].trim().replace("'", "").toLowerCase());

		if (!file.exists())
			throw new InitGeneratorValueException("File not found. (" + file.getAbsolutePath() + ")");

		boolean isFileOnly = Boolean.parseBoolean(args[1].trim().replace("'", ""));

		initQref(file, isFileOnly);
	}

	private void initQref(File file, boolean isFileOnly) {
		for(File subFile : file.listFiles()) {
			if(isFileOnly) {
				if(subFile.isFile())
					qrefValues.add(subFile.getName());
			} else
				qrefValues.add(subFile.getName());
		}
	}
}
