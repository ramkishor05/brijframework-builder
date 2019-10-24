package org.brijframework.builder.modal.setup;

import java.util.List;
import java.util.Map;

import org.brijframework.builder.modal.BuilderSetup;

public class BuilderModelSetup implements BuilderSetup {

	private String type;

	private String name;
	
	private String extend;

	private List<String> implementions;

	private List<BuilderAnnotSetup> annotations;

	private Map<String, BuilderProptiSetup> properties;

	private List<String> hash;

	private List<String> tostring;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}
	
	public String getExtend() {
		return extend;
	}
	
	public List<String> getImplementions() {
		return implementions;
	}

	public void setImplementions(List<String> implementions) {
		this.implementions = implementions;
	}

	public List<BuilderAnnotSetup> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BuilderAnnotSetup> annotations) {
		this.annotations = annotations;
	}

	public void setProperties(Map<String, BuilderProptiSetup> properties) {
		this.properties = properties;
	}

	public Map<String, BuilderProptiSetup> getProperties() {
		return properties;
	}

	public List<String> getHash() {
		return hash;
	}

	public void setHash(List<String> hash) {
		this.hash = hash;
	}

	public List<String> getTostring() {
		return tostring;
	}

	public void setTostring(List<String> tostring) {
		this.tostring = tostring;
	}

}
