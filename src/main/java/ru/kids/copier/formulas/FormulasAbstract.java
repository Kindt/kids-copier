package ru.kids.copier.formulas;

import java.util.HashSet;
import java.util.Set;

import ru.kids.copier.exceptions.ActivateException;
import ru.kids.copier.exceptions.GenerateValueException;
import ru.kids.copier.exceptions.InitGeneratorValueException;

public abstract class FormulasAbstract {

	protected String value;
	protected boolean isLoop = false;
	protected boolean isUnique = false;
	protected int maxFindUniqueValue = 10000;
	protected String formulaAll;

	protected Set<String> values = new HashSet<>();

	public void init(String formulaAll, String formulaArgs, boolean isLoop, boolean isUnique)
			throws ActivateException, InitGeneratorValueException {
		this.isLoop = isLoop;
		this.formulaAll = formulaAll;
		this.isUnique = isUnique;
		init(formulaArgs);
	}

	protected abstract void init(String formulaArgs) throws ActivateException, InitGeneratorValueException;

	public boolean isLoop() {
		return isLoop;
	}

	public String getValue() throws GenerateValueException {

		String val = getFormulaValue();
		int count = 0;

		if (isUnique)
			while (values.contains(val) && count < maxFindUniqueValue) {
				val = getFormulaValue();
				count++;
			}

		values.add(val);

		if (count >= maxFindUniqueValue)
			throw new GenerateValueException("Exceeded the maximum number of attempts to get a unique value ("
					+ maxFindUniqueValue + "). Please correct the formula description. (" + formulaAll + ")");

		return val;
	}

	protected String getFormulaValue() throws GenerateValueException {

		return value;
	}
}
