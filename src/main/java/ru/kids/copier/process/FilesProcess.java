package ru.kids.copier.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import ru.kids.copier.exceptions.ActivateException;
import ru.kids.copier.exceptions.CanselException;
import ru.kids.copier.exceptions.GenerateValueException;
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

	public void process() throws GenerateValueException, ActivateException, CanselException, IOException, SAXException {

		pd.setFirstBarSize(fArray.length);
		for (File file : fArray) {
			if(pd.isCanseled())
				throw new CanselException();
			
			pd.setFirstLabelText("Working with a file " + file.getName());
			
			if (checkedSchema(file)) {

				Cliche cliche = ClicheParser.parse(file);
				Map<String, Map<String, Boolean>> calcFormulas = cliche.getCalcFormulas();
				Map<String, FormulasAbstract> formulasValues = initFormulas(calcFormulas);

				pd.setSecondBarSize(cliche.getAmountCopyes());
				pd.setSecondLabelText("The process of creating copies.");

				processFillCopyes(cliche, formulasValues, file);

				calcFormulas.clear();
				formulasValues.clear();
				pd.setFirstBarIncValue();
			}
		}
	}

	private boolean checkedSchema(File file) throws IOException, SAXException {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("schemas/schema.xsd");){
			
			StreamSource ss = new StreamSource(is);
 
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(ss);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(file));
            return true;
        }
	}

	private void processFillCopyes(Cliche cliche, Map<String, FormulasAbstract> formulasValues, File file)
			throws GenerateValueException, CanselException {
		for (int i = 0; i < cliche.getAmountCopyes(); i++) {
			String xml = cliche.getMainText();
			String outFileName = cliche.getFileNameMask();
			for (Entry<String, FormulasAbstract> entry : formulasValues.entrySet()) {
				if(pd.isCanseled())
					throw new CanselException();
				String key = "\\$\\{" + entry.getKey() + "\\}";
				FormulasAbstract formula = entry.getValue();
				if (formula.isLoop()) {
					while (Pattern.compile(key).matcher(xml).find()) {
						if(pd.isCanseled())
							throw new CanselException();
						String val = formula.getValue();
						if (!cliche.getXmlVersion().isEmpty())
							val = StringEscapeUtils.escapeXml(val);
						xml = xml.replaceFirst(key, val);
					}

					while (Pattern.compile(key).matcher(outFileName).find()) {
						if(pd.isCanseled())
							throw new CanselException();
						outFileName = outFileName.replaceFirst(key, formula.getValue());
					}
				} else {
					String val = formula.getValue();
					if (!cliche.getXmlVersion().isEmpty())
						val = StringEscapeUtils.escapeXml(val);
					xml = xml.replaceAll(key, val);
					outFileName = outFileName.replaceAll(key, val);
				}
			}

			createOutFile(cliche, file, outFileName, xml);
			pd.setSecondBarIncValue();
		}
	}

	private void createOutFile(Cliche cliche, File file, String outFileName, String xml) {
		File realOutFolder = cliche.getToSubFolder()
				? new File(outFolder, file.getName().substring(0, file.getName().lastIndexOf(".")))
				: outFolder;
		if (!realOutFolder.exists())
			realOutFolder.mkdirs();
		File outFile = new File(realOutFolder, outFileName);
		try (BufferedWriter writer = new BufferedWriter(new PrintWriter(outFile, cliche.getEncoding()))) {
			if (!cliche.getXmlVersion().isEmpty())
				writer.write(
						"<?xml version=\"" + cliche.getXmlVersion() + "\" encoding=\"" + cliche.getEncoding() + "\"?>");
			writer.write(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, FormulasAbstract> initFormulas(Map<String, Map<String, Boolean>> calcFormulas)
			throws ActivateException {
		Map<String, FormulasAbstract> result = new HashMap<>(calcFormulas.size());

		pd.setSecondBarSize(calcFormulas.size());
		pd.setSecondLabelText("Preparing formulas");
		for (Entry<String, Map<String, Boolean>> entry : calcFormulas.entrySet()) {
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
			pd.setSecondBarIncValue();
		}
		return result;
	}

}
