/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.osgi.framework.Bundle;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.matching.date.pattern.DatePatternRetriever;
import org.talend.dataquality.matching.date.pattern.ModelMatcher;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Date Pattern Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class DatePatternFreqIndicatorImpl extends FrequencyIndicatorImpl implements DatePatternFreqIndicator {

    private static final String PATTERNS_FILENAME = "PatternsNameAndRegularExpressions.txt";

    private DatePatternRetriever dateRetriever;

    private boolean isDelimtedFile = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected DatePatternFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.DATE_PATTERN_FREQ_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.PatternFreqIndicatorImpl#prepare()
     */
    @Override
    public boolean prepare() {
        dateRetriever = new DatePatternRetriever();
        URL url = null;
        if (Platform.isRunning()) {
            Bundle bundle = Platform.getBundle("org.talend.dataquality.matching"); //$NON-NLS-1$
            url = bundle.getResource(PATTERNS_FILENAME);
            String filepath = null;
            try {
                filepath = FileLocator.toFileURL(url).getFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File file = new File(filepath);
            dateRetriever.initModel2Regex(file);
        } else {
            InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATTERNS_FILENAME);
            dateRetriever.initModel2Regex(inStream);
        }

        // MOD qiongli 2011-11-15 TDQ-3864,judge if it is file connection.
        MetadataColumn mdColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(this.getAnalyzedElement());
        if (mdColumn != null) {
            Connection Connection = ConnectionHelper.getTdDataProvider(mdColumn);
            if (Connection != null && SwitchHelpers.DELIMITEDFILECONNECTION_SWITCH.doSwitch(Connection) != null) {
                isDelimtedFile = true;
            }

        }

        return super.prepare();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.PatternFreqIndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {
        if (data != null) {
            // MOD qiongli 2011-11-11 TDQ-3864,format the date for file connection.
            if (data instanceof Date && isDelimtedFile) {
                MetadataColumn mdColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(this.getAnalyzedElement());
                String pattern = mdColumn.getPattern();
                if (pattern != null) {
                    pattern = StringUtils.replace(pattern, "\"", StringUtils.EMPTY);
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    data = sdf.format((Date) data);
                }
            }
            dateRetriever.handle(String.valueOf(data));
        }
        boolean returnValue = super.handle(data);
        // MOD yyi 2011-12-14 TDQ-4166:View rows for Date Pattern Frequency Indicator.
        return returnValue;
    }

    @Override
    public Double getFrequency(Object dataValue) {
        if (this.count.compareTo(0L) == 0) {
            return Double.NaN;
        } else

        if (dataValue instanceof ModelMatcher) {
            ModelMatcher dataMatcher = (ModelMatcher) dataValue;
            return ((double) dataMatcher.getScore()) / this.getCount().longValue();
        }
        return super.getFrequency(dataValue);
    }

    @Override
    public boolean finalizeComputation() {
        List<ModelMatcher> modelMatchers = dateRetriever.getModelMatchers();
        HashMap<Object, Long> map = new HashMap<Object, Long>();
        for (ModelMatcher matcher : modelMatchers) {
            map.put(matcher.getModel(), (long) matcher.getScore());
        }
        setValueToFreq(map);
        return super.finalizeComputation();
    }

    @Override
    public List<ModelMatcher> getModelMatcherList() {
        return dateRetriever.getModelMatchers();
    }

    /**
     * return List for ModelMatcher which Score more than 1.
     * 
     * @deprecated use {@link #getResult()} instead
     */
    @Deprecated
    @Override
    public List<Object> getRealModelMatcherList() {
        List<Object> realModelMatcherList = new ArrayList<Object>();
        for (ModelMatcher matcher : dateRetriever.getModelMatchers()) {
            if (matcher.getScore() > 0) {
                realModelMatcherList.add(matcher);
            }
        }
        return realModelMatcherList;
    }

    @Override
    public String getModel(Object matcher) {
        if (matcher instanceof ModelMatcher) {
            return ((ModelMatcher) matcher).getModel();
        } else {
            return null;
        }
    }

    @Override
    public int getScore(Object matcher) {
        if (matcher instanceof ModelMatcher) {
            return ((ModelMatcher) matcher).getScore();
        } else {
            return -1;
        }
    }

    /**
     * 
     * DOC zshen Comment method "getRegex".
     * 
     * @param model the model of matcher.
     * @return if can find corresponding to matcher return it's the Regex of matcher else return null;
     */
    @Override
    public String getRegex(String model) {
        return this.dateRetriever.getRegex(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.DatePatternFreqIndicator#getResult()
     */
    @Override
    public Map<String, Long> getResult() {
        Map<String, Long> result = new TreeMap<String, Long>();
        HashMap<Object, Long> values = this.getValueToFreq();
        // add the value which greater than zero into the result
        Iterator<Object> iterator = values.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Long value = values.get(key);
            if (value > 0) {
                result.put(key.toString(), value);
            }
        }
        return result;
    }

} // DatePatternFreqIndicatorImpl
