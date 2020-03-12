package com.visionagent.apptest.sqlite

import org.hibernate.dialect.identity.IdentityColumnSupportImpl

class SQLiteDialectIdentityColumnSupport : IdentityColumnSupportImpl() {
    override fun hasDataTypeInIdentityColumn(): Boolean {
        return false
    }

    override fun getIdentityColumnString(p0: Int): String {
        return "integer";
    }

    override fun getIdentitySelectString(p0: String?, p1: String?, p2: Int): String {
        return "select last_insert_rowid()"
    }

    override fun supportsIdentityColumns(): Boolean {
        return true
    }

}