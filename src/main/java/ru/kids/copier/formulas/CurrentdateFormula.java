package ru.kids.copier.formulas;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class CurrentdateFormula extends FormulasAbstract {

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {

		String arg = formulaArgs.trim().replace("'", "");
		if(!arg.isEmpty()) {
			SimpleDateFormat sdf;
			try {
				sdf = new SimpleDateFormat(arg);
			} catch (Exception e) {
				throw new InitGeneratorValueException("Date or date and time format error ("+formulaArgs+").");
			}
			value = sdf.format(new Date());
		} else {
			value = (new Date()).toString();
		}
	}

}
