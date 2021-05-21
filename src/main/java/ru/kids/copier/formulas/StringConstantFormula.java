package ru.kids.copier.formulas;

public class StringConstantFormula extends FormulasAbstract {

	@Override
	public void init(String formulaArgs) {
		super.value = formulaArgs;
	}
}
