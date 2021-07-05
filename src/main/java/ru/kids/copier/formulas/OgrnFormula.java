package ru.kids.copier.formulas;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class OgrnFormula extends InnFormula {
	
	protected StringBuilder ogrnStart; 
	protected int maxVal = 10000000;
	protected byte maxZero = 7;
	protected byte delimiter = 11;
			
	@Override
	public String getValue() {
		StringBuilder result = new StringBuilder(ogrnStart);

		result.append(StringUtils.leftPad(rnd.nextInt(maxVal) + "", maxZero, '0'));
		
		result.append((Long.parseLong(result.toString()) % delimiter) % 10);
		
		return result.toString();
	}

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");
		String firstChar;
		String year;
		String okatoCode;

		if (args.length > 3)
			throw new InitGeneratorValueException("Incorrect number of arguments.");
		
		if (args.length > 0 && !args[0].trim().isEmpty()) {
			firstChar = args[0].trim().replace("'", "");
			if(firstChar.length() != 1) 
				throw new InitGeneratorValueException("The first character is set incorrectly ("+firstChar+"). There must be one digit.");
		} else
			firstChar = (rnd.nextInt(9) + 1) + "";

		if (args.length > 1) {
			year = args[1].trim().replace("'", "");
			if(year.length() != 1 && year.length() != 2) 
				throw new InitGeneratorValueException("The year is set incorrectly ("+year+"). There should be one or two digits.");
		} else
			year = rnd.nextInt(100) + "";
		
		if(year.length() < 2)
			year = StringUtils.leftPad(year, 2, '0');
		
		if (args.length > 2) {
			okatoCode = args[2].trim().replace("'", "");
			if(!Arrays.asList(ocatoCodes).contains(okatoCode))
				throw new InitGeneratorValueException("The OKATO code is set incorrectly.");
		} else
			okatoCode = ocatoCodes[rnd.nextInt(ocatoCodes.length)];
		
		ogrnStart = new StringBuilder(firstChar);
		ogrnStart.append(year);
		ogrnStart.append(okatoCode);
	}
}
