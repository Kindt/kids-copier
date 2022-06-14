package ru.kids.copier.exceptions;

public class InitGeneratorValueException extends Exception {

	public InitGeneratorValueException(String string) {
		super(string);
	}

	public InitGeneratorValueException(InitGeneratorValueException e) {
		super(e);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
