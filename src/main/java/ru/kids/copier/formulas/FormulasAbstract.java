package ru.kids.copier.formulas;

import ru.kids.copier.exceptions.ActivateException;
import ru.kids.copier.exceptions.GenerateValueException;

public abstract class FormulasAbstract {

	protected String value;
	protected boolean isLoop = false;

	public void init(String formulaArgs, boolean isLoop) throws ActivateException {
		setLoop(isLoop);
		init(formulaArgs);
	}

	public abstract void init(String formulaArgs) throws ActivateException;

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public String getValue() throws GenerateValueException {
		return value;
	}
}
