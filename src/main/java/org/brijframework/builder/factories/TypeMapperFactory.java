package org.brijframework.builder.factories;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TypeMapperFactory {
	private static Logger logger = Logger.getLogger(TypeMapperFactory.class.getName());
	private Map<String, Class<?>> typeMapper = new HashMap<>();
	private static Map<Class<?>, Class<?>[]> asmMapper = new HashMap<>();
	private static TypeMapperFactory factory;

	private TypeMapperFactory() {
	}

	public static TypeMapperFactory getFactory() {
		if (factory == null) {
			factory = new TypeMapperFactory();
			factory.loadFactory();
		}
		return factory;
	}

	private void loadFactory() {
		this.registerTypeMapper("String", String.class);
		this.registerTypeMapper("StringBuilder", StringBuilder.class);
		this.registerTypeMapper("StringBuffer", StringBuffer.class);
		this.registerTypeMapper("Integer", Integer.class);
		this.registerTypeMapper("int", int.class);
		this.registerTypeMapper("Float", Float.class);
		this.registerTypeMapper("float", float.class);
		this.registerTypeMapper("Long", Long.class);
		this.registerTypeMapper("long", long.class);
		this.registerTypeMapper("Character", Character.class);
		this.registerTypeMapper("char", char.class);
		this.registerTypeMapper("Double", Double.class);
		this.registerTypeMapper("double", double.class);
		this.registerTypeMapper("Date", Date.class);
		this.registerTypeMapper("date", Date.class);
		this.registerTypeMapper("Time", Time.class);
		this.registerTypeMapper("time", Time.class);
		this.registerTypeMapper("boolean", boolean.class);
		this.registerTypeMapper("Boolean", Boolean.class);

		// type same
		this.registerAsmMapper(boolean.class, Boolean.class, boolean.class);
		this.registerAsmMapper(int.class, Integer.class, int.class);
		this.registerAsmMapper(float.class, Float.class, float.class);
		this.registerAsmMapper(double.class, Double.class, double.class);
		this.registerAsmMapper(long.class, Long.class, long.class);
		this.registerAsmMapper(char.class, Character.class, char.class);
		this.registerAsmMapper(Integer.class, int.class, Integer.class);
		this.registerAsmMapper(Float.class, float.class, Float.class);
		this.registerAsmMapper(Float.class, double.class, Float.class);
		this.registerAsmMapper(Boolean.class, boolean.class, Boolean.class);
	}

	public Class<?> getTypeMapper(String type) {
		if (type == null) {
			return String.class;
		}
		for (Entry<String, Class<?>> entry : typeMapper.entrySet()) {
			if (entry.getKey().equals(type)) {
				return entry.getValue();
			}
		}
		for (Entry<String, Class<?>> entry : typeMapper.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(type)) {
				return entry.getValue();
			}
		}
		try {
			return Class.forName(type);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return String.class;
	}

	public void registerTypeMapper(String key, Class<?> value) {
		typeMapper.put(key, value);
	}
	
	public boolean isAssignable(Class<?> lcls, Class<?> rcls) {
		if (asmMapper.containsKey(lcls)) {
			for(Class<?> cls:asmMapper.get(lcls)){
				if(cls.isAssignableFrom(rcls)){
					return true;
				}
			}
		}
		return lcls.isAssignableFrom(rcls);
	}

	public void registerAsmMapper(Class<?> key, Class<?>... values) {
		asmMapper.put(key, values);
	}

}
