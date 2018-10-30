package org.brijframework.builder.factories;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationMapperFactory {

	private static AnnotationMapperFactory factory;
	private static Logger logger = Logger.getLogger(AnnotationMapperFactory.class.getName());
	
	private Map<String, Class<? extends Annotation>> annotationMapper = new HashMap<>();
	
	private AnnotationMapperFactory() {
	}

	public static AnnotationMapperFactory getFactory() {
		if (factory == null) {
			factory = new AnnotationMapperFactory();
			factory.loadFactory();
		}
		return factory;
	}
	
	public void loadFactory(){
		this.registerAnnotationMapper("@Property", "org.brijframework.builder.annotation.Property");
		this.registerAnnotationMapper("@property", "org.brijframework.builder.annotation.Property");
		this.registerAnnotationMapper("com.catamaran.briovarx.annotations.Property","org.brijframework.builder.annotation.Property");
		this.registerAnnotationMapper("@model","org.brijframework.builder.annotation.Model");
		this.registerAnnotationMapper("@Model", "org.brijframework.builder.annotation.Model");
		this.registerAnnotationMapper("com.catamaran.briovarx.annotations.Model","org.brijframework.builder.annotation.Model");
	}
	
	public void registerAnnotationMapper(String key, Class<? extends Annotation> value) {
		annotationMapper.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public void registerAnnotationMapper(String key, String value) {
		try {
			registerAnnotationMapper(key, (Class<? extends Annotation> )Class.forName(value));
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Annotation> getAnnotationMapper(String type){
		if (annotationMapper.containsKey(type)) {
			return annotationMapper.get(type);
		}
		try {
			System.out.println(annotationMapper.containsKey(type)+"=type="+type);
			return (Class<? extends Annotation>) Class.forName(type);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	
}
