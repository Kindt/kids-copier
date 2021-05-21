package ru.kids.copier.formulas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalcdateFormula extends FormulasAbstract {

	@Override
	public void init(String formulaArgs) {
		String[] args = formulaArgs.split(",");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		for (int i = 0; i < args.length - 1; i = i + 2) {
			int val = Integer.parseInt(args[i].trim());
			String type = args[i + 1].trim().replace("'", "");
			switch (type.toLowerCase()) {
			case "day":
				cal.add(Calendar.DAY_OF_YEAR, val);
				break;
			case "week":
				cal.add(Calendar.WEEK_OF_YEAR, val);
				break;
			case "month":
				cal.add(Calendar.MONTH, val);
				break;
			case "year":
				cal.add(Calendar.YEAR, val);
				break;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat(args[args.length - 1].trim().replace("'", ""));
		value = sdf.format(cal.getTime());
	}

}
