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
package org.talend.dataprofiler.core.ui.editor.preview.model.dataset;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYSeriesCollection;
import org.talend.dataprofiler.common.ui.editor.preview.ICustomerDataset;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class CustomerXYSeriesCollection extends XYSeriesCollection implements ICustomerDataset {

    private List<ChartDataEntity> dataEnities;

    public CustomerXYSeriesCollection() {
        dataEnities = new ArrayList<ChartDataEntity>();
    }

    public void addDataEntity(ChartDataEntity dataEntity) {
        dataEnities.add(dataEntity);
    }

    public void addDataEntity(ChartDataEntity[] dataEntity) {
        for (ChartDataEntity data : dataEntity) {
            dataEnities.add(data);
        }
    }

    public ChartDataEntity[] getDataEntities() {
        return dataEnities.toArray(new ChartDataEntity[dataEnities.size()]);
    }

}
