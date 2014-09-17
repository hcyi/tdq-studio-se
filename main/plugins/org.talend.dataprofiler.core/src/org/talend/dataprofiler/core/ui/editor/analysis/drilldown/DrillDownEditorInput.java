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
package org.talend.dataprofiler.core.ui.editor.analysis.drilldown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.sqlexplorer.dataset.DataSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.commons.MapDB.utils.AbstractDB;
import org.talend.commons.MapDB.utils.ColumnFilter;
import org.talend.commons.MapDB.utils.ColumnSetDBMap;
import org.talend.commons.MapDB.utils.DBMap;
import org.talend.commons.MapDB.utils.DBSet;
import org.talend.commons.MapDB.utils.StandardDBName;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.analysis.impl.AnalyzedDataSetImpl;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.LengthIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.indicators.preview.table.PatternChartDataEntity;
import org.talend.sqlexplorer.dataset.MapDBColumnSetDataSet;
import org.talend.sqlexplorer.dataset.MapDBDataSet;
import org.talend.sqlexplorer.dataset.MapDBSetDataSet;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class DrillDownEditorInput implements IEditorInput {

    private Analysis analysis;

    private Indicator currIndicator;

    private MenuItemEntity menuItemEntity;

    private ChartDataEntity dataEntity;

    private String[] columnHeader = null;

    private String[][] columnValue = null;

    public static final int MENU_VALUE_TYPE = 1;

    public static final int MENU_VALID_TYPE = 2;

    public static final int MENU_INVALID_TYPE = 3;

    public DrillDownEditorInput() {

    }

    public DrillDownEditorInput(Analysis analysis, ChartDataEntity dataEntity, MenuItemEntity menuItemEntity) {
        this.analysis = analysis;
        this.currIndicator = dataEntity.getIndicator();
        this.menuItemEntity = menuItemEntity;
        this.dataEntity = dataEntity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return currIndicator.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText() {
        return analysis.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public Indicator getCurrIndicator() {
        return currIndicator;
    }

    public void setCurrIndicator(Indicator currIndicator) {
        this.currIndicator = currIndicator;
    }

    public String getMenuType() {
        return this.menuItemEntity.getLabel();
    }

    public String getSelectValue() {
        return this.dataEntity.getLabel();
    }

    public String getComputeValue() {
        if (judgeMenuType(this.getMenuType(), MENU_INVALID_TYPE)) {
            return ((PatternChartDataEntity) this.dataEntity).getNumNoMatch();
        } else if (judgeMenuType(this.getMenuType(), MENU_VALID_TYPE)) {
            return ((PatternChartDataEntity) this.dataEntity).getNumMatch();
        }
        return this.dataEntity.getValue();
    }

    /**
     * 
     * Judge current name of menu whether is same to menuType
     * 
     * @param menuStr is the name of the menu
     * @param menuType is the type which we think it should be
     * @return return true if menuStr is adapt to menuType, else return false
     */
    public static boolean judgeMenuType(String menuStr, int menuType) {
        if (menuStr == null) {
            return false;
        }
        switch (menuType) {
        case MENU_VALUE_TYPE:
            return menuStr.toLowerCase().indexOf("values") > -1;//$NON-NLS-1$
        case MENU_VALID_TYPE:
            return menuStr.toLowerCase().indexOf("valid") > -1;//$NON-NLS-1$
        case MENU_INVALID_TYPE:
            return menuStr.toLowerCase().indexOf("invalid") > -1;//$NON-NLS-1$
        default:
            return false;
        }
    }

    public DataSet getDataSet() {
        List<String> columnElementList = filterAdaptColumnHeader();
        columnHeader = new String[columnElementList.size()];
        int headerIndex = 0;
        for (String columnElement : columnElementList) {
            columnHeader[headerIndex++] = columnElement;
        }
        List<Object[]> newColumnElementList = filterAdaptDataList();
        if (newColumnElementList.size() <= 0) {
            columnValue = new String[0][0];
            return new DataSet(columnHeader, columnValue);
        }
        // MOD qiongli 2011-4-8,bug 19192.delimited file may has diffrent number of columns for every row.
        if (DrillDownEditorInput.judgeMenuType(getMenuType(), DrillDownEditorInput.MENU_VALUE_TYPE)) {
            columnValue = new String[newColumnElementList.size()][newColumnElementList.get(0).length];
        } else {
            columnValue = new String[newColumnElementList.size()][columnElementList.size()];
        }
        int rowIndex = 0;
        for (Object[] tableRow : newColumnElementList) {
            int columnIndex = 0;
            for (Object tableValue : tableRow) {
                // added yyin 20120523 TDQ-4691: in MDM, when these two size not equal, this for will throw exception
                if (tableValue == null) {
                    if (newColumnElementList.get(0).length != columnElementList.size()) {
                        continue;
                    }
                }// ~
                columnValue[rowIndex][columnIndex++] = tableValue == null ? "<null>" : tableValue.toString();
            }
            rowIndex++;
        }
        return new DataSet(columnHeader, columnValue);
    }

    /**
     * 
     * DataSet is used to be the input on the export wizard. unchecked is for the type of mapDB else will have a warning
     * 
     * @param controller
     * @return
     */
    @SuppressWarnings("unchecked")
    public DataSet getDataSetForMapDB(PageableController controller) {
        List<String> columnElementList = filterAdaptColumnHeader();
        columnHeader = new String[columnElementList.size()];
        int headerIndex = 0;
        for (String columnElement : columnElementList) {
            columnHeader[headerIndex++] = columnElement;
        }
        AbstractDB<?> mapDB = getMapDB();
        AnalysisType analysisType = analysis.getParameters().getAnalysisType();
        if (AnalysisType.COLUMN_SET == analysisType) {
            Long size = getCurrentIndicatorResultSize();
            if (ColumnSetDBMap.class.isInstance(mapDB)) {
                return new MapDBColumnSetDataSet(columnHeader, (ColumnSetDBMap) mapDB, size, currIndicator,
                        controller.getPageSize());
            }
        }

        if (DBSet.class.isInstance(mapDB)) {
            return new MapDBSetDataSet(columnHeader, (DBSet<Object>) mapDB, controller.getPageSize());
        } else {
            ColumnFilter columnFilter = getColumnFilter();
            Long itemSize = getItemSize(mapDB);
            return new MapDBDataSet(columnHeader, (DBMap<Object, List<Object>>) mapDB, controller.getPageSize(), columnFilter,
                    itemSize);
        }
    }

    /**
     * DOC talend Comment method "getPageSize".
     * 
     * @param controller
     * @return
     */
    private Long getItemSize(AbstractDB<?> mapDB) {
        if (DrillDownEditorInput.judgeMenuType(getMenuType(), DrillDownEditorInput.MENU_VALUE_TYPE)) {
            return getCurrentIndicatorResultSize();
        } else {
            return Long.valueOf(mapDB.size());
        }
    }

    /**
     * Create columnFilter for current columnSet. It will be used when we can drill down both current column and whole
     * of the row
     * 
     * @return
     */
    public ColumnFilter getColumnFilter() {
        Integer[] columnIndexArray = getColumnIndexArray();
        ColumnFilter filter = null;
        if (columnIndexArray != null) {
            filter = new ColumnFilter(columnIndexArray);
        }
        return filter;
    }

    /**
     * Get the result of current indicator. Which only be used by column Set analysis
     * 
     * @return
     */
    public Long getCurrentIndicatorResultSize() {
        Long itemsSize = 0l;
        if (isColumnSetIndicator()) {
            itemsSize = getColumnSetIndicatorResultSize();
        } else {
            itemsSize = currIndicator.getIntegerValue();
        }
        return itemsSize;
    }

    /**
     * DOC talend Comment method "isColumnSetIndicator".
     * 
     * @return
     */
    private boolean isColumnSetIndicator() {
        Analysis analysis = this.getAnalysis();
        AnalysisType analysisType = analysis.getParameters().getAnalysisType();
        return AnalysisType.COLUMN_SET == analysisType;
    }

    /**
     * DOC talend Comment method "getColumnSetIndicatorResultSize".
     * 
     * @param itemsSize
     * @return
     */
    private Long getColumnSetIndicatorResultSize() {
        Long itemsSize = 0l;
        SimpleStatIndicator simpleStatIndicator = null;
        // Find simpleStatIndicator from result of analysis
        for (Indicator indicator : analysis.getResults().getIndicators()) {
            if (SimpleStatIndicator.class.isInstance(indicator)) {
                simpleStatIndicator = (SimpleStatIndicator) indicator;
                break;
            }
        }
        // Get the Result from simpleStatIndicator by currIndicator
        if (simpleStatIndicator != null) {
            if (DuplicateCountIndicator.class.isInstance(currIndicator)) {
                itemsSize = simpleStatIndicator.getDuplicateCount();
            } else if (DistinctCountIndicator.class.isInstance(currIndicator)) {
                itemsSize = simpleStatIndicator.getDistinctCount();
            } else if (UniqueCountIndicator.class.isInstance(currIndicator)) {
                itemsSize = simpleStatIndicator.getUniqueCount();
            } else if (RowCountIndicator.class.isInstance(currIndicator)) {
                itemsSize = simpleStatIndicator.getCount();
            }
        }
        return itemsSize;
    }

    /**
     * Get MapDB which store the drill down data for current indicator
     * 
     * @return
     */
    public AbstractDB<Object> getMapDB() {
        AnalysisType analysisType = analysis.getParameters().getAnalysisType();
        AbstractDB<Object> columnSetMapDB = getColumnSetAnalysisMapDB(analysisType);
        if (columnSetMapDB != null) {
            return columnSetMapDB;
        }
        String dbMapName = getDBMapName(analysisType);
        return this.currIndicator.getMapDB(dbMapName);

    }

    /**
     * Get MapDB which store the drill down data for columnSet analysis
     * 
     * @param analysisType
     */
    private AbstractDB<Object> getColumnSetAnalysisMapDB(AnalysisType analysisType) {
        if (AnalysisType.COLUMN_SET == analysisType) {
            SimpleStatIndicator simpleStatIndicator = null;
            for (Indicator indicator : analysis.getResults().getIndicators()) {
                if (SimpleStatIndicator.class.isInstance(indicator)) {
                    simpleStatIndicator = (SimpleStatIndicator) indicator;
                    break;
                }
            }
            if (simpleStatIndicator != null) {
                return simpleStatIndicator.getMapDB(StandardDBName.dataSection.name());
            }
        }
        return null;
    }

    /**
     * Get the name of MapDB
     * 
     * @return
     */
    private String getDBMapName(AnalysisType analysisType) {
        String dbMapName = StandardDBName.drillDown.name();
        if (FrequencyIndicator.class.isInstance(currIndicator)) {
            dbMapName = this.getSelectValue();
        } else if (LengthIndicator.class.isInstance(currIndicator)) {
            String selectValue = ((LengthIndicator) currIndicator).getLength().toString();
            dbMapName = this.getSelectValue() + selectValue;
        } else if (AnalysisType.COLUMN_SET == analysisType) {
            dbMapName = StandardDBName.dataSection.name();
        }

        return dbMapName;
    }

    public boolean computeColumnValueLength(List<Object[]> newColumnElementList) {
        List<Integer> maxLength = new ArrayList<Integer>();
        for (Object[] columnValue : newColumnElementList) {
            maxLength.add(columnValue.length);
        }
        // ADD msjian 2011-6-20 22549: fixed another NoSuchElementException
        if (maxLength.size() == 0 || maxLength == null) {
            return true;
        }
        return Collections.max(maxLength) == Collections.min(maxLength);
    }

    /**
     * 
     * 
     * @return get the data which will be displayed on the drill down editor.
     */
    public List<Object[]> filterAdaptDataList() {
        // get columnValue
        List<Object[]> newColumnElementList = new ArrayList<Object[]>();
        AnalyzedDataSet analysisDataSet = this.getAnalysis().getResults().getIndicToRowMap().get(currIndicator);
        if (analysisDataSet.getData() != null && analysisDataSet.getData().size() > 0) {
            List<Object[]> dataList = analysisDataSet.getData();
            newColumnElementList.addAll(getDesignatedData(dataList));
        } else if (analysisDataSet.getFrequencyData() != null && analysisDataSet.getFrequencyData().size() > 0) {
            String selectValue = this.getSelectValue();
            if (currIndicator instanceof LengthIndicator) {
                selectValue = ((LengthIndicator) currIndicator).getLength().toString();
            }
            // MOD yyi 2011-12-14 TDQ-4166:View rows for Date Pattern Frequency Indicator.
            if (currIndicator instanceof DatePatternFreqIndicator) {
                for (Object expression : analysisDataSet.getFrequencyData().keySet()) {
                    if (Pattern.matches(((DatePatternFreqIndicator) currIndicator).getRegex(selectValue), expression.toString())) {
                        newColumnElementList.addAll(analysisDataSet.getFrequencyData().get(expression));
                    }
                }
            } else {
                // MOD msjian TDQ-4617 2012-2-8: fixed a NPE
                List<Object[]> list = analysisDataSet.getFrequencyData().get(selectValue);
                if (list != null && list.size() > 0) {
                    newColumnElementList.addAll(list);
                }
                // TDQ-4617 ~
            }
        } else if (analysisDataSet.getPatternData() != null && analysisDataSet.getPatternData().size() > 0) {
            if (DrillDownEditorInput.judgeMenuType(getMenuType(), DrillDownEditorInput.MENU_INVALID_TYPE)) {
                newColumnElementList.addAll(getDesignatedData((List<Object[]>) analysisDataSet.getPatternData().get(
                        AnalyzedDataSetImpl.INVALID_VALUE)));
            } else if (DrillDownEditorInput.judgeMenuType(getMenuType(), DrillDownEditorInput.MENU_VALID_TYPE)) {
                newColumnElementList.addAll(getDesignatedData((List<Object[]>) analysisDataSet.getPatternData().get(
                        AnalyzedDataSetImpl.VALID_VALUE)));
            }
        }
        return newColumnElementList;
    }

    /**
     * 
     * DOC zshen Comment method "getDesignatedData".
     * 
     * @return make column mapping with data
     */
    private List<Object[]> getDesignatedData(List<Object[]> dataList) {

        ModelElement analysisElement = currIndicator.getAnalyzedElement();

        List<Object[]> returnDataList = new ArrayList<Object[]>();
        if (dataList == null || dataList.size() < 0) {
            return returnDataList;
        }
        if (DrillDownEditorInput.judgeMenuType(this.getMenuType(), DrillDownEditorInput.MENU_VALUE_TYPE)) {
            int offset = 0;
            // MOD qiongli 2011-3-3 feature 19192 drill down for columnSet with jave engine.
            if (analysisElement == null && currIndicator.eContainer() instanceof SimpleStatIndicator) {
                returnDataList = dataList;

            } else {
                if (analysisElement instanceof MetadataColumn) {
                    MetadataTable mTable = ColumnHelper.getColumnOwnerAsMetadataTable((MetadataColumn) analysisElement);
                    List<MetadataColumn> columnElementList = mTable.getColumns();
                    offset = columnElementList.indexOf(analysisElement);
                } else if (analysisElement instanceof TdXmlElementType) {
                    TdXmlElementType parentElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(XmlElementHelper
                            .getParentElement(SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(analysisElement)));
                    List<TdXmlElementType> xmlElementListTmp = org.talend.cwm.db.connection.ConnectionUtils
                            .getXMLElements(parentElement);
                    List<TdXmlElementType> xmlElementList = new ArrayList<TdXmlElementType>();
                    for (TdXmlElementType tdXmlEle : xmlElementListTmp) {
                        if (!DqRepositoryViewService.hasChildren(tdXmlEle)) {
                            xmlElementList.add(tdXmlEle);
                        }
                    }
                    offset = xmlElementList.indexOf(analysisElement);
                }
                // Added yyin 20120608 TDQ-3589
                if (currIndicator instanceof DuplicateCountIndicator) {
                    for (Object obj : ((DuplicateCountIndicator) currIndicator).getDuplicateValues()) {
                        Object[] newObj = new Object[1];
                        newObj[0] = obj;
                        returnDataList.add(newObj);
                    }
                    return returnDataList;
                }// ~
                for (Object[] obj : dataList) {
                    Object[] newObj = new Object[1];
                    newObj[0] = obj[offset];
                    returnDataList.add(newObj);
                }
            }

        } else {
            returnDataList = dataList;
        }
        return returnDataList;
    }

    /**
     * 
     * create column header for columnSet analysis.
     * 
     * @param simpInd
     * @param columnElementList
     */
    private List<String> columnHeaderForColumnSet(SimpleStatIndicator simpInd) {

        List<String> columnElementList = new ArrayList<String>();
        if (simpInd.getAnalyzedColumns().size() == 0) {
            return columnElementList;
        }
        TdXmlElementType tdXmeElement = null;
        if (DrillDownEditorInput.judgeMenuType(this.getMenuType(), DrillDownEditorInput.MENU_VALUE_TYPE)) {
            for (ModelElement mod : simpInd.getAnalyzedColumns()) {
                tdXmeElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(mod);
                if (tdXmeElement != null) {
                    columnElementList.add(tdXmeElement.getName());
                } else {
                    columnElementList.add(ModelElementHelper.getName(mod));
                }

            }
        } else {
            boolean isDelimitedFile = false;
            boolean isMDM = false;
            for (ModelElement mColumn : simpInd.getAnalyzedColumns()) {
                tdXmeElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(mColumn);
                TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(mColumn);
                if (tdXmeElement != null) {
                    isMDM = true;
                } else if (tdColumn == null) {
                    isDelimitedFile = true;
                }
                break;
            }
            if (isMDM) {
                TdXmlElementType parentElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(XmlElementHelper
                        .getParentElement(SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(tdXmeElement)));
                List<TdXmlElementType> columnList = org.talend.cwm.db.connection.ConnectionUtils.getXMLElements(parentElement);
                for (TdXmlElementType tdXmlElement : columnList) {
                    columnElementList.add(tdXmlElement.getName());
                }

            } else if (isDelimitedFile) {
                List<MetadataColumn> columnList = ColumnHelper.getColumnOwnerAsMetadataTable(simpInd.getAnalyzedColumns().get(0))
                        .getColumns();
                for (MetadataColumn mdColumn : columnList) {
                    columnElementList.add(mdColumn.getLabel());
                }
            } else {
                List<TdColumn> columnList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch((simpInd
                        .getAnalyzedColumns().get(0).eContainer())));
                for (TdColumn tdColumn : columnList) {
                    columnElementList.add(tdColumn.getLabel());
                }
            }
        }
        return columnElementList;

    }

    /**
     * 
     * DOC zshen Comment method "filterAdaptColumnHeader".
     * 
     * @returnget the name of column which will be displayed on the drill down editor.
     */
    public List<String> filterAdaptColumnHeader() {
        // get columnHeader

        Indicator indicator = this.getCurrIndicator();
        ModelElement analysisElement = indicator.getAnalyzedElement();
        String menuType = this.getMenuType();
        List<String> columnElementList = new ArrayList<String>();
        // MOD qiongli 2011-3-3,feature 19192 ,drill down for columnSet with java engine .
        if (analysisElement == null && indicator.eContainer() instanceof SimpleStatIndicator) {
            columnElementList = columnHeaderForColumnSet((SimpleStatIndicator) indicator.eContainer());

        } else {
            // MOD qiongli 2011-1-9 feature 16796
            if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_VALUE_TYPE)) {

                columnElementList.add(ModelElementHelper.getName(indicator.getAnalyzedElement()));
            } else if (analysisElement instanceof TdColumn) {
                for (TdColumn column : TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator.getAnalyzedElement()
                        .eContainer()))) {
                    columnElementList.add(column.getName());
                }

            } else if (analysisElement instanceof MetadataColumn) {
                MetadataTable mTable = ColumnHelper.getColumnOwnerAsMetadataTable((MetadataColumn) analysisElement);
                for (MetadataColumn mColumn : mTable.getColumns()) {
                    columnElementList.add(mColumn.getLabel());
                }
            }
        }

        return columnElementList;
    }

    /**
     * Get index of column whiche will be used on the dirll down
     * 
     * @return
     */
    public Integer[] getColumnIndexArray() {
        if (!DrillDownEditorInput.judgeMenuType(this.getMenuType(), DrillDownEditorInput.MENU_VALUE_TYPE)) {
            return null;
        }
        List<Integer> indexArray = new ArrayList<Integer>();
        Indicator indicator = this.getCurrIndicator();
        ModelElement analysisElement = indicator.getAnalyzedElement();
        int index = 0;
        if (analysisElement instanceof TdColumn) {
            for (TdColumn column : TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator.getAnalyzedElement()
                    .eContainer()))) {
                if (column.getName().equals(analysisElement.getName())) {
                    indexArray.add(index);
                }
                index++;
            }

        } else if (analysisElement instanceof MetadataColumn) {
            MetadataTable mTable = ColumnHelper.getColumnOwnerAsMetadataTable((MetadataColumn) analysisElement);
            for (MetadataColumn mColumn : mTable.getColumns()) {
                if (mColumn.getLabel().equals(analysisElement.getName())) {
                    indexArray.add(index);
                }
                index++;
            }
        }

        return indexArray.toArray(new Integer[indexArray.size()]);
    }

    public boolean isDataSpill() {
        return analysis.getResults().getIndicToRowMap().get(currIndicator).getDataCount() < Double.parseDouble(getComputeValue());
    }

}
