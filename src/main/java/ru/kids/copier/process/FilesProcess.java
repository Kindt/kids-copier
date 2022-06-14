package ru.kids.copier.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import ru.kids.copier.exceptions.ActivateException;
import ru.kids.copier.exceptions.CanselException;
import ru.kids.copier.exceptions.GenerateValueException;
import ru.kids.copier.exceptions.InitGeneratorValueException;
import ru.kids.copier.formulas.FormulasAbstract;
import ru.kids.copier.formulas.StringConstantFormula;
import ru.kids.copier.ui.ProgressDialog;
import ru.kids.copier.xml.ClicheParser;

public class FilesProcess {

	private static final Logger logger = LogManager.getRootLogger();
	private File[] fArray;
	private File outFolder;
	private ProgressDialog pd;

	private Map<String, String> notLoopValues = new HashMap<>();

	public FilesProcess(File[] fArray, File outFolder, ProgressDialog pd) {
		this.fArray = fArray;
		this.outFolder = outFolder;
		this.pd = pd;
	}

	public void process() throws GenerateValueException, ActivateException, CanselException, IOException, SAXException,
			InitGeneratorValueException {

		pd.setFirstBarSize(fArray.length);
		for (File file : fArray) {

			logger.info("Getting started with the cliche file: {}", file.getName());
			if (pd.isCanseled())
				throw new CanselException();

			pd.setFirstLabelText("Working with a file: " + file.getName());

			checkedSchema(file);

			logger.info("Initializing the cliche file");
			Cliche cliche = ClicheParser.parse(file);

			logger.info("Initializing cliche formulas");
			Map<String, FormulasAbstract> formulasValues = initFormulas(cliche.getCalcFormulas());

			pd.setSecondBarSize(cliche.getAmountCopyes());
			pd.setSecondLabelText("The process of creating copies.");

			logger.info("Creating copies of files");
			processFillCopyes(cliche, formulasValues, file);

			formulasValues.clear();
			pd.setFirstBarIncValue();

			logger.info("End of working with the cliche file: {}", file.getName());
		}
	}

	private void checkedSchema(File file) throws IOException, SAXException {
		logger.info("Checking according to the scheme of the cliche file");
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("schemas/schema.xsd");) {

			StreamSource ss = new StreamSource(is);

			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(ss);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(file));
		}
	}

	private void processFillCopyes(Cliche cliche, Map<String, FormulasAbstract> formulasValues, File file)
			throws GenerateValueException, CanselException {
		char[] xml = cliche.getMainText().toCharArray();
		char[] outFileName = cliche.getFileNameMask().toCharArray();

		File realOutFolder = cliche.getToSubFolder()
				? new File(outFolder, file.getName().substring(0, file.getName().lastIndexOf(".")))
				: outFolder;
		if (!realOutFolder.exists())
			realOutFolder.mkdirs();

		try {
			for (int i = 0; i < cliche.getAmountCopyes(); i++) {
				String outFileNameNewStr = processInsertsValues(outFileName, formulasValues, cliche.getLoopTexts(),
						false, null);

				File outFile = new File(realOutFolder, outFileNameNewStr);
				try (PrintWriter pw = new PrintWriter(outFile, cliche.getEncoding());
						BufferedWriter writer = new BufferedWriter(pw)) {
					if (!cliche.getXmlVersion().isEmpty())
						writer.write("<?xml version=\"" + cliche.getXmlVersion() + "\" encoding=\""
								+ cliche.getEncoding() + "\"?>");
					processInsertsValues(xml, formulasValues, cliche.getLoopTexts(), !cliche.getXmlVersion().isEmpty(),
							writer);
				}

				logger.info("Creating file {}", outFileNameNewStr);
				pd.setSecondBarIncValue();
				notLoopValues.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String processInsertsValues(char[] chars, Map<String, FormulasAbstract> formulasValues,
			Map<String, LoopText> loopTexts, boolean isXML, BufferedWriter writer)
			throws CanselException, GenerateValueException, IOException {
		StringBuilder result = new StringBuilder();
		StringBuilder keyName = new StringBuilder();

		String value = "";

		for (char ch : chars) {
			if (pd.isCanseled())
				throw new CanselException();
			if (ch == '}' && !keyName.toString().isEmpty()) {
				keyName.append(ch);
				String key = keyName.toString();
				if (formulasValues.containsKey(key)) {
					FormulasAbstract formula = formulasValues.get(key);
					if (!formula.isLoop() && notLoopValues.containsKey(key))
						value = notLoopValues.get(key);
					else {
						value = formula.getValue();
						if (!formula.isLoop())
							notLoopValues.put(key, value);
					}

					if (isXML)
						value = StringEscapeUtils.escapeXml10(value);

					result.append(value);
				} else {
					if (loopTexts.containsKey(key)) {
						LoopText loopText = loopTexts.get(key);
						for (long i = 0; i < loopText.getCopyes(); i++)
							processInsertsValues(loopText.getText().toCharArray(), formulasValues, loopTexts, isXML,
									writer);
						result.setLength(0);
					} else {
						if (isXML)
							value = StringEscapeUtils.escapeXml10(value);

						result.append(value);
					}
				}
				keyName.setLength(0);
			} else {
				if (ch == '$' || !keyName.toString().isEmpty()) {
					keyName.append(ch);
				} else if (ch == '\n') {
					if (writer != null) {
						writer.newLine();
						writer.write(result.toString());
						result.setLength(0);
					}
				} else
					result.append(ch);
			}
		}
		if (writer != null && result.length() > 0) {
			writer.write(result.toString());
			result.setLength(0);
		}
		return result.toString();
	}

	@SuppressWarnings("deprecation")
	private Map<String, FormulasAbstract> initFormulas(Map<String, Map<String, FormulaPropertis>> calcFormulas)
			throws ActivateException, InitGeneratorValueException {
		Map<String, FormulasAbstract> result = new HashMap<>(calcFormulas.size());

		pd.setSecondBarSize(calcFormulas.size());
		pd.setSecondLabelText("Preparing formulas");
		for (Entry<String, Map<String, FormulaPropertis>> entry : calcFormulas.entrySet()) {
			String key = entry.getKey();
			Map<String, FormulaPropertis> map = entry.getValue();
			Entry<String, FormulaPropertis> val = map.entrySet().iterator().next();
			String formula = val.getKey();
			boolean isLoop = val.getValue().isLoop();
			boolean isUnique = val.getValue().isUnique();
			FormulasAbstract fomulaClass = null;
			String arguments = formula;
			if (formula.contains("(")) {
				String formulaName = formula.substring(0, formula.indexOf("(")).toLowerCase();
				String className = new StringBuilder("ru.kids.copier.formulas.").append(StringUtils.capitalize(formulaName)).append("Formula").toString();
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

			fomulaClass.init(formula, arguments, isLoop, isUnique);
			result.put(new StringBuilder("${").append(key).append("}").toString(), fomulaClass);
			pd.setSecondBarIncValue();
		}
		return result;
	}

}
