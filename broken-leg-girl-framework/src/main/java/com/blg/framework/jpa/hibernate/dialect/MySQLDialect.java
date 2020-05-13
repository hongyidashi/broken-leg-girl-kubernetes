package com.blg.framework.jpa.hibernate.dialect;

import org.hibernate.dialect.MySQL8Dialect;

/**
 * MYSQL方言
 */
public class MySQLDialect extends MySQL8Dialect {
    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return "";
    }
}
