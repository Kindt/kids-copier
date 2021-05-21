package ru.kids.copier.formulas;

public class OgrnipFormula extends OgrnFormula {
			
	@Override
	public void init(String formulaArgs) {
		super.init(formulaArgs);
		maxVal = 1000000000;
		maxZero = 9;
		delimiter = 13;
	}
}
