package org.brijframework.builder.factories.impl;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.builder.CodeBuilderImpl;
import org.brijframework.builder.factories.BuilderFactory;
import org.brijframework.container.Container;

public class CodeBuilderFactory implements BuilderFactory {
	
	private static CodeBuilderFactory factory;
	private static Logger logger = Logger.getLogger(CodeBuilderFactory.class.getName());
	private Container container;
	private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
	public CodeBuilderFactory() {
	}

	public static CodeBuilderFactory getFactory() {
		if (factory == null) {
			factory = new CodeBuilderFactory();
		}
		return factory;
	}
	
	public CodeBuilderFactory loadFactory(String path) {
		return loadFactory(new File(path));
	}

	public CodeBuilderFactory loadFactory(File path) {
		Objects.requireNonNull(path, "Path should not be null");
		if (path.isDirectory()) {
			for (File file : path.listFiles()) {
				load(file);
			}
		} else {
			load(path);
		}
		return this;
	}

	private void load(File path) {
		try {
			new CodeBuilderImpl(path).build();
		} catch (Exception e) {
			logger.log(Level.ALL, e.getMessage(), e);
		}
	}

	@Override
	public CodeBuilderFactory loadFactory() {
		return this;
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
	public CodeBuilderFactory clear() {
		getCache().clear();
		return this;
	}
}
