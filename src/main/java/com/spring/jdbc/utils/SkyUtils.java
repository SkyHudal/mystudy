package com.spring.jdbc.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkyUtils {

    /**
     * 将传进来的Object转换成map返回，格式属性名作为key，属性值作为value
     */
    public static Map<String, Object> beanToMap(Object object) {
        if (object == null || object instanceof List || object instanceof Set) {
            return new HashMap<>();
        }
        if (object instanceof Map) {
            return (Map<String, Object>) object;
        }
        Class<?> clazz = object.getClass();
        Map<String, Object> result = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Parameter conversion failed", e.getCause());
            }
            if (value != null && !"".equals(value)) {
                result.put(field.getName(), value);
            }
        }
        return result;
    }

    public static StringBuilder replace(StringBuilder src, String old, String replace) {
        return new StringBuilder(src.toString().replace(old, replace));
    }
    public static StringBuilder replace(StringBuilder src, String old, char replace) {
        return new StringBuilder(src.toString().replace(old,String.valueOf(replace)));
    }
}
