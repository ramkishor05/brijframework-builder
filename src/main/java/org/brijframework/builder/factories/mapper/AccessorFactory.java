package org.brijframework.builder.factories.mapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.container.Container;
import org.brijframework.factories.Factory;
import org.brijframework.util.support.Access;

public class AccessorFactory implements Factory{

	private static AccessorFactory factory;
	
	private static Logger logger = Logger.getLogger(AccessorFactory.class.getName());
	private Container container;
	private ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();
	
	private AccessorFactory() {
	}

	public static AccessorFactory getFactory() {
		if (factory == null) {
			factory = new AccessorFactory();
			factory.loadFactory();
		}
		return factory;
	}

	public AccessorFactory loadFactory() {
		for(Access access:Access.values()) {
			this.registerAccessModifier(access.toString(),access.getID());
		}
		logger.log(Level.SEVERE, "Modifier mapper loaded..");
		return this;
	}
	
	public void registerAccessModifier(String key, int value) {
		getCache().put(key, value);
	}
	
	public Integer getAccessModifier(String mod) {
		for (String key : getCache().keySet()) {
			if (key.equalsIgnoreCase(mod)) {
				return getCache().get(key);
			}
		}
		return 0;
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
	public ConcurrentHashMap<String, Integer> getCache() {
		return cache;
	}

	@Override
	public AccessorFactory clear() {
		getCache().clear();
		return this;
	}
}
