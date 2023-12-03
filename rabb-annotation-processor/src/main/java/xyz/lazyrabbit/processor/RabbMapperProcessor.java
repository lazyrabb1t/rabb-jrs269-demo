package xyz.lazyrabbit.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
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
                try {
                    generateClass(element);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }

    private void generateClass(Element element) throws ClassNotFoundException {
        if (element.getKind() == ElementKind.INTERFACE) {

            Set<String> importClassSet = new HashSet<>();
            StringBuilder methodCode = new StringBuilder();
            String className = element.getSimpleName().toString();
            String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
            System.err.println("当前class：" + packageName + "." + className);


            List<? extends Element> enclosedElements = element.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                if (enclosedElement.getKind() == ElementKind.METHOD) {
                    System.err.println("当前method：" + enclosedElement.getSimpleName().toString());
                    ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                    TypeMirror returnType = executableElement.getReturnType();
                    importClassSet.add(returnType.toString());
                    methodCode.append("    public ")
                            .append(getLastName(returnType.toString())).append(" ")
                            .append(enclosedElement.getSimpleName().toString())
                            .append("(");
                    List<? extends VariableElement> parameters = executableElement.getParameters();
                    String variableName = null;
                    for (VariableElement parameter : parameters) {
                        String variableType = parameter.asType().toString();
                        variableName = parameter.getSimpleName().toString();
                        System.err.println("当前参数类型：" + variableType);
                        importClassSet.add(variableType);
//                                System.err.println(parameter.getEnclosingElement().getEnclosingElement().toString());
                        System.err.println("当前参数名称：" + variableName);
                        methodCode.append(getLastName(variableType)).append(" ")
                                .append(parameter.getSimpleName().toString());
                    }
                    methodCode.append(") {\n");
                    String returnClassName = getLastName(returnType.toString());

                    methodCode.append("        ")
                            .append(returnClassName).append(" ").append(lowerFirst(returnClassName))
                            .append(" = new ").append(returnClassName).append("();\n");
                    DeclaredType declaredType = (DeclaredType) returnType;
                    Element returnElement = declaredType.asElement();
                    List<? extends Element> returnElementEnclosedElements = returnElement.getEnclosedElements();
                    for (Element returnElementEnclosedElement : returnElementEnclosedElements) {
                        if (ElementKind.FIELD == returnElementEnclosedElement.getKind()) {
                            String fieldName = captureFirst(returnElementEnclosedElement.getSimpleName().toString());
                            methodCode.append("        ")
                                    .append(lowerFirst(returnClassName)).append(".set").append(fieldName).append("(")
                                    .append(variableName).append(".get").append(fieldName).append("());\n");
//                                    .append(variableName).append(".get").append(fieldName).append("() + \"（set by @RabbMapper）\");\n");
                        }

                    }
                    methodCode.append("        ")
                            .append("return ").append(lowerFirst(returnClassName)).append(";\n");
                    methodCode.append("    }\n");
                }
            }
            StringBuilder classCode = new StringBuilder();
            classCode.append("package ").append(packageName).append(";\n\n");
            for (String clazz : importClassSet) {
                classCode.append("import ").append(clazz).append(";\n");
            }
            classCode.append("\n");
            classCode.append("public class ").append(className).append("Impl")
                    .append(" implements ").append(className).append(" {\n\n");
            classCode.append(methodCode);
            classCode.append("}\n");
            System.err.println(classCode);
            try {
                JavaFileObject serializerFile = processingEnv.getFiler().createSourceFile(packageName + "." + className + "Impl");
                try (Writer writer = serializerFile.openWriter()) {
                    writer.write(classCode.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String getLastName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1);
    }

    private String captureFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    private String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
