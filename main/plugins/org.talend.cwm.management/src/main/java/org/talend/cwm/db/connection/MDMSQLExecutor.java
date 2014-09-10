// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection;

import java.util.Iterator;
import java.util.List;

import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.matchmerge.Record;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MDMSQLExecutor extends SQLExecutor {

    @SuppressWarnings("unused")
    private int limit;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.ISQLExecutor#executeQuery(org.talend.dataquality.analysis.Analysis)
     */
    public List<Object[]> executeQuery(DataManager connection, List<ModelElement> analysedElements) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unused")
    private String createSqlStatement(Analysis analysis) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.ISQLExecutor#setLimit(int)
     */
    public void setLimit(int limit) {
        this.limit = limit;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.cwm.db.connection.ISQLExecutor#getResultSetIterator(orgomg.cwm.foundation.softwaredeployment.DataManager
     * , java.util.List)
     */
    public Iterator<Record> getResultSetIterator(DataManager connection, List<ModelElement> analysedElements) {
        // TODO Auto-generated method stub
        return null;
    }

}
