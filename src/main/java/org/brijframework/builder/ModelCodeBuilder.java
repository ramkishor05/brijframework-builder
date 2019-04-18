package org.brijframework.builder;

import static com.sun.codemodel.ClassType.CLASS;
import static com.sun.codemodel.ClassType.ENUM;
import static org.brijframework.builder.util.BuilderConstants.ANNO_KEY;
import static org.brijframework.builder.util.BuilderConstants.ARG_KEY;
import static org.brijframework.builder.util.BuilderConstants.CONSTR_KEY;
import static org.brijframework.builder.util.BuilderConstants.EXTEND_KEY;
import static org.brijframework.builder.util.BuilderConstants.FIELD_KEY;
import static org.brijframework.builder.util.BuilderConstants.GETTER_KEY;
import static org.brijframework.builder.util.BuilderConstants.IMPLS_KEY;
import static org.brijframework.builder.util.BuilderConstants.LOGIC_KEY;
import static org.brijframework.builder.util.BuilderConstants.MODIFIER_KEY;
import static org.brijframework.builder.util.BuilderConstants.NAME_KEY;
import static org.brijframework.builder.util.BuilderConstants.PARAM_KEY;
import static org.brijframework.builder.util.BuilderConstants.SETTER_KEY;
import static org.brijframework.builder.util.BuilderConstants.TYPE_KEY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.brijframework.builder.factories.AnnotationMapperFactory;
import org.brijframework.builder.factories.ModifierMapperFactory;
import org.brijframework.builder.factories.TypeMapperFactory;
import org.brijframework.builder.util.ModelCodeMapper;
import org.brijframework.util.resouces.JSONUtil;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ModelCodeBuilder implements CodeBuilder {

	private static Logger logger = Logger.getLogger(ModelCodeBuilder.class.getName());
	private File sourcePath;

	private Map<String, Object> clsMap = new HashMap<>();
	private File codebase;
	private JCodeModel codeModel = new JCodeModel();

	public ModelCodeBuilder() {
		this.codebase = new File("src/main/java/");
		if (!codebase.exists()) {
			this.codebase = new File("src/");
		}
		if (!codebase.exists()) {
			this.codebase = new File("/");
		}
	}

	public ModelCodeBuilder(String sourcePath) {
		this(new File(sourcePath));
	}

	public ModelCodeBuilder(File sourcePath) {
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
		}
		if ("class".equalsIgnoreCase(type)) {
			JDefinedClass mdlCls = getDefinedClass(getNameSource());
			loadExtendClass(mdlCls, getExtends());
			loadInterfaces(mdlCls, getImplements());
			loadProperties(mdlCls, getProperties());
			loadMethods(mdlCls, getMethodResourceList());
			loadAnnotations(mdlCls, getAnnotations());
			loadConstructors(mdlCls, getConstructors());
		}
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
		int mod = ModifierMapperFactory.getFactory().getModMapper((String) constructorMap.get(MODIFIER_KEY));
		JMethod constructor = mdlCls.constructor(mod);
		List<Map<String, Object>> paramMap = getParams(constructorMap);
		loadParams(constructor, getParams(constructorMap));
		JBlock block = constructor.body();
		if (mdlCls.getClassType() == ENUM) {
			for (Map<String, Object> fieldMap : paramMap) {
				String name = (String) fieldMap.get(ARG_KEY);
				addField(mdlCls, name, fieldMap);
				block.assign(JExpr._this().ref(name), JExpr.ref(name));
			}
		}
	}

	private String getTypeSource() {
		return getTypeSource(clsMap);
	}

	@Override
	public void build() {
		try {
			codeModel.build(codebase);
		} catch (IOException e) {
			logger.log(Level.WARNING, "IOException", e);
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
		Objects.requireNonNull(enm, "class name should not be null or empty.");
		try {
			return codeModel._class(enm, ENUM);
		} catch (JClassAlreadyExistsException e) {
			logger.log(Level.WARNING, "ClassAlreadyExists", e);
		}
		return null;
	}

	private JDefinedClass getDefinedClass(String cls) {
		Objects.requireNonNull(cls, "class name should not be null or empty.");
		try {
			return codeModel._class(cls);
		} catch (JClassAlreadyExistsException e) {
			logger.log(Level.WARNING, "ClassAlreadyExists", e);
			return null;
		}
	}

	private void loadExtendClass(JDefinedClass mdlCls, String extend) {
		if (extend == null) {
			return;
		}
		try {
			mdlCls._extends(Class.forName(extend));
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "ClassNotFound", e);
		}
	}

	private void loadInterfaces(JDefinedClass mdlCls, List<String> implnts) {
		if (implnts == null) {
			return;
		}
		for (String cls : implnts) {
			mdlCls._implements(forClass(cls));
		}
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
		if (properties == null) {
			return;
		}
		for (Entry<String, Map<String, Object>> field : properties.entrySet()) {
			if (mdlCls.getClassType() == CLASS) {
				this.addField(mdlCls, field.getKey(), field.getValue());
			}
			if (mdlCls.getClassType() == ENUM) {
				this.addEnum(mdlCls, field.getKey(), field.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addEnum(JDefinedClass mdlCls, String key, Map<String, Object> properties) {
		JEnumConstant enumConstant = mdlCls.enumConstant(key);
		List<Map<String, Object>> args = (List<Map<String, Object>>) properties.get(ARG_KEY);
		for (Map<String, Object> arg : args) {
			Class<?> type = TypeMapperFactory.getFactory().getTypeMapper((String) arg.get(TYPE_KEY));
			if (type.equals(String.class)) {
				enumConstant.arg(JExpr.lit((String) arg.get("value")));
			}
			if (type.equals(Integer.class) || type.equals(int.class)) {
				enumConstant.arg(JExpr.lit(Integer.valueOf(String.valueOf(arg.get("value")))));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public JFieldVar addField(JDefinedClass mdlCls, String field, Map<String, Object> fieldMap) {
		int mod = ModifierMapperFactory.getFactory().getModMapper((String) fieldMap.get(MODIFIER_KEY));
		Class<?> type = TypeMapperFactory.getFactory().getTypeMapper((String) fieldMap.get(TYPE_KEY));
		JFieldVar mdlField = mdlCls.field(mod, type, field);
		loadAnnotations(mdlField, getAnnotations(fieldMap));
		Map<String, Object> setter = (Map<String, Object>) fieldMap.get(SETTER_KEY);
		if (setter != null) {
			try {
				addSetter(mdlCls, mdlField, type, setter);
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		Map<String, Object> getter = (Map<String, Object>) fieldMap.get(GETTER_KEY);
		if (getter != null) {
			addGetter(mdlCls, mdlField, type, getter);
		}
		return mdlField;
	}

	private void addSetter(JDefinedClass mdlCls, JFieldVar mdlField, Class<?> type, Map<String, Object> setterMap) {
		Map<String, Object> paramMap = new HashMap<>();
		Class<?> paramType = TypeMapperFactory.getFactory().getTypeMapper(setterMap.get(TYPE_KEY) == null ? type.getSimpleName() : (String) setterMap.get(TYPE_KEY));
		if (!TypeMapperFactory.getFactory().isAssignable(type, paramType)) {
			String msg = paramType + " type mismatched : " + " for " + mdlField.name() + " " + type;
			logger.log(Level.SEVERE, msg);
			return;
		}
		paramMap.put(TYPE_KEY, setterMap.get(TYPE_KEY) == null ? type.getSimpleName() : setterMap.get(TYPE_KEY));
		paramMap.put(ARG_KEY, setterMap.get(ARG_KEY) == null ? mdlField.name() : setterMap.get(ARG_KEY));
		JMethod mdlMethod = mdlCls.method(JMod.PUBLIC, mdlCls.owner().VOID, ModelCodeMapper.getSetterKey(mdlField.name()));
		addParam(mdlMethod, paramMap);
		loadAnnotations(mdlMethod, getAnnotations(setterMap));
		loadParams(mdlMethod, getParams(setterMap));
		mdlMethod.body().assign(JExpr._this().ref(mdlField.name()),
				JExpr.ref(setterMap.get(ARG_KEY) == null ? mdlField.name() : (String) setterMap.get(ARG_KEY)));
	}

	private void addGetter(JDefinedClass mdlCls, JFieldVar mdlField, Class<?> type, Map<String, Object> getterMap) {
		Class<?> paramType = TypeMapperFactory.getFactory().getTypeMapper(getterMap.get(TYPE_KEY) == null ? type.getSimpleName() : (String) getterMap.get(TYPE_KEY));
		if (!TypeMapperFactory.getFactory().isAssignable(type, paramType)) {
			String msg = paramType + " type mismatched : " + " for " + mdlField.name() + " " + type;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JMethod mdlMethod = mdlCls.method(JMod.PUBLIC, mdlField.type(), ModelCodeMapper.getGetterKey(type, mdlField.name()));
		loadAnnotations(mdlMethod, getAnnotations(getterMap));
		loadParams(mdlMethod, getParams(getterMap));
		mdlMethod.body()._return(mdlField);
	}

	public JMethod addMethod(JDefinedClass mdlCls, String method, Map<String, Object> methodMap) {
		int mod = ModifierMapperFactory.getFactory().getModMapper((String) methodMap.get(MODIFIER_KEY));
		Class<?> type = TypeMapperFactory.getFactory().getTypeMapper((String) methodMap.get(TYPE_KEY));
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
		Class<?> type = TypeMapperFactory.getFactory().getTypeMapper((String) paramMap.get(TYPE_KEY));
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

	public void loadAnnotations(JFieldVar mdlField, List<Map<String, Object>> annotationMap) {
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
		Class<? extends Annotation> type = AnnotationMapperFactory.getFactory().getAnnotationMapper(annotation);
		if(type==null){
			String msg=mdlCls.name()+" -> Unable to add annotation on class for "+annotation;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JAnnotationUse annotationUse = mdlCls.annotate(type);
		buildAnnotation(annotationUse, type, annotationMap);
	}

	public void addAnnotation(JFieldVar mdlField, String annotation, Map<String, Object> annotationMap) {
		Class<? extends Annotation> type = AnnotationMapperFactory.getFactory().getAnnotationMapper(annotation);
		if(type==null){
			String msg=mdlField.name()+" -> Unable to add annotation on field for "+annotation;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JAnnotationUse annotationUse = mdlField.annotate(type);
		buildAnnotation(annotationUse, type, annotationMap);
	}

	public void addAnnotation(JMethod mdlField, String annotation, Map<String, Object> annotationMap) {
		Class<? extends Annotation> type = AnnotationMapperFactory.getFactory().getAnnotationMapper(annotation);
		if(type==null){
			String msg=mdlField.name()+" -> Unable to add annotation on method for "+annotation;
			logger.log(Level.SEVERE, msg);
			return;
		}
		JAnnotationUse annotationUse = mdlField.annotate(type);
		buildAnnotation(annotationUse, type, annotationMap);
	}

	@SuppressWarnings("unchecked")
	private void buildAnnotation(JAnnotationUse annotationUse, Class<? extends Annotation> annoType,
			Map<String, Object> annotationMap) {
		Map<String, Object> params = (Map<String, Object>) annotationMap.get(PARAM_KEY);
		if (params != null) {
			params.forEach((key, value) -> {
				try {
					value = ModelCodeMapper.castValue(annoType.getDeclaredMethod(key).getReturnType(), value);
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage());
				}
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
			});
		}
	}

	private void addParamArray(JAnnotationUse annotationUse, String key, Object[] paramArray) {
		JAnnotationArrayMember arrayMember = annotationUse.paramArray(key);
		for (Object paramValue : paramArray) {
			paramValue = ModelCodeMapper.castValue(String.class, paramValue);
			if (paramValue instanceof String) {
				arrayMember.param((String) paramValue);
			} else if (paramValue instanceof Integer) {
				arrayMember.param((Integer) paramValue);
			}
		}
	}
}
