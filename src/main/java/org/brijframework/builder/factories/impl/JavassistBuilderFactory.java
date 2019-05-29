package org.brijframework.builder.factories.impl;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.builder.asm.JavassistBuilder;
import org.brijframework.builder.factories.BuilderFactory;
import org.brijframework.container.Container;

public class JavassistBuilderFactory implements BuilderFactory {

	private static JavassistBuilderFactory factory;
	private static Logger logger = Logger.getLogger(JavaModelBuilderFactory.class.getName());
	private Container container;
	private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
	public JavassistBuilderFactory() {
	}

	public static JavassistBuilderFactory getFactory() {
		if (factory == null) {
			factory = new JavassistBuilderFactory();
		}
		return factory;
	}
	
	public JavassistBuilderFactory loadFactory(String path) {
		return loadFactory(new File(path));
	}

	public JavassistBuilderFactory loadFactory(File path) {
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
			new JavassistBuilder(path).build();
		} catch (Exception e) {
			logger.log(Level.ALL, e.getMessage(), e);
		}
	}

	@Override
	public JavassistBuilderFactory loadFactory() {
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
	public JavassistBuilderFactory clear() {
		getCache().clear();
		return this;
	}
}
