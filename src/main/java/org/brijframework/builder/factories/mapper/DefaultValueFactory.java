package org.brijframework.builder.factories.mapper;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.brijframework.container.Container;
import org.brijframework.factories.Factory;


public class DefaultValueFactory implements Factory{
	
	private static Logger logger = Logger.getLogger(DefaultValueFactory.class.getName());
	private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
	private Container container;
	private static DefaultValueFactory factory;
	
	public DefaultValueFactory() {
	}

	public static DefaultValueFactory getFactory() {
		if (factory == null) {
			factory = new DefaultValueFactory();
			factory.loadFactory();
		}
		return factory;
	}

	public DefaultValueFactory loadFactory() {
		logger.info("DataTypeDefaultValueMapperFactory loading ...");
		this.registerDefaultValue("Integer", "0");
		this.registerDefaultValue("int", "0");
		this.registerDefaultValue("Float", "0.0f");
		this.registerDefaultValue("float", "0.0f");
		this.registerDefaultValue("Long", "0l");
		this.registerDefaultValue("long", "0l");
		this.registerDefaultValue("Character", Character.valueOf('\u0000'));
		this.registerDefaultValue("char", Character.valueOf('\u0000'));
		this.registerDefaultValue("Double", 0.0);
		this.registerDefaultValue("double", 0.0);
		this.registerDefaultValue("boolean", false);
		this.registerDefaultValue("Boolean", false);
		return this;
	}
	
	public Object getDefaultValue(String type) {
		if (type == null) {
			return String.class;
		}
		for (Entry<String, Object> entry : cache.entrySet()) {
			if (entry.getKey().equals(type)) {
				return entry.getValue();
			}
		}
		for (Entry<String, Object> entry : cache.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(type)) {
				return entry.getValue();
			}
		}
		return null;
	}


	public void registerDefaultValue(String key, Object value) {
		cache.put(key, value);
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
	public ConcurrentHashMap<String, Object> getCache() {
		return cache;
	}

	@Override
	public DefaultValueFactory clear() {
		getCache().clear();
		return this;
	}

}
