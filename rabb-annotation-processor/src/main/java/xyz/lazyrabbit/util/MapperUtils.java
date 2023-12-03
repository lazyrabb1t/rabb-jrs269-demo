package xyz.lazyrabbit.util;

public class MapperUtils {
    public static <T> T getMapper(Class<T> clazz) {
        try {
            String className = clazz.getName() + "Impl";
            return (T) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
