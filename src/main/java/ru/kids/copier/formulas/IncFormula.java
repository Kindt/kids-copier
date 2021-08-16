package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class IncFormula extends FormulasAbstract {

	protected int step = 1;
	protected int val = 0;

	@Override
	protected String getFormulaValue() {
		val += step;
		return val + "";
	}

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");
		if (args.length > 2 && this.getClass().getName().equalsIgnoreCase("IncFormula"))
			throw new InitGeneratorValueException("Incorrect number of arguments.");
		val = Integer.parseInt(args[0].trim());
		step = Integer.parseInt(args[1].trim());
	}
}
