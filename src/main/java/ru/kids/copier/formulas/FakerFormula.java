package ru.kids.copier.formulas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.github.javafaker.Faker;

public class FakerFormula extends FormulasAbstract {

	String formulaArgs;

	@Override
	public void init(String formulaArgs) {
		this.formulaArgs = formulaArgs;
	}

	@Override
	public String getValue() {
		String result = "";
		String[] args = formulaArgs.trim().split(",");
		String[] args1 = args[0].trim().split("\\.");
		Faker f = new Faker(new Locale("ru"));
		Class<?> findClass = f.getClass();

		try {
			for (int i = 0; i < args1.length - 1; i++)
				findClass = findClass.getDeclaredMethod(args1[i]).getReturnType();

			String function = args1[args1.length - 1];
			String arguments = "";
			if (function.contains("(")) {
				arguments = function.substring(function.indexOf("(") + 1, function.lastIndexOf(")"));
				function = function.substring(0, function.indexOf("(")).toLowerCase();
			}
			String[] argumentsArr = arguments.isEmpty() ? new String[] {} : arguments.trim().split(";");
			Object[] params = new Object[argumentsArr.length];
			Method m = getMethod(argumentsArr, findClass, function, params);
			if (m == null)
				return "Ошибка при формировании значения функции faker(" + formulaArgs + ")";
			m.setAccessible(true);
			Constructor<?>[] constrs = findClass.getDeclaredConstructors();
			Constructor<?> constr = constrs[0];
			constr.setAccessible(true);

			Object res;

			if (argumentsArr.length == 0)
				res = m.invoke(constr.newInstance(f));
			else
				res = m.invoke(constr.newInstance(f), params);

			if (res instanceof String)
				result = (String) res;
			else if (res instanceof Date) {
				if (args.length > 1) {
					SimpleDateFormat sdf = new SimpleDateFormat(args[1].trim().replace("'", ""));
					result = sdf.format(res);
				} else
					result = res.toString();
			} else if (res instanceof Integer)
				result = res.toString();

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException e) {
			e.printStackTrace();
		}
		return result;
	}

	private Method getMethod(final String[] arguments, final Class<?> findClass, final String function,
			Object[] reasult) throws NoSuchMethodException, SecurityException {
		for (Method method : findClass.getDeclaredMethods()) {
			if (method.getName().equalsIgnoreCase(function)) {
				Parameter[] parameters = method.getParameters();
				if (parameters.length == arguments.length) {
					try {
						for (int i = 0; i < arguments.length; i++) {
							Parameter parameter = parameters[i];
							String arg = arguments[i];
							switch (parameter.getType().getTypeName().toLowerCase()) {
							case "integer":
							case "int":
								reasult[i] = Integer.parseInt(arg);
								break;
							case "boolean":
								reasult[i] = Boolean.parseBoolean(arg);
								break;
							case "date":
								reasult[i] = new SimpleDateFormat().parse(arg);
								break;
							default:
								reasult[i] = arg.replace("'", "").trim();
								break;
							}
						}
						return method;
					} catch (Exception ignore) {
					}
				}
			}
		}
		return null;
	}
}
