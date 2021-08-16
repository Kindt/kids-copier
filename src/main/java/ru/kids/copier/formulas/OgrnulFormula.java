package ru.kids.copier.formulas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class OgrnulFormula extends InnFormula {

	protected StringBuilder ogrnStart;
	protected int maxVal = 10000000;
	protected byte maxZero = 7;
	protected byte delimiter = 11;

	private boolean isFirstChar = false;
	private boolean isYear = false;
	private boolean isRegNum = false;

	private String firstChar = "";
	private String year = "";
	private String okatoCode = "";

	protected List<String> firstChars = new ArrayList<>();

	@Override
	protected String getFormulaValue() {

		if (!isFirstChar)
			firstChar = firstChars.get(rnd.nextInt(firstChars.size()));

		if (!isYear)
			year = rnd.nextInt(100) + "";

		if (year.length() < 2)
			year = StringUtils.leftPad(year, 2, '0');

		if (!isRegNum)
			okatoCode = ocatoCodes[rnd.nextInt(ocatoCodes.length)];

		StringBuilder result = new StringBuilder(firstChar);
		result.append(year);
		result.append(okatoCode);

		result.append(StringUtils.leftPad(rnd.nextInt(maxVal) + "", maxZero, '0'));

		result.append((Long.parseLong(result.toString()) % delimiter) % 10);

		return result.toString();
	}

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		initFirstChars();
		String[] args = formulaArgs.split(",");

		if (args.length > 3)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		if (args.length > 0 && !args[0].trim().isEmpty()) {
			firstChar = args[0].trim().replace("'", "");

			if (firstChar.length() != 1)
				throw new InitGeneratorValueException(
						"The first character is set incorrectly (" + firstChar + "). There must be one digit.");

			if (!firstChars.contains(firstChar))
				throw new InitGeneratorValueException("The specified first character for this type of OGRN ("
						+ firstChar + "). Acceptable values: " + firstChars + ".");
			isFirstChar = true;
		}

		if (args.length > 1) {
			year = args[1].trim().replace("'", "");
			if (year.length() != 1 && year.length() != 2)
				throw new InitGeneratorValueException(
						"The year is set incorrectly (" + year + "). There should be one or two digits.");
			isYear = true;
		}

		if (args.length > 2) {
			okatoCode = args[2].trim().replace("'", "");
			if (!Arrays.asList(ocatoCodes).contains(okatoCode))
				throw new InitGeneratorValueException("The OKATO code is set incorrectly.");
			isRegNum = true;
		}
	}

	protected void initFirstChars() {
		if (firstChars.isEmpty()) {
			firstChars.add("1");
			firstChars.add("5");
		}
	}
}
