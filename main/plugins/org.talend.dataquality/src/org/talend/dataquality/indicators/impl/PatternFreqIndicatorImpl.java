/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.utils.string.AsciiUtils;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Pattern Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class PatternFreqIndicatorImpl extends FrequencyIndicatorImpl implements PatternFreqIndicator {

    private String charsToReplace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected PatternFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.PATTERN_FREQ_INDICATOR;
    }

    @Override
    public boolean finalizeComputation() {
        // TODO Auto-generated method stub
        return super.finalizeComputation();
    }

    /**
     * add by zshen for feature 12919 to convertCharacter when save data into analysisDataSet with java engin
     */
    @Override
    public String convertCharacters(String data) {
        return AsciiUtils.replaceCharacters(String.valueOf(data), this.charsToReplace, this.replacementChars);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#specialName(java.lang.Object)
     */
    @Override
    protected String specialName(Object name) {
        return convertCharacters(name.toString());
    }

} // PatternFreqIndicatorImpl
