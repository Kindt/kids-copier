package ru.kids.copier.process;

public class LoopText {
	private int copyes = 10;
	private String text = "";

	public LoopText(int copyes, String text) {
		this.copyes = copyes;
		this.text = text;
	}

	public int getCopyes() {
		return copyes;
	}

	public String getText() {
		return text;
	}
}
