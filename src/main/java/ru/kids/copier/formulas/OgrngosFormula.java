package ru.kids.copier.formulas;

public class OgrngosFormula extends OgrnulFormula {

	@Override
	protected void initFirstChars() {
		if(firstChars.isEmpty()) {
			firstChars.add("2");
			firstChars.add("4");
			firstChars.add("6");
			firstChars.add("7");
			firstChars.add("8");
			firstChars.add("9");
		}
	}
}
