package org.brijframework.builder.modal.setup;

import java.util.List;

import org.brijframework.builder.modal.BuilderSetup;

public class BuilderProptiSetup implements BuilderSetup {

	private String type;
	private String name;
	private List<BuilderAnnotSetup> annotations;

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

	public List<BuilderAnnotSetup> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BuilderAnnotSetup> annotations) {
		this.annotations = annotations;
	}
}
