package ru.kids.copier.process;

public class LoopText {
	private Long copyes = (long) 10;
	private String text = "";

	public LoopText(Long copyes, String text) {
		this.copyes = copyes;
		this.text = text;
	}

	public Long getCopyes() {
		return copyes;
	}

	public String getText() {
		return text;
	}
}
