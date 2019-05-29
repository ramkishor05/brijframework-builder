package org.brijframework.builder;

import java.io.File;

public interface Builder {

	void build();
	
	void build(String codebase);

	void build(File codebase);

	void generator();

}
