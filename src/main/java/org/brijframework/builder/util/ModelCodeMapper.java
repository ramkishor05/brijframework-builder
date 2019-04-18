package org.brijframework.builder.util;

public final class ModelCodeMapper {
	private ModelCodeMapper() {
	}
	public static String getSetterKey(String key) {
		return "set" + key.replace(key.charAt(0), Character.toUpperCase(key.charAt(0)));
	}

	public static String getGetterKey(Class<?> type, String key) {
		if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
			return "is" + key.replace(key.charAt(0), Character.toUpperCase(key.charAt(0)));
		} else {
			return "get" + key.replace(key.charAt(0), Character.toUpperCase(key.charAt(0)));
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T castValue(Class type, T value) {
		if (type.isEnum()) {
			return (T) Enum.valueOf(type, String.valueOf(value));
		}
		if (Integer.class.isAssignableFrom(type)|| int.class.isAssignableFrom(type)) {
			return (T) Integer.valueOf(String.valueOf(value));
		}
		if (Float.class.isAssignableFrom(type)|| float.class.isAssignableFrom(type)) {
			return (T) Float.valueOf(String.valueOf(value));
		}
		if (Double.class.isAssignableFrom(type)|| double.class.isAssignableFrom(type)) {
			return (T) Double.valueOf(String.valueOf(value));
		}
		if (Long.class.isAssignableFrom(type)|| long.class.isAssignableFrom(type)) {
			return (T) Long.valueOf(String.valueOf(value));
		}
		if (Boolean.class.isAssignableFrom(type)|| boolean.class.isAssignableFrom(type)) {
			return (T) Boolean.valueOf(String.valueOf(value));
		}
		if (Character.class.isAssignableFrom(type)|| char.class.isAssignableFrom(type)) {
			return (T) Character.valueOf(String.valueOf(value).charAt(0));
		}
		return value;
	}
}
