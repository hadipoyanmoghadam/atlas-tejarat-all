// Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 2005/11/15 02:56:49 ?.?
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DB2390Dialect.java

package net.sf.hibernate.dialect;

// Referenced classes of package org.hibernate.dialect:
//            DB2Dialect

public class DB2390Dialect extends DB2Dialect {

    public DB2390Dialect() {
    }

    public boolean supportsSequences() {
        return false;
    }

    public String getIdentitySelectString() {
        return "select identity_val_local() from sysibm.sysdummy1";
    }

    public boolean supportsLimit() {
        return true;
    }

    public boolean supportsLimitOffset() {
        return false;
    }

    public String getLimitString(String sql, int offset, int limit) {
        return (new StringBuffer(sql.length() + 40)).append(sql).append(" fetch first ").append(limit).append(" rows only ").toString();
    }

    public boolean useMaxForLimit() {
        return true;
    }

    public boolean supportsVariableLimit() {
        return false;
    }
}