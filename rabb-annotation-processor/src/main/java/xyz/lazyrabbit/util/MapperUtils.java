package xyz.lazyrabbit.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapperUtils {

    public static <T> T getMapper(Class<T> clazz) {
        try {
            String className = clazz.getName() + "Impl";
            return (T) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1);
    }

    public static String captureFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    public static String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public static void main(String[] args) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap();
        data.put("classPath", "xyz.lazyrabbit.struct");
        data.put("className", "UserStruct");
        List<String> importList = Arrays.asList("xyz.lazyrabbit.pojo.domain.UserDO", "xyz.lazyrabbit.pojo.domain.UserVO");
        data.put("importList", importList);
        MethodDTO methodDTO = new MethodDTO();
        methodDTO.setMethodName("to");
        methodDTO.setReturnType("UserVO");
        methodDTO.setReturnFieldList(Arrays.asList("name", "age"));
        methodDTO.setVariableType("UserDO");
        methodDTO.setVariableName("userDO");
        data.put("methodList", Arrays.asList(methodDTO));
        System.out.println(data.toString());
        System.out.println(generator(data));
    }

    public static String generator(Map<String, Object> data) {
        try (StringWriter stringWriter = new StringWriter();) {
            Configuration conf = new Configuration(Configuration.VERSION_2_3_31);
            conf.setDirectoryForTemplateLoading(new File(MapperUtils.class.getClassLoader().getResource("template").getPath()));
            conf.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
            Template temp = conf.getTemplate("struct.java.ftl");
            temp.process(data, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

}
