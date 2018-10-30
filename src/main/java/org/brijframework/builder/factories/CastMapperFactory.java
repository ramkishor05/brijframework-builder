package org.brijframework.builder.factories;

import java.util.HashMap;
import java.util.Map;

public class CastMapperFactory {

	private static CastMapperFactory factory;
//	private static Logger logger = Logger.getLogger(AnnotationMapperFactory.class.getName());
	
	private static Map<Class<?>, Class<?>[]> castMapper = new HashMap<>();
	
	private CastMapperFactory() {
	}

	public static CastMapperFactory getFactory() {
		if (factory == null) {
			factory = new CastMapperFactory();
			factory.loadFactory();
		}
		return factory;
	}

	private void loadFactory() {
		this.registerCastMapper(boolean.class, Boolean.class, String.class);
		this.registerCastMapper(int.class, Integer.class, String.class);
		this.registerCastMapper(float.class, Float.class, String.class);
		this.registerCastMapper(double.class, Double.class, String.class);
		this.registerCastMapper(long.class, Long.class, String.class);
		this.registerCastMapper(char.class, Character.class, String.class);
		this.registerCastMapper(Integer.class, int.class, String.class);
		this.registerCastMapper(Float.class, float.class, String.class);
		this.registerCastMapper(Double.class, double.class, String.class);
		this.registerCastMapper(Boolean.class, boolean.class, String.class);
	}
	
	public  void registerCastMapper(Class<?> key, Class<?>... values) {
		castMapper.put(key, values);
	}
}
