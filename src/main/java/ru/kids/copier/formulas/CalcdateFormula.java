package ru.kids.copier.formulas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.kids.copier.exceptions.InitGeneratorValueException;

public class CalcdateFormula extends FormulasAbstract {

	@Override
	public void init(String formulaArgs) throws InitGeneratorValueException {
		String[] args = formulaArgs.split(",");
		if(args.length < 2)
			throw new InitGeneratorValueException("Required parameters are not specified.");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		for (int i = 0; i < args.length - 1; i = i + 2) {
			int val = 0;
			try {
				val = Integer.parseInt(args[i].trim());
			} catch (Exception e) {
				throw new InitGeneratorValueException("Error converting the parameter value to a number. ("+args[i].trim()+")");
			}
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
			default :
				throw new InitGeneratorValueException("The function parameter is set incorrectly ("+type+"). Acceptable parameters: day, week, month, year.");
			}
		}
		
		if(args.length % 2 > 0) {
			SimpleDateFormat sdf;
			try {
				sdf = new SimpleDateFormat(args[args.length - 1].trim().replace("'", ""));
			} catch (Exception e) {
				throw new InitGeneratorValueException("Date or date and time format error ("+args[args.length - 1]+").");
			}
			value = sdf.format(cal.getTime());
		} else {
			value = cal.toString();
		}
	}

}
