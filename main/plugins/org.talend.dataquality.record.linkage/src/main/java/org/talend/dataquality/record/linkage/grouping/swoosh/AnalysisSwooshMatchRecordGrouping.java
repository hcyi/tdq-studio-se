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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

/**
 * created by yyin on 2014-9-15 Detailled comment
 * 
 */
public class AnalysisSwooshMatchRecordGrouping extends AnalysisMatchRecordGrouping {

    /**
     * DOC yyin AnalysisSwooshMatchRecordGrouping constructor comment.
     * 
     * @param matchResultConsumer
     */
    public AnalysisSwooshMatchRecordGrouping(MatchGroupResultConsumer matchResultConsumer) {
        super(matchResultConsumer);
    }

    @Override
    public void setSurvivorShipAlgorithmParams(SurvivorShipAlgorithmParams survivorShipAlgorithmParams) {
        super.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);
        swooshGrouping.initialMFBForOneRecord(getCombinedRecordMatcher(), survivorShipAlgorithmParams);
    }

    @Override
    public void doGroup(RichRecord currentRecord) {
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

    @Override
    public void end() {
        swooshGrouping.afterAllRecordFinished();
    }
}
