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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

/**
 * created by talend on Aug 25, 2014 Detailled comment
 * 
 */
public class ColumnSetDBMap extends DBMap<List<Object>, Long> {

    public ColumnSetDBMap(String parentFullPathStr, String fileName, String mapName) {
        super(parentFullPathStr, fileName, mapName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.MapDB.utils.DBMap#subList(long, long, java.util.Map)
     */
    @Override
    public List<Object[]> subList(long fromIndex, long toIndex, Map<Long, List<Object>> indexMap) {
        boolean stratToRecord = false;
        List<Object[]> returnList = new ArrayList<Object[]>();
        List<Object> fromKey = null;
        List<Object> toKey = null;
        if (indexMap != null) {
            fromKey = indexMap.get(fromIndex);
            toKey = indexMap.get(toIndex);
        }
        Iterator<List<Object>> iterator = null;
        int index = 0;
        if (fromKey == null) {
            iterator = this.iterator();
        } else if (toKey == null) {
            NavigableSet<List<Object>> tailSet = tailSet(fromKey, true);
            stratToRecord = true;
            iterator = tailSet.iterator();
        } else {
            NavigableSet<List<Object>> tailSet = subSet(fromKey, toKey);
            stratToRecord = true;
            iterator = tailSet.iterator();
        }

        while (iterator.hasNext()) {
            List<Object> next = iterator.next();
            if (index == 0 && fromKey == null && indexMap != null) {
                indexMap.put(0l, next);
            }
            if (index == fromIndex) {
                stratToRecord = true;
            }
            if (index == toIndex && indexMap != null) {
                if (toKey == null) {
                    indexMap.put(toIndex, next);
                }
                break;
            }
            if (stratToRecord == true) {
                Object arrayElement[] = new Object[next.size() + 1];
                for (int i = 0; i < next.size(); i++) {
                    arrayElement[i] = next.get(i);
                }
                Long value = this.get(next);
                arrayElement[next.size()] = (value == null ? "" : value.toString()); //$NON-NLS-1$
                returnList.add(arrayElement);
            }
            index++;

        }

        return returnList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.MapDB.utils.AbstractDB#subList(long, long, java.util.Map,
     * org.talend.commons.MapDB.utils.DataValidation)
     */
    @Override
    public List<Object[]> subList(long fromIndex, long toIndex, Map<Long, List<Object>> indexMap, DataValidation dataValiator) {
        boolean stratToRecord = false;
        List<Object[]> returnList = new ArrayList<Object[]>();
        List<Object> fromKey = null;
        List<Object> toKey = null;
        if (indexMap != null) {
            fromKey = indexMap.get(fromIndex);
            toKey = indexMap.get(toIndex);
        }
        Iterator<List<Object>> iterator = null;
        int index = 0;
        if (fromKey == null) {
            iterator = this.iterator();
        } else if (toKey == null) {
            NavigableSet<List<Object>> tailSet = tailSet(fromKey, true);
            iterator = tailSet.iterator();
            index = (int) fromIndex;
        } else {
            NavigableSet<List<Object>> tailSet = subSet(fromKey, toKey);
            iterator = tailSet.iterator();
            index = (int) fromIndex;
        }

        while (iterator.hasNext()) {
            List<Object> next = iterator.next();
            if (dataValiator != null && !dataValiator.isValid(this.get(next))) {
                continue;
            }
            if (index == 0 && fromKey == null && indexMap != null) {
                indexMap.put(0l, next);
            }
            if (index == fromIndex) {
                stratToRecord = true;
                if (fromKey == null && indexMap != null) {
                    indexMap.put(fromIndex, next);
                }

            }
            if (index == toIndex && indexMap != null) {
                if (toKey == null) {
                    indexMap.put(toIndex, next);
                }
                break;
            }
            if (stratToRecord == true) {
                returnList.add(next.toArray());
            }
            index++;

        }

        return returnList;
    }

}
