package ru.kids.copier.process;

import java.util.HashMap;
import java.util.Map;

public class Cliche {
	private Map<String, Map<String, FormulaPropertis>> calcFormulas = new HashMap<>();
	private String fileNameMask = "";
	private int amountCopyes = 10;
	private String mainText = "";
	private boolean toSubFolder = false;
	private Map<String, LoopText> loopTexts = new HashMap<>();
	private String encoding = "UTF-8";
	private String xmlVersion = "";

	public Map<String, Map<String, FormulaPropertis>> getCalcFormulas() {
		return calcFormulas;
	}

	public void putCalcFormulas(String name, String formula, boolean isLoop, boolean isUnique) {
		Map<String, FormulaPropertis> map = new HashMap<>();
		map.put(formula, new FormulaPropertis(isLoop, isUnique));
		this.calcFormulas.put(name, map);
	}

	public String getFileNameMask() {
		return fileNameMask;
	}

	public void setFileNameMask(String fileNameMask) {
		this.fileNameMask = fileNameMask;
	}

	public int getAmountCopyes() {
		return amountCopyes;
	}

	public void setAmountCopyes(int amountCopyes) {
		this.amountCopyes = amountCopyes;
	}

	public String getMainText() {
		return mainText;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}

	public boolean getToSubFolder() {
		return toSubFolder;
	}

	public void setToSubFolder(boolean toSubFolder) {
		this.toSubFolder = toSubFolder;
	}

	public void putLoopTexts(String name, String text, int copyes) {
		this.loopTexts.put(name, new LoopText(copyes, text));
	}
	
	public Map<String, LoopText> getLoopTexts() {
		return loopTexts;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getXmlVersion() {
		return xmlVersion;
	}
}
