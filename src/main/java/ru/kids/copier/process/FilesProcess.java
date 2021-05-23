package ru.kids.copier.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import ru.kids.copier.formulas.FormulasAbstract;
import ru.kids.copier.formulas.StringConstantFormula;
import ru.kids.copier.ui.ProgressDialog;
import ru.kids.copier.xml.ClicheParser;

@SuppressWarnings("deprecation")
public class FilesProcess {

	private File[] fArray;
	private File outFolder;
	private ProgressDialog pd;

	public FilesProcess(File[] fArray, File outFolder, ProgressDialog pd) {
		this.fArray = fArray;
		this.outFolder = outFolder;
		this.pd = pd;
	}
	
	public void process() {

		pd.setFirstBarSize(fArray.length);
		for (File file : fArray) {
			pd.setFirstLabelText("Working with a file " + file.getName());
			pd.setFirstBarIncValue();
			Cliche cliche = ClicheParser.parse(file);
			Map<String, Map<String, Boolean>> calcFormulas = cliche.getCalcFormulas();
			Map<String, FormulasAbstract> formulasValues = initFormulas(calcFormulas);

			pd.setSecondBarSize(cliche.getAmountCopyes());
			pd.setSecondLabelText("The process of creating copies.");
			for (int i = 0; i < cliche.getAmountCopyes(); i++) {
				pd.setSecondBarIncValue();
				String xml = cliche.getMainText();
				String outFileName = cliche.getFileNameMask();
				for (Entry<String, FormulasAbstract> entry : formulasValues.entrySet()) {
					String key = "\\$\\{" + entry.getKey() + "\\}";
					FormulasAbstract formula = entry.getValue();
					if (formula.isLoop()) {
						while (Pattern.compile(key).matcher(xml).find())
							xml = xml.replaceFirst(key, formula.getValue());

						while (Pattern.compile(key).matcher(outFileName).find())
							outFileName = outFileName.replaceFirst(key, formula.getValue());
					} else {
						String val = formula.getValue();
						if (!cliche.getXmlVersion().isEmpty())
							val = StringEscapeUtils.escapeXml(val);
						xml = xml.replaceAll(key, val);
						outFileName = outFileName.replaceAll(key, val);
					}
				}

				File realOutFolder = cliche.getToSubFolder()
						? new File(outFolder, file.getName().substring(0, file.getName().lastIndexOf(".")))
						: outFolder;
				if (!realOutFolder.exists())
					realOutFolder.mkdirs();
				File outFile = new File(realOutFolder, outFileName);
				try (BufferedWriter writer = new BufferedWriter(new PrintWriter(outFile, cliche.getEncoding()))) {
					if (!cliche.getXmlVersion().isEmpty())
						writer.write("<?xml version=\"" + cliche.getXmlVersion() + "\" encoding=\""
								+ cliche.getEncoding() + "\"?>");
					writer.write(xml);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			calcFormulas.clear();
			formulasValues.clear();
		}
	}

	private Map<String, FormulasAbstract> initFormulas(Map<String, Map<String, Boolean>> calcFormulas) {
		Map<String, FormulasAbstract> result = new HashMap<>(calcFormulas.size());

		pd.setSecondBarSize(calcFormulas.size());
		pd.setSecondLabelText("Preparing formulas");
		for (Entry<String, Map<String, Boolean>> entry : calcFormulas.entrySet()) {
			pd.setSecondBarIncValue();
			String key = entry.getKey();
			Map<String, Boolean> map = entry.getValue();
			Entry<String, Boolean> val = map.entrySet().iterator().next();
			String formula = val.getKey();
			boolean isLoop = val.getValue();
			FormulasAbstract fomulaClass = null;
			String arguments = formula;
			if (formula.contains("(")) {
				String formulaName = formula.substring(0, formula.indexOf("(")).toLowerCase();
				String className = "ru.kids.copier.formulas." + StringUtils.capitalize(formulaName) + "Formula";
				try {
					Class<?> classW = getClass().getClassLoader().loadClass(className);
					fomulaClass = (FormulasAbstract) classW.newInstance();
					if (formula.indexOf("(") + 1 == formula.lastIndexOf(")"))
						arguments = "";
					else
						arguments = formula.substring(formula.indexOf("(") + 1, formula.lastIndexOf(")"));
				} catch (ClassNotFoundException e) {
					fomulaClass = new StringConstantFormula();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					fomulaClass = new StringConstantFormula();
				}
			} else
				fomulaClass = new StringConstantFormula();

			fomulaClass.init(arguments, isLoop);
			result.put(key, fomulaClass);
		}
		return result;
	}

}
