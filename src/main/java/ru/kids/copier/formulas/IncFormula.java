package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class IncFormula extends FormulasAbstract {

	protected long step = 1;
	protected long val = 0;

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
		val = Long.parseLong(args[0].trim());
		step = Long.parseLong(args[1].trim());
	}
}
