package xyz.lazyrabbit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import xyz.lazyrabbit.annotation.RabbMapper;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// 指定我们这个注解处理器能够处理的注解，写我们想要处理的注解
@SupportedAnnotationTypes({"xyz.lazyrabbit.annotation.RabbMapper"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class RabbMapperProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.err.println("RabbMapperProcessor");
        if (annotations.isEmpty()) {
            return false;
        }
        parseElement(roundEnv, RabbMapper.class);
        return true;
    }

    private void parseElement(RoundEnvironment roundEnvironment, Class<? extends Annotation> annotationClass) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotationClass);
        for (Element element : elements) {
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String className = element.getSimpleName().toString();
            ClassName elementClass = ClassName.get(packageName, className);
            mapperProcess(element, packageName, className, elementClass);
        }
    }

    private void mapperProcess(Element element, String packageName, String className, ClassName elementClass) {
        RabbMapper rabbMapper = element.getAnnotation(RabbMapper.class);

        // mapper 类名
        String name = className + "Mapper";

        TypeMirror baseMapperTypeMirror = readValue(element, RabbMapper.class, "baseMapper");


        // 处理父类BaseMapper
        ParameterizedTypeName baseMapperType = null;
        if (baseMapperTypeMirror != null && !Objects.equals(baseMapperTypeMirror.toString(), Void.class.getName())) {
            baseMapperType = ParameterizedTypeName.get((ClassName) ClassName.get(baseMapperTypeMirror), elementClass);
        }

        // 构建需要生成的java类
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);

        if (baseMapperType != null) {
            builder.addSuperinterface(baseMapperType);
        }
        JavaFile javaFile = JavaFile.builder(packageName + ".mapper", builder.build()).build();
        System.err.printf("", javaFile.toString());
        try {
            // 生成类文件
            javaFile.writeTo(filer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AnnotationMirror getEventTypeAnnotationMirror(Element element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (Objects.equals(annotationMirror.getAnnotationType().toString(), clazzName)) {
                return annotationMirror;
            }
        }
        return null;
    }

    private AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (Objects.equals(key, entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private <T> T readValue(Element element, Class<?> clazz, String key) {
        AnnotationMirror am = getEventTypeAnnotationMirror(element, clazz);
        AnnotationValue av = null;
        if (am != null) {
            av = getAnnotationValue(am, key);
        }
        return av == null ? null : (T) av.getValue();
    }

}
