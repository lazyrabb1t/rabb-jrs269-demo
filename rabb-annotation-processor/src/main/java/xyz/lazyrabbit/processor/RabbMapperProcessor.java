package xyz.lazyrabbit.processor;

import xyz.lazyrabbit.util.MapperUtils;
import xyz.lazyrabbit.util.MethodDTO;

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
import java.util.*;

// 指定我们这个注解处理器能够处理的注解，写我们想要处理的注解
@SupportedAnnotationTypes({"xyz.lazyrabbit.annotation.RabbMapper"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class RabbMapperProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.err.println("调用RabbMapperProcessor");
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                generateClass(element);
            }
        }
        return true;
    }

    private void generateClass(Element element) {
        if (element.getKind() == ElementKind.INTERFACE) {
            Set<String> importClassSet = new HashSet<>();
            List<MethodDTO> methodDTOList = new ArrayList<>();
            String className = element.getSimpleName().toString();
            String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
            List<? extends Element> enclosedElements = element.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                if (enclosedElement.getKind() == ElementKind.METHOD) {
                    ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                    TypeMirror returnType = executableElement.getReturnType();
                    importClassSet.add(returnType.toString());
                    List<? extends VariableElement> parameters = executableElement.getParameters();
                    String variableName = null;
                    String variableType = null;
                    for (VariableElement parameter : parameters) {
                        variableType = MapperUtils.getLastName(parameter.asType().toString());
                        variableName = parameter.getSimpleName().toString();
                        importClassSet.add(parameter.asType().toString());
                    }
                    String returnClassName = MapperUtils.getLastName(returnType.toString());
                    DeclaredType declaredType = (DeclaredType) returnType;
                    Element returnElement = declaredType.asElement();
                    List<? extends Element> returnElementEnclosedElements = returnElement.getEnclosedElements();
                    List<String> fieldList = new ArrayList<>();
                    for (Element returnElementEnclosedElement : returnElementEnclosedElements) {
                        if (ElementKind.FIELD == returnElementEnclosedElement.getKind()) {
                            String fieldName = MapperUtils.captureFirst(returnElementEnclosedElement.getSimpleName().toString());
                            fieldList.add(fieldName);
                        }
                    }
                    MethodDTO methodDTO = new MethodDTO();
                    methodDTO.setMethodName(enclosedElement.getSimpleName().toString());
                    methodDTO.setReturnType(returnClassName);
                    methodDTO.setReturnFieldList(fieldList);
                    methodDTO.setVariableType(variableType);
                    methodDTO.setVariableName(variableName);
                    methodDTOList.add(methodDTO);
                }
            }
            try {
                Map<String, Object> data = new HashMap();
                data.put("classPath", packageName);
                data.put("className", className);
                data.put("importList", importClassSet);
                data.put("methodList", methodDTOList);
                String result = MapperUtils.generator(data);
                System.err.println(data);
                System.err.println(result);
                JavaFileObject serializerFile = processingEnv.getFiler().
                        createSourceFile(packageName + "." + className + "Impl");
                try (Writer writer = serializerFile.openWriter()) {
                    writer.write(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
