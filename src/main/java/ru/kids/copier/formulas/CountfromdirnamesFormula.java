package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.GenerateValueException;

public class CountfromdirnamesFormula extends FromdirnamesFormula {

	@Override
	public String getValue() throws GenerateValueException {
		return qrefValues.size() + "";
	}
}
