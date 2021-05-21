package ru.kids.copier.formulas;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentdateFormula extends FormulasAbstract {

	@Override
	public void init(String formulaArgs) {
		SimpleDateFormat sdf = new SimpleDateFormat(formulaArgs.trim().replace("'", ""));
		value = sdf.format(new Date());
	}

}
