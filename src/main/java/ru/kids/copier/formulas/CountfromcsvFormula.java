package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.GenerateValueException;

public class CountfromcsvFormula extends FromcsvFormula {

	@Override
	public String getValue() throws GenerateValueException {
		return qrefValues.size() + "";
	}
}
