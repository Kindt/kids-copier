package ru.kids.copier.formulas;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class KppFormula extends InnFormula {

	private int sdvig = 0;
	private static final String[] corractFirstParameterValue = new String[] { "ul", "inul", "" };

	private boolean isKodNO = false;

	@Override
	protected String getFormulaValue() {
		if (!isKodNO)
			kodNO = ocatoCodes[rnd.nextInt(ocatoCodes.length)];

		if (kodNO.length() == 2)
			kodNO += StringUtils.leftPad(rnd.nextInt(100) + "", 2, '0');

		StringBuilder result = new StringBuilder(kodNO);

		result.append(StringUtils.leftPad((rnd.nextInt(49) + 1 + sdvig) + "", 2, '0'));
		result.append(StringUtils.leftPad(rnd.nextInt(1000) + "", 3, '0'));

		return result.toString();
	}

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");
		String type = args[0].trim().replace("'", "").toLowerCase();

		if (!Arrays.asList(corractFirstParameterValue).contains(type))
			throw new InitGeneratorValueException("Person type is set incorrectly (" + args[0].trim() + ")");

		if (args.length > 2)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		if ("inul".equalsIgnoreCase(type))
			sdvig = 50;

		if (args.length > 1) {
			kodNO = args[1].trim().replace("'", "");
			isKodNO = true;
		}
	}
}
