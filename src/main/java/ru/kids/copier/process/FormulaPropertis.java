package ru.kids.copier.process;

public class FormulaPropertis {
	private boolean isLoop = false;
	private boolean isUnique = false;

	public FormulaPropertis(boolean isLoop, boolean isUnique) {
		this.isLoop = isLoop;
		this.isUnique = isUnique;
	}

	public boolean isLoop() {
		return isLoop;
	}

	public boolean isUnique() {
		return isUnique;
	}
}
