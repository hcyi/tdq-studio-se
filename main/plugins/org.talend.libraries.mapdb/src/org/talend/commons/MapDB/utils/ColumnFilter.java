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
package org.talend.commons.MapDB.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Columns filter
 * 
 */
public class ColumnFilter {

    // Which column need to be remained
    Integer[] remaindColumnIndexs = null;

    public ColumnFilter(Integer[] columnIndexArray) {
        Arrays.sort(columnIndexArray);
        remaindColumnIndexs = columnIndexArray;
    }

    public List<Object[]> filterArray(List<Object[]> inputData) {
        List<Object[]> retrunList = new ArrayList<Object[]>();
        for (int i = 0; i < inputData.size(); i++) {
            // view values case
            Object[] currentInputData = inputData.get(i);
            if (remaindColumnIndexs.length == 1) {
                Object[] filterViewValuesData = filterViewValuesData(currentInputData);
                if (!isDuplicate(filterViewValuesData, retrunList)) {
                    retrunList.add(filterViewValuesData);
                }
            } else {// view values but column size is more than one(no this case when write those code)
                Object filterArray[] = new Object[remaindColumnIndexs.length];
                for (int remainIndex = 0; remainIndex < remaindColumnIndexs.length; remainIndex++) {
                    if (inputData.get(i).length >= remaindColumnIndexs[remainIndex]) {
                        filterArray[remainIndex] = inputData.get(i)[remaindColumnIndexs[remainIndex]];
                    }
                }
                retrunList.add(filterArray);
            }

        }
        return retrunList;
    }

    /**
     * Judge wheter current data is duplicate one
     * 
     * @param filterViewValuesData
     * @return
     */
    private boolean isDuplicate(Object[] filterViewValuesData, List<Object[]> retrunList) {
        for (Object[] noDuplicateArray : retrunList) {
            if (noDuplicateArray[0] == null && filterViewValuesData[0] == null) {
                return true;
            } else if (noDuplicateArray[0] == null || filterViewValuesData[0] == null) {
                continue;
            }
            if (noDuplicateArray[0].equals(filterViewValuesData[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC talend Comment method "filterViewValuesData".
     */
    private Object[] filterViewValuesData(Object[] currentInputData) {
        Object filterArray[] = new Object[remaindColumnIndexs.length];
        for (int remainIndex = 0; remainIndex < remaindColumnIndexs.length; remainIndex++) {
            if (currentInputData.length >= remaindColumnIndexs[remainIndex]) {
                filterArray[remainIndex] = currentInputData[remaindColumnIndexs[remainIndex]];
            }
        }
        return filterArray;

    }

    public List<Object> filter(List<Object> inputData) {
        List<Object> retrunList = new ArrayList<Object>();

        for (Integer remaindColumnIndex : remaindColumnIndexs) {
            if (inputData.size() >= remaindColumnIndex) {
                retrunList.add(inputData.get(remaindColumnIndex));
            }
        }
        return retrunList;
    }
}
