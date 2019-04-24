package com.spring.jdbc.core;

import com.spring.jdbc.constants.Constant;
import com.spring.jdbc.mapper.SkyRowMap;
import com.spring.jdbc.parameter.SqlParamWrapper;
import com.spring.jdbc.support.ParseDBConfig;
import com.spring.jdbc.utils.SkyUtils;
import com.spring.jdbc.xml.ParseSqlXml;
import com.spring.jdbc.xml.SqlWrapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyTemplate extends JdbcTemplate {

    //提供两种方式进行dataSource的配置
    //这种是指定dataSource的配置文件路径
    private String dataSourcePath;

    //这种是直接注入指定的dataSource对象
    private DataSource skyDataSource;

    public SkyTemplate(String dataSourcePath) {
        setDataSourcePath(dataSourcePath);
    }

    //得到自己编写的所有的sql
    private ParseSqlXml parseSqlXml = new ParseSqlXml();

    public void setSkyDataSource(DataSource skyDataSource) {
        super.setDataSource(skyDataSource);
    }

    public void setDataSourcePath(String dataSourcePath) {
        super.setDataSource(ParseDBConfig.getDataSource(dataSourcePath));
    }

    public <T> List<T> findList(String sqlLabel, Object params) {
        SqlWrapper sqlWrapper = getSqlWrapper(sqlLabel);
        dynamicSqlParse(sqlWrapper, params);
        return this.query(sqlWrapper.getSqlText(), new SkyRowMap<>(sqlWrapper.getResultType()),
                sqlWrapper.getParams());
    }

    private SqlWrapper getSqlWrapper(String sqlLabel) {
        Map<String, SqlWrapper> sqlWrapperMap = parseSqlXml.getSqlWrapperMap();
        if (!sqlWrapperMap.containsKey(sqlLabel)) {
            throw new RuntimeException("not found this sql: " + sqlLabel);
        }
        return sqlWrapperMap.get(sqlLabel);
    }

    /**
     * 根据传入的参数，动态的对sql进行解析
     */
    private void dynamicSqlParse(SqlWrapper sqlWrapper, Object params) {
        String sqlText = sqlWrapper.getSqlText();
        Map<String, Object> paramMap = SkyUtils.beanToMap(params);
        //得到的结果为select * from person where id = ? 或 select * from person where
        SqlParamWrapper sqlParamWrapper = removeNullParameters(sqlText, paramMap);
        sqlWrapper.setSqlText(sqlParamWrapper.getSql());
        sqlWrapper.setParams(sqlParamWrapper.getParams());
    }

    //暂时只考虑查询
    private SqlParamWrapper removeNullParameters(String sqlText, Map<String, Object> paramMap) {
        StringBuilder result = new StringBuilder(sqlText);
        Pattern compile = Pattern.compile(Constant.Regular.DYNAMIC_PARAMETER);
        Matcher matcher = compile.matcher(sqlText);
        int index = -1;
        Map<Integer, Object> paramIndex = new HashMap<>();
        if (matcher.find()) {
            //匹配到<[ and id = :id ]>
            String dynamicParam = matcher.group();
            //匹配到 and id = :id
            String param = dynamicParam.substring(2, dynamicParam.length() - 2).trim();
            Pattern c = Pattern.compile(Constant.Regular.PARAMETER_NAME);
            Matcher m = c.matcher(param);
            boolean flag = true;
            while (m.find()) {
                //匹配到id
                String paramName = m.group().substring(1).trim();
                if (!paramMap.containsKey(paramName)) {
                    flag = false;
                }
            }
            if (flag) {
                result = SkyUtils.replace(result, dynamicParam, param);
            } else {
                result = SkyUtils.replace(result, dynamicParam, "");
            }
            validRequiredParameters(result.toString(), paramMap);
            Matcher m1 = c.matcher(result);
            while (m1.find()) {
                String group = m1.group();
                index++;
                paramIndex.put(index, paramMap.get(group.substring(1).trim()));
                result = SkyUtils.replace(result, group, "?");
            }
        }
        return new SqlParamWrapper(result.toString(), paramIndex);
    }

    //暂时只考虑查询
    private void validRequiredParameters(String sqlText, Map<String, Object> paramMap) {
        Pattern c = Pattern.compile(Constant.Regular.PARAMETER_NAME);
        Matcher m = c.matcher(sqlText.substring(sqlText.indexOf("where")));
        while (m.find()) {
            //匹配到id
            String paramName = m.group().substring(1).trim();
            if (!paramMap.containsKey(paramName)) {
                throw new RuntimeException("Required parameter is null");
            }
        }
    }
}
