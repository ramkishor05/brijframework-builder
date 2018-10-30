package org.brijframework.builder.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class ValueMapperFactory {
	//private static Logger logger = Logger.getLogger(ValueMapperFactory.class.getName());
	private Map<String, Object> valueMapper = new HashMap<>();
	private static ValueMapperFactory factory;
	private ValueMapperFactory() {
	}

	public static ValueMapperFactory getFactory() {
		if (factory == null) {
			factory = new ValueMapperFactory();
			factory.loadFactory();
		}
		return factory;
	}

	private void loadFactory() {
		this.registerValueMapper("Integer", "0");
		this.registerValueMapper("int", "0");
		this.registerValueMapper("Float", "0.0f");
		this.registerValueMapper("float", "0.0f");
		this.registerValueMapper("Long", "0l");
		this.registerValueMapper("long", "0l");
		this.registerValueMapper("Character", Character.valueOf('\u0000'));
		this.registerValueMapper("char", Character.valueOf('\u0000'));
		this.registerValueMapper("Double", 0.0);
		this.registerValueMapper("double", 0.0);
		this.registerValueMapper("boolean", false);
		this.registerValueMapper("Boolean", false);
	}
	
	public Object getValueMapper(String type) {
		if (type == null) {
			return String.class;
		}
		for (Entry<String, Object> entry : valueMapper.entrySet()) {
			if (entry.getKey().equals(type)) {
				return entry.getValue();
			}
		}
		for (Entry<String, Object> entry : valueMapper.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(type)) {
				return entry.getValue();
			}
		}
		return null;
	}


	public void registerValueMapper(String key, Object value) {
		valueMapper.put(key, value);
	}

}
