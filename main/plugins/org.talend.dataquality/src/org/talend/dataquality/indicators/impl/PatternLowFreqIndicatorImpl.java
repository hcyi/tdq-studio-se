/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.utils.string.AsciiUtils;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Pattern Low Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class PatternLowFreqIndicatorImpl extends LowFrequencyIndicatorImpl implements PatternLowFreqIndicator {

    private String charsToReplace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected PatternLowFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.PATTERN_LOW_FREQ_INDICATOR;
    }

    @Override
    public boolean prepare() {
        // MOD xqliu 2009-06-29 bug 7068
        final TextParameters textParameter = this.getParameters() == null ? null : this.getParameters().getTextParameter();
        // ~
        if (textParameter != null) {
            this.replacementChars = textParameter.getReplacementCharacters();
            this.charsToReplace = textParameter.getCharactersToReplace();
        }
        return super.prepare();
    }

    @Override
    public boolean handle(Object data) {
        if (data == null) {
            return super.handle(data);
        } else {
            String parsedData = convertCharacters(String.valueOf(data));
            return super.handle(parsedData);
        }
    }

    /**
     * add by zshen for feature 12919 to convertCharacter when save data into analysisDataSet with java engin
     */
    @Override
    public String convertCharacters(String data) {
        return AsciiUtils.replaceCharacters(String.valueOf(data), this.charsToReplace, this.replacementChars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#specialName(java.lang.Object)
     */
    @Override
    protected String specialName(Object name) {
        return convertCharacters(name.toString());
    }

} // PatternLowFreqIndicatorImpl
