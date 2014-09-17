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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dataset.DataSet;
import net.sourceforge.sqlexplorer.dataset.DataSetRow;

import org.talend.commons.MapDB.utils.ColumnFilter;
import org.talend.commons.MapDB.utils.DBMap;

/**
 * created by talend on Aug 27, 2014 Detailled comment
 * 
 */
public class MapDBDataSet extends TalendDataSet {

    protected DBMap<Object, List<Object>> dataMap = null;

    protected int currentIndex = 0;

    protected Iterator<Object> iterator = null;

    protected ColumnFilter columnFilter = null;

    /**
     * DOC talend MapDBDataSet constructor comment.
     * 
     * @param columnLabels
     * @param data
     */
    public MapDBDataSet(String[] columnLabels, Comparable[][] data, int pageSize) {
        super(columnLabels, data, pageSize);
    }

    public MapDBDataSet(String[] columnLabels, DBMap<Object, List<Object>> imputDBMap, int pageSize, ColumnFilter cfilter,
            Long rowSize) {
        super(columnLabels, new Comparable[0][0], pageSize);
        this.dataMap = imputDBMap;
        iterator = dataMap.iterator();
        this.columnFilter = cfilter;
        this.rowSize = rowSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.sqlexplorer.dataset.DataSet#getRowCount()
     */
    @Override
    public int getRowCount() {
        if (rowSize != -1) {
            return (int) rowSize;
        } else if (dataMap != null) {
            return dataMap.size();
        } else {
            return super.getRowCount();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.sqlexplorer.dataset.DataSet#getRow(int)
     */
    @Override
    public DataSetRow getRow(int index) {
        DataSetRow returnDataSetRow = null;
        if (iterator == null) {
            return super.getRow(index);
        } else {
            if (index < 0 || index >= dataMap.size()) {
                throw new IndexOutOfBoundsException(Messages.getString("DataSet.errorIndexOutOfRange") + index);
            }
            if (currentIndex > index) {
                iterator = dataMap.iterator();
                currentIndex = 0;
            }
            while (currentIndex < index && iterator.hasNext()) {
                iterator.next();
                currentIndex++;
            }
            Object currentData = iterator.next();
            currentIndex++;
            List<Object> valueList = dataMap.get(currentData);
            if (columnFilter != null) {
                valueList = columnFilter.filter(valueList);
            }
            Comparable[] comparable = new Comparable[valueList.size()];
            for (int i = 0; i < valueList.size(); i++) {
                comparable[i] = (Comparable) valueList.get(i);
            }
            if (comparable.length == 0) {
                comparable = new Comparable[1];
                comparable[0] = null;
            }
            returnDataSetRow = new DataSetRow(this, comparable);
            return returnDataSetRow;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.sqlexplorer.dataset.mapdb.TalendDataSet#getCurrentPageDataSet()
     */
    @Override
    public DataSet getCurrentPageDataSet() {
        long pageSize = endIndex - startIndex;
        Comparable[][] compareArray = new Comparable[(int) (pageSize)][this.getColumns().length];
        List<Object[]> subList = this.dataMap.subList(startIndex, endIndex, null);
        if (columnFilter != null) {
            subList = columnFilter.filterArray(subList);
        }
        for (int i = 0; i < pageSize; i++) {
            Object[] objArray = subList.get(i);
            for (int j = 0; j < this.getColumns().length; j++) {
                compareArray[i][j] = (Comparable) objArray[j];
            }
        }
        return new DataSet(this.columnHeads, compareArray);
    }

}
