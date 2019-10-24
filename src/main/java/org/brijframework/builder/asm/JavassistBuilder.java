package org.brijframework.builder.asm;

import static org.brijframework.builder.util.BuilderConstants.ANNO_KEY;
import static org.brijframework.builder.util.BuilderConstants.ARG_KEY;
import static org.brijframework.builder.util.BuilderConstants.CONSTR_KEY;
import static org.brijframework.builder.util.BuilderConstants.EXTEND_KEY;
import static org.brijframework.builder.util.BuilderConstants.FIELD_KEY;
import static org.brijframework.builder.util.BuilderConstants.HASH_KEY;
import static org.brijframework.builder.util.BuilderConstants.IMPLS_KEY;
import static org.brijframework.builder.util.BuilderConstants.LOGIC_KEY;
import static org.brijframework.builder.util.BuilderConstants.MODIFIER_KEY;
import static org.brijframework.builder.util.BuilderConstants.NAME_KEY;
import static org.brijframework.builder.util.BuilderConstants.PARAM_KEY;
import static org.brijframework.builder.util.BuilderConstants.TO_STRING_KEY;
import static org.brijframework.builder.util.BuilderConstants.TYPE_KEY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.builder.Builder;
import org.brijframework.builder.factories.mapper.AccessorFactory;
import org.brijframework.builder.factories.mapper.AnnotationFactory;
import org.brijframework.builder.factories.mapper.DefaultTypeFactory;
import org.brijframework.util.accessor.EventValidateUtil;
import org.brijframework.util.casting.CastingUtil;
import org.brijframework.util.resouces.JSONUtil;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class JavassistBuilder implements Builder{

	private static Logger logger = Logger.getLogger(JavaModelBuilder.class.getName());
	private File sourcePath;

	private Map<String, Object> clsMap = new HashMap<>();
	private File codebase;
	private javassist.ClassPool codeModel = new javassist.ClassPool();

	public JavassistBuilder() {
		this.codebase = new File("src/main/java/");
		if (!codebase.exists()) {
			this.codebase = new File("src/");
		}
		if (!codebase.exists()) {
			this.codebase = new File("/");
		}
	}

	public JavassistBuilder(String sourcePath) {
		this(new File(sourcePath));
	}

	public JavassistBuilder(File sourcePath) {
		this();
		this.sourcePath = sourcePath;
		this.init();
	}

	public void init() {
		try {
			loadResource(this.sourcePath);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void loadResource(File sourcePath) throws Exception {
		if (!sourcePath.exists()) {
			throw new FileNotFoundException();
		}
		this.clsMap = JSONUtil.toMap(getSourceCode(sourcePath));
		if (clsMap == null || clsMap.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid resource");
			return;
		}
		generator();
	}

	public String getSourceCode(File sourcePath) throws IOException {
		return new String(Files.readAllBytes(sourcePath.toPath()));
	}

	@Override
	public void generator() {
		String type = getTypeSource();
		if ("enum".equalsIgnoreCase(type)) {
			JDefinedClass mdlCls = getDefinedEnum(getNameSource());
			loadProperties(mdlCls, getProperties());
			loadConstructors(mdlCls, getConstructors());
		} else if ("class".equalsIgnoreCase(type)) {
			System.out.println(getNameSource());
			CtClass mdlCls = getDefinedClass(getNameSource());
			mdlCls.setModifiers(Modifier.PUBLIC);
			try {
				System.out.println(mdlCls.toClass());
			} catch (CannotCompileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//loadExtendClass(mdlCls, getExtends());
			try {
				mdlCls.writeFile();
			} catch (NotFoundException | IOException | CannotCompileException e) {
				e.printStackTrace();
			}
			/*loadInterfaces(mdlCls, getImplements());
			loadProperties(mdlCls, getProperties());
			loadMethods(mdlCls, getMethodResourceList());
			loadAnnotations(mdlCls, getAnnotations());
			loadConstructors(mdlCls, getConstructors());
			addHashCode(mdlCls, getHashCode());
			addToString(mdlCls, getToString());*/
		} else if ("interface".equalsIgnoreCase(type)) {
			/*JDefinedClass mdlCls = getDefinedInterface(getNameSource());
			loadExtendClass(mdlCls, getExtends());
			loadInterfaces(mdlCls, getImplements());
			loadProperties(mdlCls, getProperties());
			loadMethods(mdlCls, getMethodResourceList());
			loadAnnotations(mdlCls, getAnnotations());*/
		} else if ("@interface".equalsIgnoreCase(type)) {
			/*JDefinedClass mdlCls = getDefinedAnnotation(getNameSource());
			loadProperties(mdlCls, getProperties());
			loadMethods(mdlCls, getMethodResourceList());
			loadAnnotations(mdlCls, getAnnotations());*/
		} else {
			throw new IllegalArgumentException("UNKNOWN SOURCE TYPE");
		}
		/*try {
			//ClassLoader.getSystemClassLoader().loadClass(getNameSource());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}*/
	}

	@SuppressWarnings("unused")
	private List<String> getToString() {
		return getToString(clsMap);
	}

	@SuppressWarnings("unchecked")
	private List<String> getToString(Map<String, Object> clsMap) {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(TO_STRING_KEY)) {
				return (List<String>) entry.getValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private List<String> getHashCode() {
		return getHashCode(clsMap);
	}

	@SuppressWarnings("unchecked")
	private List<String> getHashCode(Map<String, Object> clsMap) {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(HASH_KEY)) {
				return (List<String>) entry.getValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private CtClass getDefinedAnnotation(String nameSource) {
		Objects.requireNonNull(nameSource, "class name should not be null or empty.");
		return codeModel.makeInterface(nameSource);
	}

	private void loadConstructors(JDefinedClass mdlCls, List<Map<String, Object>> constructors) {
		if (constructors == null) {
			return;
		}
		for (Map<String, Object> constructor : constructors) {
			this.addConstructor(mdlCls, constructor);
		}
	}

	private void addConstructor(JDefinedClass mdlCls, Map<String, Object> constructorMap) {
		/*int mod = AccessorFactory.getFactory().getAccessModifier((String) constructorMap.get(MODIFIER_KEY));
		JMethod constructor = mdlCls.constructor(mod);
		List<Map<String, Object>> paramMap = getParams(constructorMap);
		loadParams(constructor, getParams(constructorMap));
		JBlock block = constructor.body();
		if (mdlCls.getClassType() == ENUM) {
			for (Map<String, Object> fieldMap : paramMap) {
				String name = (String) fieldMap.get(ARG_KEY);
				addClassField(mdlCls, name, fieldMap);
				block.assign(JExpr._this().ref(name), JExpr.ref(name));
			}
		}*/
	}

	private String getTypeSource() {
		return getTypeSource(clsMap);
	}

	@Override
	public void build() {
		try {
			codeModel.insertClassPath(codebase.getAbsolutePath());
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void build(String codebase) {
		this.build(new File(codebase));
	}

	@Override
	public void build(File codebase) {
		this.codebase = codebase;
		this.build();
	}

	private JDefinedClass getDefinedEnum(String enm) {
		/*Objects.requireNonNull(enm, "class name should not be null or empty.");
		try {
			return codeModel.makeClass(enm, ENUM);
		} catch (JClassAlreadyExistsException e) {
			logger.log(Level.WARNING, "ClassAlreadyExists", e);
		}*/
		return null;
	}

	private CtClass getDefinedInterface(String cls) {
		Objects.requireNonNull(cls, "class name should not be null or empty.");
		return codeModel.makeInterface(cls);
	}

	private CtClass getDefinedClass(String cls) {
		Objects.requireNonNull(cls, "class name should not be null or empty.");
		CtClass ctClass=codeModel.makeClass(cls);
		ctClass.defrost();
		return ctClass;
	}

	private void loadExtendClass(CtClass mdlCls, String extend) {
		if (extend == null) {
			return;
		}
		try {
			mdlCls.setSuperclass(codeModel.get(extend));
			Map<String, Object> fieldMap = new HashMap<>();
			fieldMap.put("access", "PUBLIC_NO_STATIC_FINAL");
			fieldMap.put("value", "1L");
			fieldMap.put("type", Long.class.getName());
			addClassField(mdlCls, "serialVersionUID", fieldMap);
		} catch (Exception e) {
			logger.log(Level.WARNING, "ClassNotFound", e);
		}
	}

	private void loadInterfaces(JDefinedClass mdlCls, List<String> implnts) {
		/*if (implnts == null) {
			return;
		}
		for (String cls : implnts) {
			mdlCls._implements(forClass(cls));
			Map<String, Object> fieldMap = new HashMap<>();
			fieldMap.put("access", "PUBLIC_NO_STATIC_FINAL");
			fieldMap.put("value", "1");
			fieldMap.put("type", long.class.getName());
			addClassField(mdlCls, "serialVersionUID", fieldMap);
		}*/
	}

	private void loadMethods(JDefinedClass mdlCls, Map<String, Map<String, Object>> methodMap) {
		if (methodMap == null) {
			return;
		}
		for (Entry<String, Map<String, Object>> entry : methodMap.entrySet()) {
			this.addMethod(mdlCls, entry.getKey(), entry.getValue());
		}
	}

	public Map<String, Map<String, Object>> getMethodResourceList() {
		return getMethodSource(clsMap);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Object>> getMethodSource(Map<String, Object> clsMap) {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(LOGIC_KEY)) {
				return (Map<String, Map<String, Object>>) entry.getValue();
			}
		}
		return null;
	}

	private Class<?> forClass(String cls) {
		try {
			return Class.forName(cls);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public String getNameSource() {
		return getNameSource(clsMap);
	}

	public String getNameSource(Map<String, Object> clsMap) {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(NAME_KEY)) {
				return (String) entry.getValue();
			}
		}
		return null;
	}

	public String getTypeSource(Map<String, Object> clsMap) {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(TYPE_KEY)) {
				return (String) entry.getValue();
			}
		}
		return null;
	}

	public String getExtends() {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(EXTEND_KEY)) {
				return (String) entry.getValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getImplements() {
		for (Entry<String, Object> entry : clsMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(IMPLS_KEY)) {
				if (entry.getValue() instanceof String) {
					return Arrays.asList((String) entry.getValue());
				}
				return (List<String>) entry.getValue();
			}
		}
		return Collections.emptyList();
	}

	public List<Map<String, Object>> getAnnotations() {
		return getAnnotations(clsMap);
	}

	public List<Map<String, Object>> getConstructors() {
		return getConstructors(clsMap);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getConstructors(Map<String, Object> source) {
		for (Entry<String, Object> entry : source.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(CONSTR_KEY)) {
				return (List<Map<String, Object>>) entry.getValue();
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAnnotations(Map<String, Object> source) {
		for (Entry<String, Object> entry : source.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(ANNO_KEY)) {
				return (List<Map<String, Object>>) entry.getValue();
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getParams(Map<String, Object> source) {
		for (Entry<String, ?> entry : source.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(PARAM_KEY)) {
				return (List<Map<String, Object>>) entry.getValue();
			}
		}
		return Collections.emptyList();
	}

	public Map<String, Map<String, Object>> getProperties() {
		return getProperties(clsMap);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getProperties(Map<String, Object> source) {
		for (Entry<String, Object> entry : source.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(FIELD_KEY)) {
				return (Map<String, Map<String, Object>>) entry.getValue();
			}
		}
		return null;
	}

	public void loadProperties(JDefinedClass mdlCls, Map<String, Map<String, Object>> properties) {
		/*if (properties == null) {
			return;
		}
		for (Entry<String, Map<String, Object>> field : properties.entrySet()) {
			if (mdlCls.getClassType() == CLASS) {
				this.addClassField(mdlCls, field.getKey(), field.getValue());
			}
			if (mdlCls.getClassType() == ENUM) {
				this.addEnumField(mdlCls, field.getKey(), field.getValue());
			}
			if (mdlCls.getClassType() == INTERFACE) {
				this.addClassField(mdlCls, field.getKey(), field.getValue());
			}
			if (mdlCls.getClassType() == ANNOTATION_TYPE_DECL) {
				this.addClassField(mdlCls, field.getKey(), field.getValue());
			}
		}*/
	}

	@SuppressWarnings("unchecked")
	private void addEnumField(JDefinedClass mdlCls, String key, Map<String, Object> properties) {
		JEnumConstant enumConstant = mdlCls.enumConstant(key);
		List<Map<String, Object>> args = (List<Map<String, Object>>) properties.get(ARG_KEY);
		for (Map<String, Object> arg : args) {
			Class<?> type = DefaultTypeFactory.getFactory().getTypeMapper((String) arg.get(TYPE_KEY));
			if (type.equals(String.class)) {
				enumConstant.arg(JExpr.lit((String) arg.get("value")));
			}
			if (type.equals(Integer.class) || type.equals(int.class)) {
				enumConstant.arg(JExpr.lit(Integer.valueOf(String.valueOf(arg.get("value")))));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public CtField addClassField(CtClass mdlCls, String field, Map<String, Object> fieldMap) {
		int mod = AccessorFactory.getFactory().getAccessModifier((String) fieldMap.get(MODIFIER_KEY));
		Class<?> type = DefaultTypeFactory.getFactory().getTypeMapper((String) fieldMap.get(TYPE_KEY));
		CtField mdlField;
		try {
			mdlField = new CtField(codeModel.get(type.getName()), field, mdlCls);
		} catch (CannotCompileException | NotFoundException e) {
			e.printStackTrace();
		}
		String velue = (String) fieldMap.get("value");
		/*if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type))
			mdlField.assign(mdlField.init(JExpr.lit(Long.valueOf(velue))));
		loadAnnotations(mdlField, getAnnotations(fieldMap));
		Map<String, Object> setter = (Map<String, Object>) fieldMap.get(SETTER_KEY);
		if (setter != null) {
			addSetter(mdlCls, mdlField, type, setter);
		}
		Map<String, Object> getter = (Map<String, Object>) fieldMap.get(GETTER_KEY);
		if (getter != null) {
			addGetter(mdlCls, mdlField, type, getter);
		}*/
		return null;
	}

	@SuppressWarnings("unused")
	private void addSetter(JDefinedClass mdlCls, JFieldVar mdlField, Class<?> type, Map<String, Object> setterMap) {
		Map<String, Object> paramMap = new HashMap<>();
		Class<?> paramType = DefaultTypeFactory.getFactory().getTypeMapper(
				setterMap.get(TYPE_KEY) == null ? type.getSimpleName() : (String) setterMap.get(TYPE_KEY));
		if (!DefaultTypeFactory.getFactory().isAssignable(type, paramType)) {
			String msg = paramType + " type mismatched : " + " for " + mdlField.name() + " " + type;
			logger.log(Level.SEVERE, msg);
			return;
		}
		paramMap.put(TYPE_KEY, setterMap.get(TYPE_KEY) == null ? type.getSimpleName() : setterMap.get(TYPE_KEY));
		paramMap.put(ARG_KEY, setterMap.get(ARG_KEY) == null ? mdlField.name() : setterMap.get(ARG_KEY));
		JMethod mdlMethod = mdlCls.method(JMod.PUBLIC, mdlCls.owner().VOID, EventValidateUtil.setter(mdlField.name()));
		addParam(mdlMethod, paramMap);
		loadAnnotations(mdlMethod, getAnnotations(setterMap));
		loadParams(mdlMethod, getParams(setterMap));
		mdlMethod.body().assign(JExpr._this().ref(mdlField.name()),
				JExpr.ref(setterMap.get(ARG_KEY) == null ? mdlField.name() : (String) setterMap.get(ARG_KEY)));
	}

	@SuppressWarnings("unused")
	private void addGetter(JDefinedClass mdlCls, JFieldVar mdlField, Class<?> type, Map<String, Object> getterMap) {
		Class<?> paramType = DefaultTypeFactory.getFactory().getTypeMapper(
				getterMap.get(TYPE_KEY) == null ? type.getSimpleName() : (String) getterMap.get(TYPE_KEY));
		if (!DefaultTypeFactory.getFactory().isAssignable(type, paramType)) {
			String msg = paramType + " type mismatched : " + " for " + mdlField.name() + " " + type;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JMethod mdlMethod = mdlCls.method(JMod.PUBLIC, mdlField.type(), EventValidateUtil.getter(mdlField.name()));
		loadAnnotations(mdlMethod, getAnnotations(getterMap));
		loadParams(mdlMethod, getParams(getterMap));
		mdlMethod.body()._return(mdlField);
	}

	public JMethod addMethod(JDefinedClass mdlCls, String method, Map<String, Object> methodMap) {
		int mod = AccessorFactory.getFactory().getAccessModifier((String) methodMap.get(MODIFIER_KEY));
		Class<?> type = DefaultTypeFactory.getFactory().getTypeMapper((String) methodMap.get(TYPE_KEY));
		JMethod mdlMethod = type == null ? mdlCls.method(mod, mdlCls.owner().VOID, method)
				: mdlCls.method(mod, type, method);
		loadAnnotations(mdlMethod, getAnnotations(methodMap));
		loadParams(mdlMethod, getParams(methodMap));
		mdlMethod.body();
		if (type != null) {
			mdlMethod.body()._return(JExpr.ref(type.getTypeName()));
		}
		return mdlMethod;
	}

	private void loadParams(JMethod mdlMethod, List<Map<String, Object>> paramMap) {
		if (paramMap == null) {
			return;
		}
		for (Map<String, Object> param : paramMap) {
			this.addParam(mdlMethod, param);
		}
	}

	private JVar addParam(JMethod mdlMethod, Map<String, Object> paramMap) {
		String name = (String) paramMap.get(ARG_KEY);
		Class<?> type = DefaultTypeFactory.getFactory().getTypeMapper((String) paramMap.get(TYPE_KEY));
		return mdlMethod.param(type, name);
	}

	public void loadAnnotations(JMethod mdlMethod, List<Map<String, Object>> annotationMap) {
		if (annotationMap == null) {
			return;
		}
		for (Map<String, Object> annotation : annotationMap) {
			this.addAnnotation(mdlMethod, (String) annotation.get(TYPE_KEY), annotation);
		}
	}

	public void loadAnnotations(CtField mdlField, List<Map<String, Object>> annotationMap) {
		if (annotationMap == null) {
			return;
		}
		for (Map<String, Object> annotation : annotationMap) {
			this.addAnnotation(mdlField, (String) annotation.get(TYPE_KEY), annotation);
		}
	}

	public void loadAnnotations(JDefinedClass mdlCls, List<Map<String, Object>> annotationMap) {
		if (annotationMap == null) {
			return;
		}
		for (Map<String, Object> annotation : annotationMap) {
			this.addAnnotation(mdlCls, (String) annotation.get(TYPE_KEY), annotation);
		}
	}

	public void addAnnotation(JDefinedClass mdlCls, String annotation, Map<String, Object> annotationMap) {
		Class<? extends Annotation> type = AnnotationFactory.getFactory().getAnnotation(annotation);
		if (type == null) {
			String msg = mdlCls.name() + " -> Unable to add annotation on class for " + annotation;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JAnnotationUse annotationUse = mdlCls.annotate(type);
		buildAnnotation(annotationUse, type, annotationMap);
	}

	public void addAnnotation(CtField mdlField, String annotation, Map<String, Object> annotationMap) {
		/*Class<? extends Annotation> type = AnnotationFactory.getFactory().getAnnotation(annotation);
		if (type == null) {
			String msg = mdlField.getName() + " -> Unable to add annotation on field for " + annotation;
			logger.log(Level.SEVERE, msg);
			return;
		}
		buildAnnotation(annotationUse, type, annotationMap);*/
	}

	public void addAnnotation(JMethod mdlField, String annotation, Map<String, Object> annotationMap) {
		Class<? extends Annotation> type = AnnotationFactory.getFactory().getAnnotation(annotation);
		if (type == null) {
			String msg = mdlField.name() + " -> Unable to add annotation on method for " + annotation;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JAnnotationUse annotationUse = mdlField.annotate(type);
		buildAnnotation(annotationUse, type, annotationMap);
	}

	@SuppressWarnings("unchecked")
	private void buildAnnotation(JAnnotationUse annotationUse, Class<? extends Annotation> annoType,
			Map<String, Object> annotationMap) {
		Map<String, Object> params = (Map<String, Object>) annotationMap.get(FIELD_KEY);
		if (params != null) {
			params.forEach((key, value) -> {
				try {
					value = CastingUtil.castObject(value, annoType.getDeclaredMethod(key).getReturnType());
					if (value instanceof Enum<?>) {
						annotationUse.param(key, (Enum<?>) value);
					} else if (value instanceof String) {
						annotationUse.param(key, (String) value);
					} else if (value instanceof Integer) {
						annotationUse.param(key, (Integer) value);
					} else if (value instanceof Float) {
						annotationUse.param(key, (Float) value);
					} else if (value instanceof Double) {
						annotationUse.param(key, (Double) value);
					} else if (value.getClass().isArray()) {
						Object[] paramArray = (Object[]) value;
						this.addParamArray(annotationUse, key, paramArray);
					} else if (value instanceof List) {
						Object[] paramArray = ((List<?>) value).toArray();
						this.addParamArray(annotationUse, key, paramArray);
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage());
				}
			});
		}
	}

	private void addParamArray(JAnnotationUse annotationUse, String key, Object[] paramArray) {
		JAnnotationArrayMember arrayMember = annotationUse.paramArray(key);
		for (Object paramValue : paramArray) {
			paramValue = CastingUtil.castObject(paramValue, String.class);
			if (paramValue instanceof String) {
				arrayMember.param((String) paramValue);
			} else if (paramValue instanceof Integer) {
				arrayMember.param((Integer) paramValue);
			}
		}
	}

	@SuppressWarnings("unused")
	private void addToString(JDefinedClass definedClass, List<String> fieldList) {
		if (fieldList == null) {
			return;
		}
		Map<String, JFieldVar> fields = definedClass.fields();
		List<JFieldVar> fieldVars = new ArrayList<>();
		for (JFieldVar fieldVar : fields.values()) {
			if ((fieldVar.mods().getValue() & JMod.STATIC) == JMod.STATIC) {
				continue;
			}
			if (fieldList.contains(fieldVar.name())) {
				fieldVars.add(fieldVar);
			}
		}
		if (!fieldVars.isEmpty()) {
			JMethod toString = definedClass.method(JMod.PUBLIC, String.class, "toString");
			toString.annotate(Override.class);

			JBlock body = toString.body();

			JExpression expression = JExpr.lit(definedClass.name() + " ( ");

			boolean first = true;
			for (JFieldVar fieldVar : fieldVars) {
				if (!first) {
					expression = expression.plus(JExpr.lit(", "));
				}
				expression = expression.plus(JExpr.lit(fieldVar.name() + " = "));
				expression = expression.plus(JExpr.ref(fieldVar.name()));
				first = false;
			}
			expression = expression.plus(JExpr.lit(" ) "));

			body._return(expression);
		}
	}

	private void addHashCode(JDefinedClass jclass, List<String> fieldList) {
		if (fieldList == null) {
			return;
		}
		Map<String, JFieldVar> fields = jclass.fields();
		List<JFieldVar> fieldVars = new ArrayList<>();
		for (JFieldVar fieldVar : fields.values()) {
			if ((fieldVar.mods().getValue() & JMod.STATIC) == JMod.STATIC) {
				continue;
			}
			if (fieldList.contains(fieldVar.name())) {
				fieldVars.add(fieldVar);
			}
		}
		if (!fieldVars.isEmpty()) {
			JMethod hashCode = jclass.method(JMod.PUBLIC, int.class, "hashCode");
			JBlock body = hashCode.body();
			JClass hashCodeBuilderClass = jclass.owner().ref(Objects.class);
			JInvocation hashCodeBuilderInvocation = hashCodeBuilderClass.staticInvoke("hash");
			for (JFieldVar fieldVar : fieldVars) {
				hashCodeBuilderInvocation.arg(fieldVar);
			}
			body._return(hashCodeBuilderInvocation);
			hashCode.annotate(Override.class);
		}
	}

}
