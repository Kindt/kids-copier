package ru.kids.copier.formulas;

public abstract class FormulasAbstract {

	protected String value;
	protected boolean isLoop = false;

	public void init(String formulaArgs, boolean isLoop) {
		setLoop(isLoop);
		init(formulaArgs);
	}

	public abstract void init(String formulaArgs);

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public String getValue() {
		return value;
	}
}
