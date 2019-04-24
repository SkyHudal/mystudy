package com.spring.jdbc.xml;

import com.spring.jdbc.constants.Constant;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParseSqlXml {

    private StringBuilder sqlPath = new StringBuilder(Constant.SQL_PATH);

    private Map<String, SqlWrapper> sqlWrapperMap = new HashMap<>();

    public Map<String, SqlWrapper> getSqlWrapperMap() {
        return sqlWrapperMap;
    }

    public ParseSqlXml() {
        init();
    }

    private void init() {
        URL url = this.getClass().getClassLoader()
                .getResource(sqlPath.toString().replaceAll("/+", File.pathSeparator));
        if (url == null) {
            throw new RuntimeException("Naming-SQL directory could not be found under resources");
        }
        File[] fileList = new File(url.getFile()).listFiles();
        if (fileList == null) {
            return;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                sqlPath = sqlPath.append("/").append(file.getName());
                init();
            } else {
                if (file.getName().toLowerCase(Locale.US).endsWith(Constant.SQL_FORMAT)) {
                    setSqlWrapperList(file);
                }
            }
        }
    }

    private void setSqlWrapperList(File file) {
        if (file == null) {
            return;
        }
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            throw new RuntimeException("File read failed, " + file.getName());
        }
        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.elements();
        elementList.forEach(element -> {
            String id = element.attribute("id").getValue();
            Attribute resultType = element.attribute("resultType");
            int before = sqlWrapperMap.size();
            if (resultType != null) {
                sqlWrapperMap.put(id, new SqlWrapper(id, resultType.getValue(), element.getTextTrim()));
                int after = sqlWrapperMap.size();
                if (before == after) {
                    throw new RuntimeException("exist repeat sql, " + id);
                }
                return;
            }
            sqlWrapperMap.put(id, new SqlWrapper(id, null, element.getTextTrim()));
            int after = sqlWrapperMap.size();
            if (before == after) {
                throw new RuntimeException("exist repeat sql, " + id);
            }
        });
    }
}
