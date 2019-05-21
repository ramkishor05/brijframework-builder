package org.brijframework.builder.factories.mapper;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.container.Container;
import org.brijframework.factories.Factory;

public class AnnotationFactory implements Factory{

	private static AnnotationFactory factory;
	private static Logger logger = Logger.getLogger(AnnotationFactory.class.getName());
	private Container container;
	private ConcurrentHashMap<String, Class<? extends Annotation>> cache = new ConcurrentHashMap<>();
	
	public AnnotationFactory() {
	}

	public static AnnotationFactory getFactory() {
		if (factory == null) {
			factory = new AnnotationFactory();
			factory.loadFactory();
		}
		return factory;
	}
	
	public AnnotationFactory loadFactory(){
		this.registerAnnotation("@Property", "org.brijframework.support.model.Property");
		this.registerAnnotation("@property", "org.brijframework.support.model.Property");
		this.registerAnnotation("com.catamaran.briovarx.annotations.Property","org.brijframework.support.model.Property");
		this.registerAnnotation("@model","org.brijframework.support.model.Model");
		this.registerAnnotation("@Model", "org.brijframework.support.model.Model");
		this.registerAnnotation("com.catamaran.briovarx.annotations.Model","org.brijframework.support.model.Model");
		return this;
	}
	
	public void registerAnnotation(String key, Class<? extends Annotation> value) {
		cache.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public void registerAnnotation(String key, String value) {
		try {
			registerAnnotation(key, (Class<? extends Annotation> )Class.forName(value));
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Annotation> getAnnotation(String type){
		if (cache.containsKey(type)) {
			return cache.get(type);
		}
		try {
			System.out.println(cache.containsKey(type)+"=type="+type);
			return (Class<? extends Annotation>) Class.forName(type);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container=container;
	}

	@Override
	public ConcurrentHashMap<String, Class<? extends Annotation>> getCache() {
		return cache;
	}

	@Override
	public Factory clear() {
		getCache().clear();
		return this;
	}
	
	
}
