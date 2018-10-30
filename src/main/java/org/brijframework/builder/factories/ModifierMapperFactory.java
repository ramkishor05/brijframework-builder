package org.brijframework.builder.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.codemodel.JMod;

public class ModifierMapperFactory {

	private static ModifierMapperFactory factory;
	private static Logger logger = Logger.getLogger(ModifierMapperFactory.class.getName());
	
	private Map<String, Integer> modifierMapper = new HashMap<>();
	
	private ModifierMapperFactory() {
	}

	public static ModifierMapperFactory getFactory() {
		if (factory == null) {
			factory = new ModifierMapperFactory();
			factory.loadFactory();
		}
		return factory;
	}

	private void loadFactory() {
		this.registerModMapper("NONE", JMod.NONE);
		this.registerModMapper("PUBLIC", JMod.PUBLIC);
		this.registerModMapper("PROTECTED", JMod.PROTECTED);
		this.registerModMapper("PRIVATE", JMod.PRIVATE);
		this.registerModMapper("FINAL", JMod.FINAL);
		this.registerModMapper("STATIC", JMod.STATIC);
		this.registerModMapper("ABSTRACT", JMod.ABSTRACT);
		this.registerModMapper("NATIVE", JMod.NATIVE);
		this.registerModMapper("SYNCHRONIZED", JMod.SYNCHRONIZED);
		this.registerModMapper("TRANSIENT", JMod.TRANSIENT);
		this.registerModMapper("VOLATILE", JMod.VOLATILE);
		logger.log(Level.SEVERE, "Modifier mapper loaded..");
	}
	
	public void registerModMapper(String key, int value) {
		modifierMapper.put(key, value);
	}
	
	public int getModMapper(String mod) {
		for (String key : modifierMapper.keySet()) {
			if (key.equalsIgnoreCase(mod)) {
				return modifierMapper.get(key);
			}
		}
		return JMod.NONE;
	}
}
