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
	
	protected Set<String> values = new HashSet<String>();

	public void init(String formulaArgs, boolean isLoop, boolean isUnique) throws ActivateException, InitGeneratorValueException {
		setLoop(isLoop);
		this.isUnique = isUnique;
		init(formulaArgs);
	}

	protected abstract void init(String formulaArgs) throws ActivateException, InitGeneratorValueException;

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public String getValue() throws GenerateValueException {
		
		String val = getFormulaValue();
		int count = 0;
		
		if(isUnique)
			while (values.contains(val) && count < 100) {
				val = getFormulaValue();
				count++;
			}
		
		if(count >= 100)
			throw new GenerateValueException("Exceeded the maximum number of attempts to get a unique value. Please correct the formula description.");
			
		return val;
	}
	
	protected String getFormulaValue() throws GenerateValueException {

		return value;
	}	
}
