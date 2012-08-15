// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.dbms;

import org.talend.utils.ProductVersion;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class HiveDbmsLanguage extends DbmsLanguage {

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     */
    public HiveDbmsLanguage() {
        super(DbmsLanguage.HIVE);
    }

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     */
    public HiveDbmsLanguage(String dbmsType) {
        super(dbmsType);
    }

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param dbVersion
     */
    public HiveDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    public String toQualifiedName(String catalog, String schema, String table) {
        return super.toQualifiedName(null, null, table);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getTopNQuery(java.lang.String, int)
     */
    @Override
    public String getTopNQuery(String query, int n) {
        return query + " LIMIT " + n; //$NON-NLS-1$
    }

}