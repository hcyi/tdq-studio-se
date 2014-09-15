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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.fileprocess.FileInputDelimited;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;

/**
 * created by yyin on 2014-9-15 Detailled comment
 * 
 */
public class DelimitedFileIterator implements Iterator<Record> {

    private CsvReader csvReader = null;

    private long csvLimitValue = 0;

    private int csvHeadValue = 0;

    private FileInputDelimited fileInputDelimited;

    private boolean isCSV = false;

    private int analysedColumnIndex[];

    private String analysedColumnName[];

    private long index = 0;

    public DelimitedFileIterator(DelimitedFileConnection delimitedFileconnection, List<ModelElement> analysisElementList) {
        String path = AnalysisExecutorHelper.getFilePath(delimitedFileconnection);
        IPath iPath = new Path(path);
        File file = iPath.toFile();
        if (!file.exists()) {
            return;
        }
        isCSV = Escape.CSV.equals(delimitedFileconnection.getEscapeType());
        analysedColumnIndex = new int[analysisElementList.size()];
        analysedColumnName = new String[analysisElementList.size()];
        try {
            if (isCSV) {
                useCsvReader(file, delimitedFileconnection, analysisElementList);
            } else {
                useFileInputDelimited(file, delimitedFileconnection, analysisElementList);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * DOC yyin Comment method "useFileInputDelimited".
     * 
     * @param file
     * @param delimitedFileconnection
     * @param analysisElementList
     * @throws IOException
     */
    private void useFileInputDelimited(File file, DelimitedFileConnection delimitedFileconnection,
            List<ModelElement> analysisElementList) throws IOException {
        fileInputDelimited = AnalysisExecutorHelper.createFileInputDelimited(delimitedFileconnection);

        findElementPositionByColumn(analysisElementList);
    }

    /**
     * find the position of the analysed elements in the file table's column
     */
    private void findElementPositionByColumn(List<ModelElement> analysisElementList) {
        MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(0);
        MetadataTable metadataTable = ColumnHelper.getColumnOwnerAsMetadataTable(mColumn);
        EList<MetadataColumn> columns = metadataTable.getColumns();
        List<String> columnLabels = new ArrayList<String>();
        for (MetadataColumn column : columns) {
            columnLabels.add(column.getLabel());
        }
        for (int j = 0; j < analysisElementList.size(); j++) {
            analysedColumnName[j] = analysisElementList.get(j).getName();
            analysedColumnIndex[j] = columnLabels.indexOf(analysedColumnName[j]);
        }
    }

    /**
     * DOC yyin Comment method "useCsvReader".
     * 
     * @param file
     * @param delimitedFileconnection
     * @param analysisElementList
     * @throws IOException
     */
    private void useCsvReader(File file, DelimitedFileConnection delimitedFileconnection, List<ModelElement> analysisElementList)
            throws IOException {
        csvLimitValue = AnalysisExecutorHelper.getLimitValue(delimitedFileconnection);
        csvHeadValue = AnalysisExecutorHelper.getHeadValue(delimitedFileconnection);
        csvReader = AnalysisExecutorHelper.createCsvReader(file, delimitedFileconnection);
        AnalysisExecutorHelper.initializeCsvReader(delimitedFileconnection, csvReader);

        findElementPosition(analysisElementList);
    }

    /**
     * need to find the analysed element position , and only get these analysed column's values.
     * 
     * @param analysisElementList
     * @throws IOException
     */
    private void findElementPosition(List<ModelElement> analysisElementList) throws IOException {
        List<String> columnLabels = new ArrayList<String>();
        for (int i = 0; i < csvHeadValue && csvReader.readRecord(); i++) {
            Collections.addAll(columnLabels, csvReader.getValues());
        }
        for (int j = 0; j < analysisElementList.size(); j++) {
            analysedColumnName[j] = analysisElementList.get(j).getName();
            analysedColumnIndex[j] = columnLabels.indexOf(analysedColumnName[j]);
        }
    }

    /*
     * check if the file connection has next record, if not, close the file.
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        boolean hasNext = false;
        try {
            if (isCSV) {
                hasNext = csvReader.readRecord();
            } else {
                hasNext = fileInputDelimited.nextRecord();
            }
            if (!hasNext) {
                close();
            }
            return hasNext;
        } catch (IOException e) {
            close();
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    public Record next() {
        try {
            if (isCSV) {
                return nextFromCsv();
            } else {
                return nextFromFileInput();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not build next result", e); //$NON-NLS-1$
            // return null;
        }
    }

    /**
     * DOC yyin Comment method "nextFromFileInput".
     * 
     * @return
     * @throws IOException
     */
    private Record nextFromFileInput() throws IOException {
        String[] rowValues = new String[analysedColumnIndex.length];
        for (int i = 0; i < analysedColumnIndex.length; i++) {
            rowValues[i] = fileInputDelimited.get(analysedColumnIndex[i]);
        }
        return createRichRecord(rowValues);
    }

    /**
     * DOC yyin Comment method "nextFromCsv".
     * 
     * @return
     * @throws IOException
     */
    private Record nextFromCsv() throws IOException {
        long currentRecord = csvReader.getCurrentRecord();
        if (csvLimitValue != -1 && currentRecord > csvLimitValue - 1) {
            return null;
        }
        String[] values = csvReader.getValues();
        String[] analysedValues = new String[analysedColumnIndex.length];
        for (int i = 0; i < analysedColumnIndex.length; i++) {
            analysedValues[i] = values[analysedColumnIndex[i]];
        }
        return createRichRecord(analysedValues);
    }

    /**
     * DOC yyin Comment method "createRichRecord".
     * 
     * @param analysedValues
     * @return
     */
    private Record createRichRecord(String[] analysedValues) {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        for (int i = 0; i < analysedValues.length; i++) {
            Attribute attribute = new Attribute(analysedColumnName[i], i);

            String value = String.valueOf(analysedValues[i]);
            attribute.setValue(value);
            attributes.add(attribute);
        }
        return new RichRecord(attributes, String.valueOf(index++), 0, StringUtils.EMPTY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Read only iterator"); //$NON-NLS-1$
    }

    public void close() {
        if (isCSV) {
            this.csvReader.close();
        } else {
            this.fileInputDelimited.close();
        }
    }

}
