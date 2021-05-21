package ru.kids.copier.formulas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class RschetFormula extends FormulasAbstract {

	private static final Random rnd = new Random();
	private StringBuilder rschet;
	private int nomPor = 1;
	private static final String[] kodVals = new String[] { "008", "012", "032", "036", "044", "048", "050", "051",
			"052", "060", "064", "068", "072", "084", "090", "096", "104", "108", "116", "124", "132", "136", "144",
			"152", "156", "170", "174", "188", "191", "192", "203", "208", "214", "222", "230", "232", "238", "242",
			"262", "270", "292", "320", "324", "328", "332", "340", "344", "348", "352", "356", "360", "364", "368",
			"376", "388", "392", "398", "400", "404", "408", "410", "414", "417", "418", "422", "426", "430", "434",
			"728", "446", "454", "458", "462", "929", "480", "484", "496", "498", "504", "512", "516", "524", "532",
			"533", "548", "554", "558", "566", "578", "586", "590", "598", "600", "604", "608", "634", "643", "646",
			"654", "682", "690", "694", "702", "704", "706", "710", "748", "752", "756", "760", "764", "776", "780",
			"784", "788", "800", "807", "818", "826", "834", "840", "858", "860", "882", "886", "901", "931", "932",
			"934", "936", "938", "940", "941", "943", "944", "946", "949", "950", "951", "952", "953", "960", "968",
			"969", "970", "971", "972", "973", "975", "976", "977", "978", "980", "981", "985", "986", "967", "933",
			"930", "928" };
	private static final String[] fCodesS = new String[] { "102-111", "202", "203", "204", "301-329", "401-406", "407",
			"408", "409", "410-426", "427-440", "441-457", "458", "459", "460-473", "474-479", "501-528", "601-621",
			"706-708", "801-855", "909-971" };
	private static List<String> fCodes = new ArrayList<>();

	@Override
	public String getValue() {
		StringBuilder result = new StringBuilder(rschet).append(StringUtils.leftPad((nomPor++) + "", 7, '0'));

		int checkSumm = 0;
		for (int i = 0; i < result.length(); i++) {
			checkSumm += Integer.parseInt(result.substring(i, i + 1)) * (i % 3 == 0 ? 7 : (i % 3 == 1 ? 1 : 3));
		}

		checkSumm = ((checkSumm % 10) * 3) % 10;
		result.replace(8, 9, checkSumm + "");
		return result.toString();
	}

	@Override
	public void init(String formulaArgs) {
		String kodval;
		String firstR;
		String secondR;
		String nomFil;
		String[] args = formulaArgs.isEmpty() ? new String[] {} : formulaArgs.split(",");

		if (args.length > 0)
			kodval = args[0].trim().replace("'", "");
		else
			kodval = kodVals[rnd.nextInt(kodVals.length)];

		if (args.length > 1)
			firstR = args[1].trim().replace("'", "");
		else {
			if (fCodes.isEmpty())
				generateFCodes();
			firstR = fCodes.get(rnd.nextInt(fCodes.size()));
		}

		if (args.length > 2)
			secondR = args[2].trim().replace("'", "");
		else
			secondR = StringUtils.leftPad(rnd.nextInt(100) + "", 2, '0');

		if (args.length > 3)
			nomFil = StringUtils.leftPad(args[3].trim().replace("'", ""), 4, '0');
		else
			nomFil = StringUtils.leftPad(rnd.nextInt(10000) + "", 4, '0');

		if (args.length > 4)
			nomPor = Integer.parseInt(args[4].trim().replace("'", ""));

		rschet = new StringBuilder(firstR);
		rschet.append(secondR);
		rschet.append(kodval);
		rschet.append(0);
		rschet.append(nomFil);
	}

	private void generateFCodes() {
		for (String code : fCodesS) {
			if (code.contains("-")) {
				String[] codesSplit = code.split("-");
				int codeStart = Integer.parseInt(codesSplit[0]);
				int codeEnd = Integer.parseInt(codesSplit[1]);
				for (int i = codeStart; i <= codeEnd; i++)
					fCodes.add(StringUtils.leftPad(i + "", 3, '0'));
			} else
				fCodes.add(code);
		}

	}
}
