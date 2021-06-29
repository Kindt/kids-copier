package ru.kids.copier.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Cliche {
	private Map<String, Map<String, Boolean>> calcFormulas = new HashMap<>();
	private String fileNameMask = "";
	private int amountCopyes = 10;
	private String mainText = "";
	private boolean toSubFolder = false;
	private Map<String, LoopText> loopTexts = new HashMap<>();
	private String encoding = "UTF-8";
	private String xmlVersion = "";

	public Map<String, Map<String, Boolean>> getCalcFormulas() {
		return calcFormulas;
	}

	public void putCalcFormulas(String name, String formula, boolean isLoop) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put(formula, isLoop);
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


	public void prepareXML() {
		if (!loopTexts.isEmpty()) {
			for (Entry<String, LoopText> entry : loopTexts.entrySet()) {
				String keyRegexp = "\\$\\{" + entry.getKey() + "\\}";
				String key = "${" + entry.getKey() + "}";
				if (Pattern.compile(keyRegexp).matcher(mainText).find()) {
					String sourceText = entry.getValue().getText();
					StringBuilder resultString = new StringBuilder();
					for (int i = 0; i < entry.getValue().getCopyes(); i++)
						resultString.append(sourceText);
					mainText = mainText.replace(key, resultString.toString());
				}
			}
			loopTexts.clear();
		}
	}

	private class LoopText {
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
}
