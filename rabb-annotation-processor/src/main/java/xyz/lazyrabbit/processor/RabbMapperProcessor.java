package xyz.lazyrabbit.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

// 指定我们这个注解处理器能够处理的注解，写我们想要处理的注解
@SupportedAnnotationTypes({"xyz.lazyrabbit.annotation.RabbMapper"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class RabbMapperProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.err.println("RabbMapperProcessor");
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind() == ElementKind.INTERFACE) {
                    String className = element.getSimpleName().toString();
                    String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
                    generateSerializerClass(packageName, className);
                }
            }
        }

        return true;
    }

    private void generateSerializerClass(String packageName, String className) {
        String serializerClassName = className + "Serializer";

        StringBuilder serializerClassCode = new StringBuilder();
        serializerClassCode.append("package ").append(packageName).append(";\n\n");
        serializerClassCode.append("import java.io.Serializable;\n");
        serializerClassCode.append("import java.io.ObjectOutputStream;\n");
        serializerClassCode.append("import java.io.IOException;\n\n");
        serializerClassCode.append("public class ").append(serializerClassName)
                .append(" implements Serializable {\n\n");
        serializerClassCode.append(" private static final long serialVersionUID = 1L;\n\n");
        serializerClassCode.append(" public static void serialize(").append(className)
                .append(" obj, ObjectOutputStream out) throws IOException {\n");
        serializerClassCode.append(" out.writeObject(obj);\n");
        serializerClassCode.append(" }\n");

        serializerClassCode.append("}\n");

        try {
            JavaFileObject serializerFile = processingEnv.getFiler().createSourceFile(packageName + "." + serializerClassName);
            try (Writer writer = serializerFile.openWriter()) {
                writer.write(serializerClassCode.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
