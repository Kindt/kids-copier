package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class OgrnipFormula extends OgrnulFormula {
	
	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		super.init(formulaArgs);
		maxVal = 1000000000;
		maxZero = 9;
		delimiter = 13;
	}	
	
	@Override
	protected void initFirstChars() {
		if(firstChars.isEmpty()) 
			firstChars.add("3");
	}
}
