package ru.kids.copier.formulas;

import org.apache.commons.lang3.StringUtils;

public class IncpadleftFormula extends IncFormula {

	int size = 0;
	char ch = '0';

	@Override
	public String getValue() {
		return StringUtils.leftPad(super.getValue(), size, ch);
	}

	@Override
	public void init(String formulaArgs) {
		super.init(formulaArgs);
		String[] args = formulaArgs.split(",");
		args = formulaArgs.split(",");
		size = Integer.parseInt(args[2].trim());
		ch = args[3].trim().replace("'", "").charAt(0);
	}
}
