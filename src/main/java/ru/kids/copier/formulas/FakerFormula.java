package ru.kids.copier.formulas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.github.javafaker.Faker;

import ru.kids.copier.exceptions.ActivateException;
import ru.kids.copier.exceptions.GenerateValueException;

public class FakerFormula extends FormulasAbstract {

	private String formulaArgs;
	private String[] argumentsArr;
	private Constructor<?> constr;
	private Method m;
	private String[] args;
	private Object[] params;
	private static Faker f = new Faker(new Locale("ru"));

	@Override
	public void init(String formulaArgs) throws ActivateException {
		this.formulaArgs = formulaArgs;
		args = formulaArgs.trim().split(",");
		String firstPatchClass = args[0].trim().contains("(")? args[0].trim().substring(0, args[0].trim().indexOf("(")): args[0].trim();
		
		String[] args1 = firstPatchClass.split("\\.");
		Class<?> findClass = f.getClass();

		try {
			for (int i = 0; i < args1.length - 1; i++)
				findClass = findClass.getDeclaredMethod(args1[i]).getReturnType();

			String function = args1[args1.length - 1];
			String arguments = "";
			if (args[0].trim().contains("(")) 
				arguments = args[0].trim().substring(args[0].trim().indexOf("(") + 1, args[0].trim().lastIndexOf(")"));

			argumentsArr = arguments.isEmpty() ? new String[] {} : arguments.trim().split(";");
			params = new Object[argumentsArr.length];
			m = getMethod(argumentsArr, findClass, function, params);
			if (m == null)
				throw new ActivateException("Error when forming the function value faker(" + formulaArgs + ")");
			m.setAccessible(true);
			Constructor<?>[] constrs = findClass.getDeclaredConstructors();
			constr = constrs[0];
			constr.setAccessible(true);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFormulaValue() throws GenerateValueException {
		
		String result = "";

		try {
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

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			e.printStackTrace();
			throw new GenerateValueException("Error when forming the function value faker(" + formulaArgs + ")");
		}
		
		return result;
	}

	private Method getMethod(final String[] arguments, final Class<?> findClass, final String function,
			Object[] reasult) throws SecurityException {
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
							case "string":
								reasult[i] = arg.replace("'", "").trim();
								break;
							default:
								reasult[i] = getTypeValue(arg.trim());
								break;
							}
						}
						return method;
					} catch (Exception ignore) {
						// ignoring conversion errors
					}
				}
			}
		}
		return null;
	}

	private Object getTypeValue(String arg) throws NoSuchFieldException, ClassNotFoundException {
		
		Class<?> findClass = getClass().getClassLoader().loadClass(arg.substring(0, arg.lastIndexOf('.')));
		
		if(findClass.isEnum())
			for(Object enumObject :findClass.getEnumConstants()) 
				if(enumObject.toString().equalsIgnoreCase(arg.substring(arg.lastIndexOf('.')+1)))
					return enumObject;
				
		return null;
	}
}
