package org.brijframework.builder.factories;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.builder.ModelBuilder;

public class CodeBuilderFactory {
	private static CodeBuilderFactory factory;
	private static Logger logger = Logger.getLogger(ModelBuilder.class.getName());
	private CodeBuilderFactory() {
	}

	public static CodeBuilderFactory getFactory() {
		if (factory == null) {
			factory = new CodeBuilderFactory();
		}
		return factory;
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
			new ModelBuilder(path).build();
		} catch (Exception e) {
			logger.log(Level.ALL, e.getMessage(), e);
		}
	}
}
