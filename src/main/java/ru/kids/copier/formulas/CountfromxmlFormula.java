package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.GenerateValueException;

public class CountfromxmlFormula extends FromxmlFormula {

	@Override
	public String getValue() throws GenerateValueException {
		return qrefValues.size() + "";
	}
}
