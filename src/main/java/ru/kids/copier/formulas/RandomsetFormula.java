package ru.kids.copier.formulas;

import java.util.Random;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class RandomsetFormula extends FormulasAbstract {

	private String[] array;
	private Random rnd = new Random();

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		array = formulaArgs.split(",");
		if (array.length < 2)
			throw new InitGeneratorValueException("Incorrect number of arguments.");

		maxFindUniqueValue = array.length;
	}

	@Override
	protected String getFormulaValue() {
		int id = rnd.nextInt(array.length);
		return array[id].replace("'", "").trim();
	}
}
