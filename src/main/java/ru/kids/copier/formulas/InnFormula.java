package ru.kids.copier.formulas;

import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class InnFormula extends FormulasAbstract {

	private int size = 10;
	protected String kodNO = "";

	private static final Pattern innPatter = Pattern.compile("\\d{10}||\\d{12}");

	private static final int[] checkArr = new int[] { 3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8 };

	protected static final String[] ocatoCodes = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09",
			"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
			"28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45",
			"46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63",
			"64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "83", "86",
			"87", "89", "91", "92", "99" };

	protected static final Random rnd = new Random();

	@Override
	public void init(String formulaArgs) {
		String[] args = formulaArgs.split(",");
		String type = args[0].trim().replace("'", "").toUpperCase();
		if ("INUL".equals(type))
			kodNO = "9909";
		else {
			if ("FL".equals(type))
				size = 12;
			if (args.length > 1)
				kodNO = args[1].trim().replace("'", "");
			else
				kodNO = ocatoCodes[rnd.nextInt(ocatoCodes.length)];
		}
		if (kodNO.length() == 2)
			kodNO += StringUtils.leftPad(rnd.nextInt(100) + "", 2, '0');
	}

	@Override
	public String getValue() {
		String inn = generateInn();
		while (!isValidINN(inn)) {
			inn = generateInn();
		}
		return inn;
	}

	private String generateInn() {
		StringBuilder result = new StringBuilder(kodNO);

		result.append(StringUtils.leftPad(rnd.nextInt(size == 10 ? 100000 : 1000000) + "", size == 10 ? 5 : 6, '0'));
		if (size == 10)
			result.append(getSumm(result.toString(), 1, 2));
		else {
			result.append(getSumm(result.toString(), 2, 1));
			result.append(getSumm(result.toString(), 1, 0));
		}
		return result.toString();
	}

	private boolean isValidINN(String inn) {
		if (!innPatter.matcher(inn).matches()) {
			return false;
		}
		if (size == 12) {
			return innStep(inn, 2, 1) && innStep(inn, 1, 0);
		} else {
			return innStep(inn, 1, 2);
		}
	}

	private boolean innStep(String inn, int offset, int arrOffset) {
		return getSumm(inn, offset, arrOffset) == inn.charAt(inn.length() - offset) - '0';
	}

	private int getSumm(String inn, int offset, int arrOffset) {
		int sum = 0;
		for (int i = 0; i < size - offset; i++) {
			sum += (inn.charAt(i) - '0') * checkArr[i + arrOffset];
		}
		return (sum % 11) % 10;
	}
}
