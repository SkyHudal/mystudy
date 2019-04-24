package com.spring.jdbc.parameter;

import java.util.Map;

public class SqlParamWrapper {

    //预编译的sql，条件都是？
    private String sql;

    private Object[] params;

    public SqlParamWrapper(String sql, Map<Integer, Object> paramIndex) {
        this.sql = sql;
        if (paramIndex != null) {
            params = new Object[paramIndex.size()];
            paramIndex.forEach((index, value) -> params[index] = value);
        }
    }

    public String getSql() {
        return sql;
    }

    public Object[] getParams() {
        return params;
    }
}
