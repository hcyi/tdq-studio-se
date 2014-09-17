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
package org.talend.dq.indicators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.core.IRepositoryContextService;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.fileprocess.FileInputDelimited;
import org.talend.utils.sql.TalendTypeConvert;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DelimitedFileIndicatorEvaluator extends IndicatorEvaluator {

    private Analysis analysis = null; // FIXME do we need to hide the same field from the parent class?

    private DelimitedFileConnection delimitedFileconnection = null;

    private Logger log = Logger.getLogger(DelimitedFileIndicatorEvaluator.class);

    private boolean isBablyForm = false;

    /**
     * DOC qiongli DelimitedFileIndicatorEvaluator constructor comment.
     * 
     * @param analysis
     */
    public DelimitedFileIndicatorEvaluator(Analysis analysis) {
        super(analysis);
        this.analysis = analysis;
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) {
        ReturnCode returnCode = new ReturnCode(true);
        if (delimitedFileconnection == null) {
            delimitedFileconnection = (DelimitedFileConnection) analysis.getContext().getConnection();
        }
        if (delimitedFileconnection.isContextMode()) {
            IRepositoryContextService service = CoreRuntimePlugin.getInstance().getRepositoryContextService();
            delimitedFileconnection = (DelimitedFileConnection) service.cloneOriginalValueConnection(delimitedFileconnection);
        }

        String path = JavaSqlFactory.getURL(delimitedFileconnection);
        IPath iPath = new Path(path);

        try {
            File file = iPath.toFile();

            if (!file.exists()) {
                returnCode.setReturnCode(Messages.getString("System can not find the file specified"), false); //$NON-NLS-1$
                return returnCode;
            }

            List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            indicToRowMap.clear();

            List<MetadataColumn> columnElementList = new ArrayList<MetadataColumn>();
            for (int i = 0; i < analysisElementList.size(); i++) {
                MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(i);
                MetadataTable mTable = ColumnHelper.getColumnOwnerAsMetadataTable(mColumn);
                columnElementList = mTable == null ? columnElementList : mTable.getColumns();
                if (!columnElementList.isEmpty()) {
                    break;
                }
            }
            // use CsvReader to parse.
            if (Escape.CSV.equals(delimitedFileconnection.getEscapeType())) {
                useCsvReader(file, delimitedFileconnection, analysisElementList, columnElementList, indicToRowMap);
            } else {
                // use TOSDelimitedReader in FileInputDelimited to parse.
                FileInputDelimited fileInputDelimited = AnalysisExecutorHelper.createFileInputDelimited(delimitedFileconnection);

                long currentRow = JavaSqlFactory.getHeadValue(delimitedFileconnection);

                while (fileInputDelimited.nextRecord()) {
                    if (!continueRun()) {
                        break;
                    }
                    currentRow++;
                    int columsCount = fileInputDelimited.getColumnsCountOfCurrentRow();
                    String[] rowValues = new String[columsCount];
                    for (int i = 0; i < columsCount; i++) {
                        rowValues[i] = fileInputDelimited.get(i);
                    }
                    handleByARow(rowValues, currentRow, analysisElementList, columnElementList, indicToRowMap);
                }
                fileInputDelimited.close();
            }
            // Added yyin 20120608 TDQ-3589
            for (MetadataColumn col : columnElementList) {
                List<Indicator> indicators = getIndicators(col.getLabel());
                for (Indicator indicator : indicators) {
                    if (indicator instanceof DuplicateCountIndicator) {
                        AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
                        if (analyzedDataSet == null) {
                            analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                            indicToRowMap.put(indicator, analyzedDataSet);
                            analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
                            analyzedDataSet.setRecordSize(0);
                        }
                        // indicator.finalizeComputation();
                        addResultToIndicatorToRowMap(indicator, indicToRowMap);
                    }
                }
            }// ~

        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    private void useCsvReader(File file, DelimitedFileConnection dfCon, List<ModelElement> analysisElementList,
            List<MetadataColumn> columnElementList, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        int limitValue = JavaSqlFactory.getLimitValue(delimitedFileconnection);
        int headValue = JavaSqlFactory.getHeadValue(delimitedFileconnection);
        CsvReader csvReader = null;
        try {
            csvReader = AnalysisExecutorHelper.createCsvReader(file, delimitedFileconnection);

            AnalysisExecutorHelper.initializeCsvReader(delimitedFileconnection, csvReader);

            for (int i = 0; i < headValue && csvReader.readRecord(); i++) {
                // do nothing, just ignore the header part
            }
            String[] rowValues = null;
            long currentRecord = 0;
            while (csvReader.readRecord()) {
                currentRecord = csvReader.getCurrentRecord();
                if (!continueRun() || limitValue != -1 && currentRecord > limitValue - 1) {
                    break;
                }

                if (delimitedFileconnection.isFirstLineCaption() && currentRecord == 0) {
                    continue;
                }
                rowValues = csvReader.getValues();
                handleByARow(rowValues, currentRecord + 1, analysisElementList, columnElementList, indicToRowMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
        }
    }

    /**
     * DOC yyin Comment method "addResultToIndicatorToRowMap".
     * 
     * @param indicator
     * @param indicToRowMap
     */
    private void addResultToIndicatorToRowMap(Indicator indicator, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        Map<Object, List<Object[]>> dupMap = ((DuplicateCountIndicator) indicator).getDuplicateMap();

        Iterator<Object> iterator = dupMap.keySet().iterator();
        int maxNumberRows = analysis.getParameters().getMaxNumberRows();

        while (iterator.hasNext()) {
            Object key = iterator.next();

            List<Object[]> valuelist = dupMap.get(key);
            if (valuelist.size() > 1) {
                List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, key);
                // MOD zshen add another loop to insert all of columnValue on the row into indicator.
                int recordIncrement = valueObjectList.size();

                for (Object[] row : valuelist) {
                    if (recordIncrement < maxNumberRows) {
                        valueObjectList.add(row);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected ReturnCode closeConnection() {
        return new ReturnCode(true);

    }

    public DelimitedFileConnection getDelimitedFileconnection() {
        return this.delimitedFileconnection;
    }

    public void setDelimitedFileconnection(DelimitedFileConnection delimitedFileconnection) {
        this.delimitedFileconnection = delimitedFileconnection;
    }

    @Override
    protected ReturnCode checkConnection() {
        if (delimitedFileconnection == null) {
            return new ReturnCode(Messages.getString("Evaluator.openNullConnection"), false); //$NON-NLS-1$
        }
        return new ReturnCode(true);
    }

    private void handleByARow(String[] rowValues, long currentRow, List<ModelElement> analysisElementList,
            List<MetadataColumn> columnElementList, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        Object object = null;
        int maxNumberRows = analysis.getParameters().getMaxNumberRows();
        int recordIncrement = 0;
        element: for (int i = 0; i < analysisElementList.size(); i++) {
            MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(i);
            Integer position = ColumnHelper.getColumnIndex(mColumn);
            // warning with a file of badly form
            if (position == null || position >= rowValues.length) {
                log.warn(Messages.getString("DelimitedFileIndicatorEvaluator.incorrectData", //$NON-NLS-1$
                        mColumn.getLabel(), currentRow, delimitedFileconnection.getFilePath()));
                if (!isBablyForm) {
                    isBablyForm = true;
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialog.openWarning(null,
                                    Messages.getString("DelimitedFileIndicatorEvaluator.badlyForm.Title"), //$NON-NLS-1$
                                    Messages.getString("DelimitedFileIndicatorEvaluator.badlyForm.Message")); //$NON-NLS-1$
                        }
                    });
                }
                continue;
            }

            object = TalendTypeConvert.convertToObject(mColumn.getTalendType(), rowValues[position], mColumn.getPattern());
            List<Indicator> indicators = getIndicators(mColumn.getLabel());
            for (Indicator indicator : indicators) {
                indicator.setDrillDownLimitSize(Long.valueOf(maxNumberRows));
                if (!continueRun()) {
                    break element;
                }
                // bug 19036,to irregularly data,still compute for RowCountIndicator
                if (object == null && !(indicator instanceof RowCountIndicator)) {
                    continue element;
                }
                // Added yyin 20120608 TDQ-3589
                if (indicator instanceof DuplicateCountIndicator) {
                    ((DuplicateCountIndicator) indicator).handle(object, rowValues);
                } else { // ~
                    indicator.handle(object);
                }
                AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
                if (analyzedDataSet == null) {
                    analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                    indicToRowMap.put(indicator, analyzedDataSet);
                    analyzedDataSet.setDataCount(maxNumberRows);
                    analyzedDataSet.setRecordSize(0);
                }
                // TDQ-9413: fix the drill down for file connection get no values
                // see IndicatorEvaluator line 166, the logic is almost the same
                if (analysis.getParameters().isStoreData()) {
                    if (indicator.mustStoreRow()) {
                        List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, object);
                        recordIncrement = valueObjectList.size();

                        for (int j = 0; j < rowValues.length; j++) {
                            Object newobject = rowValues[j];

                            if (indicator.isUsedMapDBMode()) {
                                indicator.handleDrillDownData(object, newobject, rowValues.length, j, mColumn.getLabel());
                                continue;
                            } else {
                                if (recordIncrement < maxNumberRows) {
                                    if (recordIncrement < valueObjectList.size()) {
                                        valueObjectList.get(recordIncrement)[j] = newobject;
                                    } else {
                                        Object[] valueObject = new Object[rowValues.length];
                                        valueObject[j] = newobject;
                                        valueObjectList.add(valueObject);
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    } else if (indicator instanceof UniqueCountIndicator
                            && analysis.getResults().getIndicToRowMap().get(indicator).getData() != null) {
                        List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator).getData();
                        if (columnElementList.size() == 0) {
                            continue;
                        }
                        int offsetting = columnElementList.indexOf(indicator.getAnalyzedElement());
                        for (Object[] dataObject : removeValueObjectList) {
                            // Added yyin 20120611 TDQ5279
                            if (object instanceof Integer) {
                                if (object.equals(Integer.parseInt((String) dataObject[offsetting]))) {
                                    removeValueObjectList.remove(dataObject);
                                    break;
                                }
                            }// ~
                            if (dataObject[offsetting].equals(object)) {
                                removeValueObjectList.remove(dataObject);
                                break;
                            }
                        }
                    }
                }
            }

        }
    }

}
