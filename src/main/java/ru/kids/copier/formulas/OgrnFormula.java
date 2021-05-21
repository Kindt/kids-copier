package ru.kids.copier.formulas;

import org.apache.commons.lang3.StringUtils;

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
	public void init(String formulaArgs) {
		String[] args = formulaArgs.split(",");
		String firstChar;
		String year;
		String okatoCode;

		if (args.length > 0)
			firstChar = args[0].trim().replace("'", "").toUpperCase();
		else
			firstChar = (rnd.nextInt(9) + 1) + "";

		if (args.length > 1)
			year = args[0].trim().replace("'", "").toUpperCase();
		else
			year = rnd.nextInt(100) + "";
		
		if(year.length() < 2)
			year = StringUtils.leftPad(year, 2, '0');
		
		if (args.length > 1)
			okatoCode = args[1].trim().replace("'", "");
		else
			okatoCode = ocatoCodes[rnd.nextInt(ocatoCodes.length)];
		
		ogrnStart = new StringBuilder(firstChar);
		ogrnStart.append(year);
		ogrnStart.append(okatoCode);
	}
}
