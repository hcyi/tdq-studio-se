/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.BenfordLawFrequencyIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Benford Law Frequency Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * * This Class provide the function to compute a group of data by Benford's Law and output the leading digits with its
 * distribution in this dataset. Then the user can use it to compare with the standard, to detect possible cases of
 * Fraud. can handle type: positive number , string, can not handle: negetive number
 * </p>
 * 
 * @generated
 */
public class BenfordLawFrequencyIndicatorImpl extends FrequencyIndicatorImpl implements BenfordLawFrequencyIndicator {

    private boolean isChecked = false;
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected BenfordLawFrequencyIndicatorImpl() {
        super();
    }

    /**
     * in order to check the result.
     * 
     * @Override public boolean storeSqlResults(List<Object[]> objects) { // store the sql results in: valueToFreq
     * super.storeSqlResults(objects);
     * 
     * // deal with valueToFreq for several cases checkValues();
     * 
     * return true; }
     */

    @Override
    public HashMap<Object, Long> getValueToFreqGen() {
        checkValues();

        return valueToFreq;
    }

    /**
     * handle with some special cases: 1) when there are null values; 2) when there miss some numbers between 1~9; 3)
     * when the column is double, the leading digit maybe 0; 4) when the column is string type, whenthe leading is not
     * 1~9, all counted into "invalid" one.
     */
    private void checkValues() {
        if (isChecked && valueToFreq.size() < 1)
            return;

        // check for invalid
        long counted = 0L;
        List<Object> invalid = new ArrayList<Object>();
        for (Object val : valueToFreq.keySet()) {
            if (isInvalid(val) < 0) {
                invalid.add(val);
                Long freq = this.valueToFreq.get(val);
                counted = (freq == null) ? counted : counted + freq;
            }
        }
        // combine all invalid into one <"invalid",counted>
        if (invalid.size() > 0) {
            for (Object val : invalid) {
                valueToFreq.remove(val);
            }
            valueToFreq.put("invalid", counted);
        }

        // check from 1~9, if miss, add it as <number, 0L>
        for (int i = 1; i < 10; i++) {
            Long value = valueToFreq.get(String.valueOf(i));
            if (value == null) {
                valueToFreq.put(String.valueOf(i), 0L);
            }
        }

        isChecked = true;
    }

    /**
     * if the val is 1~9, then return 1 if it is "0", return 0, if it is return , return -1
     * 
     * @param val
     * @return
     */
    private int isInvalid(Object val) {
        char lead = String.valueOf(val).charAt(0);
        if (Character.isDigit(lead)) {
            if ('0' == lead) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.BENFORD_LAW_FREQUENCY_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> count the occur times for 1~9 at the beginning of every numbers. <!-- end-user-doc -->
     * 
     * 
     * @generated NOT
     */
    @Override
    public boolean handle(Object data) {
        this.count++;

        if (data == null) {
            setValue("invalid");
            return true;
        }

        if (isInvalid(data) == 0) {
            setValue("0");
        } else if (isInvalid(data) < 0) {
            setValue("invalid");
        } else {
            setValue(String.valueOf(data).substring(0, 1));
        }

        return true;
    }

    private void setValue(String key) {
        // increment frequency of leading digit in data
        Long c = this.valueToFreq.get(key);
        if (c == null) {
            this.valueToFreq.put(key, 1L);
        } else {
            c++;
            this.valueToFreq.put(key, c);
        }
    }

} // BenfordLawFrequencyIndicatorImpl