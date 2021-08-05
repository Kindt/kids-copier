package ru.kids.copier.formulas;

import org.apache.commons.lang3.StringUtils;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class IncpadleftFormula extends IncFormula {

	int size = 0;
	char ch = '0';

	@Override
	protected String getFormulaValue() {
		return StringUtils.leftPad(super.getFormulaValue(), size, ch);
	}

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		super.init(formulaArgs);
		String[] args = formulaArgs.split(",");

		if(args.length > 4)
			throw new InitGeneratorValueException("Incorrect number of arguments.");
		size = Integer.parseInt(args[2].trim());
		ch = args[3].trim().replace("'", "").charAt(0);
	}
}
