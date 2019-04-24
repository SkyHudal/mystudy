package com.spring.jdbc.mapper;

import com.spring.jdbc.constants.Constant;
import com.spring.jdbc.utils.SkyUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyRowMap<T> implements RowMapper<T> {

    private String resultType;

    public SkyRowMap(String resultType) {
        this.resultType = resultType;
    }

    @Override
    public T mapRow(ResultSet resultSet, int i) throws SQLException {
        Map<String, Integer> columnNameMapperIndex = columnNameMapperIndex(resultSet);
        Class<T> clazz;
        T t;
        try {
            clazz = (Class<T>) Class.forName(resultType);
            t = clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("resultType is error", e.getCause());
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (!columnNameMapperIndex.containsKey(field.getName()))
                continue;
            Object value = JdbcUtils.getResultSetValue(resultSet, columnNameMapperIndex.get(field.getName()), type);
            if (value == null || "".equals(value))
                continue;
            try {
                field.set(t, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("set field value failed", e.getCause());
            }
        }
        return t;
    }

    private Map<String, Integer> columnNameMapperIndex(ResultSet rs) {
        Map<String, Integer> result = new HashMap<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnLabel = metaData.getColumnLabel(i).toLowerCase(Locale.US);
                String columnName = metaData.getColumnName(i).toLowerCase(Locale.US);
                StringBuilder fieldName = new StringBuilder(columnLabel);
                if (columnName.equalsIgnoreCase(columnLabel)) {
                    Pattern compile = Pattern.compile(Constant.Regular.COLUMN_NAME);
                    Matcher matcher = compile.matcher(columnLabel);
                    while (matcher.find()) {
                        String group = matcher.group();
                        char[] chars = group.toCharArray();
                        chars[1] += 32;
                        fieldName = SkyUtils.replace(fieldName, group, chars[1]);
                    }
                }
                result.put(fieldName.toString(), i);
            }
        } catch (SQLException e) {
            throw new RuntimeException("dataBase ResultSet exception", e.getCause());
        }
        return result;
    }
}
