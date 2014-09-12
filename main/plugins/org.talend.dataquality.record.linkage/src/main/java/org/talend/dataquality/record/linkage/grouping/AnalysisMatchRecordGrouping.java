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
package org.talend.dataquality.record.linkage.grouping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

/**
 * created by zshen on Aug 7, 2013 Detailled comment
 * 
 */
public class AnalysisMatchRecordGrouping extends AbstractRecordGrouping<String> {

    /**
     * 
     */
    private static final String ISMASTER = "true"; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(AnalysisMatchRecordGrouping.class);

    private List<String[]> resultStrList = new ArrayList<String[]>();

    List<Object[]> inputList = new ArrayList<Object[]>();

    // Temporarily store the match result so that it can be iterated to be handled later after all of the records are
    // computed.
    private List<RichRecord> tmpMatchResult = new ArrayList<RichRecord>();

    private MatchGroupResultConsumer matchResultConsumer = null;

    @SuppressWarnings("deprecation")
    public AnalysisMatchRecordGrouping(MatchGroupResultConsumer matchResultConsumer) {
        this.matchResultConsumer = matchResultConsumer;
        setColumnDelimiter(columnDelimiter);
        setIsOutputDistDetails(true);
        setSeperateOutput(Boolean.TRUE);
    }

    public void addRuleMatcher(List<Map<String, String>> ruleMatcherConvertResult) {
        addMatchRule(ruleMatcherConvertResult);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#setSurvivorShipAlgorithmParams(org.talend
     * .dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams)
     */
    @Override
    public void setSurvivorShipAlgorithmParams(SurvivorShipAlgorithmParams survivorShipAlgorithmParams) {
        super.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);
        swooshGrouping.initialMFBForOneRecord(getCombinedRecordMatcher(), survivorShipAlgorithmParams);
    }

    /**
     * Sets the inputList.
     * 
     * @param inputRows the inputList to set
     */
    public void setMatchRows(List<Object[]> inputRows) {
        this.inputList = inputRows;
    }

    /**
     * 
     * The initialize(); method must be called before run.
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void run() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        try {
            for (Object[] inputRow : inputList) {
                String[] inputStrRow = new String[inputRow.length];
                int index = 0;
                for (Object obj : inputRow) {
                    inputStrRow[index++] = obj == null ? null : obj.toString();
                }
                doGroup(inputStrRow);
            }
            end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void doSwooshGroup(RichRecord currentRecord) {
        // translate the record's attribute to -->origalRow, and attributes only contain match keys
        translateRecordForSwoosh(currentRecord);

        swooshGrouping.oneRecordMatch(currentRecord);
    }

    /**
     * DOC yyin Comment method "translateRecordForSwoosh".
     * 
     * @param currentRecord
     */
    private void translateRecordForSwoosh(RichRecord currentRecord) {
        List<Attribute> matchAttrs = new ArrayList<Attribute>();
        List<DQAttribute<?>> rowList = new ArrayList<DQAttribute<?>>();
        for (Attribute attribute : currentRecord.getAttributes()) {
            DQAttribute<String> attri = new DQAttribute<String>(attribute.getLabel(), attribute.getColumnIndex(),
                    attribute.getValue());
            rowList.add(attri);
        }
        // clear the current attributes to only contain match keys
        IRecordMatcher recordMatcher = this.getCombinedRecordMatcher().getMatchers().get(0);
        for (IAttributeMatcher matcher : recordMatcher.getAttributeMatchers()) {
            for (Attribute attribute : currentRecord.getAttributes()) {
                if (StringUtils.equalsIgnoreCase(attribute.getLabel(), matcher.getAttributeName())) {
                    matchAttrs.add(attribute);
                    break;
                }
            }
        }
        currentRecord.getAttributes().clear();
        currentRecord.getAttributes().addAll(matchAttrs);
        currentRecord.setOriginRow(rowList);
    }

    public void doSwooshEnd() {
        swooshGrouping.afterAllRecordFinished();
    }

    /**
     * Getter for resultStrList.
     * 
     * @return the resultStrList
     */
    public List<String[]> getResultStrList() {
        return this.resultStrList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(COLUMN[])
     */
    @Override
    protected void outputRow(String[] row) {
        matchResultConsumer.handle(row);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#end()
     */
    @Override
    public void end() throws IOException, InterruptedException {
        super.end();
        if (matchResultConsumer.isKeepDataInMemory) {
            for (RichRecord row : tmpMatchResult) {
                // For swoosh algorithm, the GID can only be know after all of the records are computed.
                out(row);
            }
        }
        // Clear the GID map , no use anymore.
        swooshGrouping.getOldGID2New().clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(org.talend.dataquality.record
     * .linkage.grouping.swoosh.RichRecord)
     */
    @Override
    protected void outputRow(RichRecord row) {
        if (matchResultConsumer.isKeepDataInMemory) {
            tmpMatchResult.add(row);
        } else {
            out(row);
        }
    }

    /**
     * DOC zhao Comment method "out".
     * 
     * @param row
     */
    private void out(RichRecord row) {
        List<DQAttribute<?>> originRow = row.getOutputRow(swooshGrouping.getOldGID2New());
        String[] strRow = new String[originRow.size()];
        int idx = 0;
        for (DQAttribute<?> attr : originRow) {
            strRow[idx] = attr.getValue();
            idx++;
        }
        outputRow(strRow);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#isMaster(java.lang.Object)
     */
    @Override
    protected boolean isMaster(String col) {
        return ISMASTER.equals(col);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#modifyGroupSize(java.lang.Object)
     */
    @Override
    protected String incrementGroupSize(String oldGroupSize) {
        return String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#createCOLUMNArray(int)
     */
    @Override
    protected String[] createTYPEArray(int size) {
        return new String[size];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#getCOLUMNFromObject(java.lang.Object)
     */
    @Override
    protected String castAsType(Object objectValue) {
        return String.valueOf(objectValue);
    }

}
