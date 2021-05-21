package ru.kids.copier.formulas;

import java.util.Random;

public class RandomsetFormula extends FormulasAbstract {

	private String[] array;
	private Random rnd = new Random();
	
	@Override
	public void init(String formulaArgs) {
		array = formulaArgs.split(",");
	}
	
	@Override
	public String getValue() {
		int id = rnd.nextInt(array.length);
		return array[id].replace("'", "").trim();
	}
}
