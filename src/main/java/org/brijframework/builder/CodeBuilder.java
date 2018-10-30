package org.brijframework.builder;

import java.io.File;

public interface CodeBuilder {

	void build();
	
	void build(String codebase);

	void build(File codebase);

	void generator();

}
