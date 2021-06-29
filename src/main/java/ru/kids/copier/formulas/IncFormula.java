package ru.kids.copier.formulas;

public class IncFormula extends FormulasAbstract {

	protected int step = 1;
	protected int val = 0;

	@Override
	public String getValue() {
		val += step;
		return val + "";
	}

	@Override
	public void init(String formulaArgs) {
		String[] args = formulaArgs.split(",");
		val = Integer.parseInt(args[0].trim());
		step = Integer.parseInt(args[1].trim());
	}
}
