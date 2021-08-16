package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.GenerateValueException;

public class CountfromjsonFormula extends FromjsonFormula {

	@Override
	public String getValue() throws GenerateValueException {
		return qrefValues.size() + "";
	}
}
