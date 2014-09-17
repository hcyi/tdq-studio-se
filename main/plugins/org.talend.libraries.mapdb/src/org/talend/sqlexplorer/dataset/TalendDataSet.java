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
package org.talend.sqlexplorer.dataset;

import net.sourceforge.sqlexplorer.dataset.mapdb.SqlExplorerTalendDataSet;

import org.talend.commons.MapDB.utils.DataValidation;

/**
 * created by talend on Sep 2, 2014 Detailled comment
 * 
 */
public class TalendDataSet extends SqlExplorerTalendDataSet {

    protected DataValidation dataValidator = null;

    /**
     * TalendDataSet constructor.
     * 
     * @param columnLabels
     * @param data
     * @param pageSize
     */
    public TalendDataSet(String[] columnLabels, Comparable[][] data, int pageSize) {
        super(columnLabels, data, pageSize);
    }

}
