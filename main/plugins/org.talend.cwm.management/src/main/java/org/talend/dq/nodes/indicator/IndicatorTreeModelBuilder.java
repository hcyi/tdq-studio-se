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
package org.talend.dq.nodes.indicator;

import org.talend.cwm.management.i18n.Messages;
import org.talend.dq.nodes.indicator.impl.IndicatorCategoryNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * This class for the indicator tree building.
 * 
 */
public final class IndicatorTreeModelBuilder {

    private static final String ADVANCED_LABEL = Messages.getString("IndicatorTreeModelBuilder.AdvancedStatistics"); //$NON-NLS-1$

    private static final String FRAUD_LABEL = Messages.getString("IndicatorTreeModelBuilder.FraudStatistics"); //$NON-NLS-1$

    private static final String PHONE_NUMBER_LABEL = Messages.getString("IndicatorTreeModelBuilder.PhonenumberStatistics"); //$NON-NLS-1$

    private static final String SOUNDEX_LABEL = Messages.getString("IndicatorTreeModelBuilder.SoundexStatistics"); //$NON-NLS-1$

    private static final String PATTERN_LABEL = Messages.getString("IndicatorTreeModelBuilder.PatternStatistics"); //$NON-NLS-1$

    private static final String SUMMARY_LABEL = Messages.getString("IndicatorTreeModelBuilder.SummaryStatistics"); //$NON-NLS-1$

    private static final String TEXT_LABEL = Messages.getString("IndicatorTreeModelBuilder.TextStatistics"); //$NON-NLS-1$

    private static final String SIMPLE_LABEL = Messages.getString("IndicatorTreeModelBuilder.SimpleStatistics"); //$NON-NLS-1$

    private IndicatorTreeModelBuilder() {
    }

    private static IndicatorCategoryNode[] indicatorCategoryNodes;

    /**
     * build Indicator Category. we DO NOT use the CACHE here, because sometimes, when the indicator definitions become
     * to Proxy, we can not get its property to get Label display on the select indicator dialog(can prefer to TDQ-8857)
     * 
     * @return
     */
    public static IIndicatorNode[] buildIndicatorCategory() {
        if (indicatorCategoryNodes != null) {
            return indicatorCategoryNodes;
        }
        // build Basic Statistic categoryNode
        IndicatorCategoryNode simpleCategoryNode = new IndicatorCategoryNode(SIMPLE_LABEL, IndicatorEnum.CountsIndicatorEnum);

        // build Text statistics categoryNode
        IndicatorCategoryNode textCategoryNode = new IndicatorCategoryNode(TEXT_LABEL, IndicatorEnum.TextIndicatorEnum);

        // build Summary Statistic categoryNode
        IndicatorCategoryNode boxCategoryNode = new IndicatorCategoryNode(SUMMARY_LABEL, IndicatorEnum.BoxIIndicatorEnum);

        // build pattern finder categoryNode
        IndicatorEnum[] patternFinderEnums = new IndicatorEnum[] { IndicatorEnum.PatternFreqIndicatorEnum,
                IndicatorEnum.PatternLowFreqIndicatorEnum, IndicatorEnum.DatePatternFreqIndicatorEnum };
        IndicatorCategoryNode patternFinderCategoryNode = new IndicatorCategoryNode(PATTERN_LABEL, patternFinderEnums);

        // MOD mzhao 2009-03-05 build Soundex Statistic categoryNode
        IndicatorEnum[] soundexIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.SoundexIndicatorEnum,
                IndicatorEnum.SoundexLowIndicatorEnum };
        IndicatorCategoryNode soundexCategoryNode = new IndicatorCategoryNode(SOUNDEX_LABEL, soundexIndicatorEnums);
        IndicatorCategoryNode phoneCategoryNode = new IndicatorCategoryNode(PHONE_NUMBER_LABEL,
                IndicatorEnum.PhoneNumbStatisticsIndicatorEnum);

        // build Nominal Statistic categoryNode
        IndicatorEnum[] advanceIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.ModeIndicatorEnum,
                IndicatorEnum.FrequencyIndicatorEnum, IndicatorEnum.DateFrequencyIndicatorEnum,
                IndicatorEnum.WeekFrequencyIndicatorEnum, IndicatorEnum.MonthFrequencyIndicatorEnum,
                IndicatorEnum.QuarterFrequencyIndicatorEnum, IndicatorEnum.YearFrequencyIndicatorEnum,
                IndicatorEnum.BinFrequencyIndicatorEnum, IndicatorEnum.LowFrequencyIndicatorEnum,
                IndicatorEnum.DateLowFrequencyIndicatorEnum, IndicatorEnum.WeekLowFrequencyIndicatorEnum,
                IndicatorEnum.MonthLowFrequencyIndicatorEnum, IndicatorEnum.QuarterLowFrequencyIndicatorEnum,
                IndicatorEnum.YearLowFrequencyIndicatorEnum, IndicatorEnum.BinLowFrequencyIndicatorEnum };
        IndicatorCategoryNode advanceCategoryNode = new IndicatorCategoryNode(ADVANCED_LABEL, advanceIndicatorEnums);

        // Added yyin 20120827, TDQ-5076, build Fraud Detection category and SIndicator
        IndicatorEnum[] fraudIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.BenfordLawFrequencyIndicatorEnum };
        IndicatorCategoryNode fraudCategoryNode = new IndicatorCategoryNode(FRAUD_LABEL, fraudIndicatorEnums);

        indicatorCategoryNodes = new IndicatorCategoryNode[] { simpleCategoryNode, textCategoryNode, boxCategoryNode,
                advanceCategoryNode, patternFinderCategoryNode, soundexCategoryNode, phoneCategoryNode, fraudCategoryNode };
        return indicatorCategoryNodes;
    }
}
