package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.GenerateValueException;

public class CountfromtxtFormula extends FromtxtFormula {

	@Override
	public String getValue() throws GenerateValueException {
		return qrefValues.size() + "";
	}
}
