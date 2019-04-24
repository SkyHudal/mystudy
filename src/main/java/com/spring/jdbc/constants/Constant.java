package com.spring.jdbc.constants;

public class Constant {

    //sql文件的路径
    public static final String SQL_PATH = "naming-sql";

    public static final String SQL_FORMAT = "naming.sql.xml";

    public class Regular {
        public static final String DYNAMIC_PARAMETER = "<\\[.*]>";

        public static final String PARAMETER_NAME = ":\\s*[A-Za-z0-9_$]+";

        public static final String COLUMN_NAME = "_[A-Za-z0-9_]";
    }
}
