package net.kumst.sillyorm;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reflection for SillyORM
 * 
 * @author Martin Kumst
 */
public class Reflection {
	
	private Object jReflectedObject;
	private Class jReflectedClass;
	
	/**
	 * Construct Reflection
	 * 
	 * @param reflectedObject The object being reflected and use in SillyORM
	 */
	public Reflection(Object reflectedObject) {
		if (reflectedObject == null) {
			throw new IllegalArgumentException("Invalid object for reflection");
		}
		jReflectedObject = reflectedObject;
		jReflectedClass = reflectedObject.getClass();
	}
	
	/**
	 * Constructs Reflection
	 * 
	 * @param reflectedClass The class to be reflected
	 */
	public Reflection(Class reflectedClass) {
		jReflectedClass = reflectedClass;
	}
	
	/**
	 * Returns the list of all variables to be used
	 * Id variable is excluded from the result
	 * 
	 * @return The list of variables
	 */
	public List<String> getAllVariables() {
		List<String> result = new ArrayList<>();
		for (Method m : jReflectedClass.getMethods()) {
			if (m.getName().matches("^get.+$")) {
				String varName = m.getName().substring(3).toLowerCase();
				if (hasSetMethod(varName) && (!varName.equals("id"))) {
					result.add(varName);
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	/**
	 * Returns the name of the entity
	 * 
	 * @return The entity name
	 */
	public String getEntityName() {
		return jReflectedClass.getSimpleName().toLowerCase();
	}
	
	/**
	 * Returns the value of the given variable
	 * 
	 * @param varName The variable name
	 * @return The value 
	 */
	public String getValue(String varName) {
		try {
			Method method = jReflectedClass.getMethod("get" + firstLetterToUpperCase(varName), null);
			Object value = method.invoke(jReflectedObject);
			return value == null ? null : value.toString();
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
			return null;
		}
	}
	
	/**
	 * Sets the value of the given variable
	 * 
	 * @param varName The variable to be set
	 * @param value The value to be set
	 */
	public void setValue(String varName, String value) {
		Method method;
		try {
			method = jReflectedClass.getMethod("set" + firstLetterToUpperCase(varName), String.class);
			method.invoke(jReflectedObject, value);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// We are silly, we do not care
		}
	}
	
	/**
	 * Makes the first letter in the given string uppercase
	 * 
	 * @param str The string to be capitalized
	 * @return The capitalized string
	 */
	private String firstLetterToUpperCase(String str) {
		return str.substring(0,1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * Checks whether the reflected class has a set method for the given variable name
	 * 
	 * @param varName The variable name
	 * @return True if there is such set method, false otherwise
	 */
	private boolean hasSetMethod(String varName) { 
		try {
			jReflectedClass.getMethod("set" + firstLetterToUpperCase(varName), String.class);
			return true;
		}
		catch (NoSuchMethodException e) {
			return false;
		}
	}
}
