package com.spring.jdbc.xml;

public class SqlWrapper {

    //sql唯一标识sqlId
    private String sqlLabel;

    //返回值类型
    private String resultType;

    //真正的sql内容
    private String sqlText;

    private Object[] params;

    public SqlWrapper(String sqlLabel, String resultType, String sqlText) {
        this.sqlLabel = sqlLabel;
        this.resultType = resultType;
        this.sqlText = sqlText;
    }

    public String getSqlLabel() {
        return sqlLabel;
    }

    public void setSqlLabel(String sqlLabel) {
        this.sqlLabel = sqlLabel;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
