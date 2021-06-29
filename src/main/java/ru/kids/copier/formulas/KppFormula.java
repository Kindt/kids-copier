package ru.kids.copier.formulas;

import org.apache.commons.lang3.StringUtils;

public class KppFormula extends InnFormula {

	private int sdvig = 0;

	@Override
	public String getValue() {
		StringBuilder result = new StringBuilder(kodNO);

		result.append(StringUtils.leftPad((rnd.nextInt(49) + 1 + sdvig) + "", 2, '0'));
		result.append(StringUtils.leftPad(rnd.nextInt(1000) + "", 3, '0'));
		return result.toString();
	}

	@Override
	public void init(String formulaArgs) {
		String[] args = formulaArgs.split(",");
		String type = args[0].trim().replace("'", "").toUpperCase();
		if ("INUL".equals(type))
			sdvig = 50;
		
		if (args.length > 1)
			kodNO = args[1].trim().replace("'", "");
		else
			kodNO = ocatoCodes[rnd.nextInt(ocatoCodes.length)];
		
		if (kodNO.length() == 2)
			kodNO += StringUtils.leftPad(rnd.nextInt(100) + "", 2, '0');
	}
}
