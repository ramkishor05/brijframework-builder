package org.brijframework.builder.annotation;

public @interface Property {

	String id();

	String name();

	String[] mapping() default {};

    Data data() default Data.fal;
}
