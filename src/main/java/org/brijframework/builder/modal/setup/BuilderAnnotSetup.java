package org.brijframework.builder.modal.setup;

import java.util.Map;

import org.brijframework.builder.modal.BuilderSetup;

public class BuilderAnnotSetup implements BuilderSetup {

	private String type;
	private Map<String, Object> properties;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}