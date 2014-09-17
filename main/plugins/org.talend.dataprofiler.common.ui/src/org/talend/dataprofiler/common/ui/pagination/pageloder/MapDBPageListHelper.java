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
package org.talend.dataprofiler.common.ui.pagination.pageloder;

import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.DefaultSortProcessor;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.SortProcessor;
import org.talend.commons.MapDB.utils.AbstractDB;
import org.talend.commons.MapDB.utils.ColumnFilter;
import org.talend.commons.MapDB.utils.DBMap;
import org.talend.commons.MapDB.utils.DBValueMap;
import org.talend.commons.MapDB.utils.DataValidation;

/**
 * created by talend on Aug 15, 2014 Detailled comment
 * 
 */
public class MapDBPageListHelper {

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap) {
        return createPage(db, controller, DefaultSortProcessor.getInstance(), indexMap, db.size(), (ColumnFilter) null);
    }

    public static <K, V> PageResult<Object[]> createPageByValue(DBValueMap<K, V> db, PageableController controller,
            Map<Long, K> indexMap, long itemsSize, ColumnFilter filter) {
        return createPageByValue(db, controller, DefaultSortProcessor.getInstance(), indexMap, itemsSize, filter);
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap,
            long itemsSize) {
        return createPage(db, controller, DefaultSortProcessor.getInstance(), indexMap, itemsSize, (ColumnFilter) null);
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap,
            long itemsSize, ColumnFilter columnFilter) {
        return createPage(db, controller, DefaultSortProcessor.getInstance(), indexMap, itemsSize, columnFilter);
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap,
            long itemsSize, DataValidation dataValidator) {
        return createPage(db, controller, DefaultSortProcessor.getInstance(), indexMap, itemsSize, dataValidator);
    }

    public static <K, V> PageResult<Object[]> createPageByValue(DBValueMap<K, V> db, PageableController controller,
            SortProcessor processor, Map<Long, K> indexMap, long itemsSize, ColumnFilter filter) {
        // sort is can not finished
        // int sortDirection = controller.getSortDirection();
        // if (sortDirection != SWT.NONE) {
        // // Sort the list
        // processor.sort(list, controller.getSortPropertyName(), sortDirection);
        // }
        long totalSize = itemsSize;
        long pageSize = controller.getPageSize();
        long pageIndex = controller.getPageOffset();

        long fromIndex = pageIndex;
        long toIndex = pageIndex + pageSize;
        if (toIndex > totalSize) {
            toIndex = totalSize;
        }
        List<Object[]> content = ((DBMap<K, V>) db).subValueList(fromIndex, toIndex, indexMap);
        if (filter != null) {
            content = filter.filterArray(content);
        }
        return new PageResult<Object[]>(content, content.size());
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, SortProcessor processor,
            Map<Long, T> indexMap, long itemsSize, ColumnFilter filter) {
        // sort is can not finished
        // int sortDirection = controller.getSortDirection();
        // if (sortDirection != SWT.NONE) {
        // // Sort the list
        // processor.sort(list, controller.getSortPropertyName(), sortDirection);
        // }
        long totalSize = itemsSize;
        long pageSize = controller.getPageSize();
        long pageIndex = controller.getPageOffset();

        long fromIndex = pageIndex;
        long toIndex = pageIndex + pageSize;
        if (toIndex > totalSize) {
            toIndex = totalSize;
        }
        List<Object[]> content = db.subList(fromIndex, toIndex, indexMap);
        if (filter != null) {
            content = filter.filterArray(content);
        }
        return new PageResult<Object[]>(content, content.size());
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, SortProcessor processor,
            Map<Long, T> indexMap, long itemsSize, DataValidation dataValidator) {
        // sort is can not finished
        // int sortDirection = controller.getSortDirection();
        // if (sortDirection != SWT.NONE) {
        // // Sort the list
        // processor.sort(list, controller.getSortPropertyName(), sortDirection);
        // }
        long totalSize = itemsSize;
        long pageSize = controller.getPageSize();
        long pageIndex = controller.getPageOffset();

        long fromIndex = pageIndex;
        long toIndex = pageIndex + pageSize;
        if (toIndex > totalSize) {
            toIndex = totalSize;
        }

        List<Object[]> content = db.subList(fromIndex, toIndex, indexMap, dataValidator);
        return new PageResult<Object[]>(content, content.size());
    }

}
