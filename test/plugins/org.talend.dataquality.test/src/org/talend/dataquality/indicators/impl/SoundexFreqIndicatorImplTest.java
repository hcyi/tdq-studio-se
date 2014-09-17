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
package org.talend.dataquality.indicators.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.SoundexFreqIndicator;

/**
 * created by talend on Aug 12, 2014 Detailled comment
 * 
 */
public class SoundexFreqIndicatorImplTest {

    /**
     * Test method for {@link org.talend.dataquality.indicators.impl.SoundexFreqIndicatorImpl#handle(java.lang.Object)}.
     */
    @Test
    public void testHandleObject() {
        Object empty = null;
        // mapDB case
        SoundexFreqIndicator soundexFreqIndicator = IndicatorsFactory.eINSTANCE.createSoundexFreqIndicator();
        soundexFreqIndicator.reset();
        soundexFreqIndicator.setUsedMapDBMode(true);
        soundexFreqIndicator.handle(empty);
        soundexFreqIndicator.handle("mac");
        soundexFreqIndicator.handle("mic");
        soundexFreqIndicator.handle(empty);
        soundexFreqIndicator.handle("mic");
        soundexFreqIndicator.handle("mac");
        soundexFreqIndicator.handle("machine");
        soundexFreqIndicator.handle(empty);
        soundexFreqIndicator.handle("machine");
        soundexFreqIndicator.handle("unique");

        soundexFreqIndicator.finalizeComputation();
        Assert.assertEquals(true, soundexFreqIndicator.getUniqueValues().isEmpty());
        Assert.assertEquals(2, soundexFreqIndicator.getValueToDistinctFreq().get("mic").intValue());
        Assert.assertEquals(0, soundexFreqIndicator.getValueToDistinctFreq().get(empty).intValue());
        Assert.assertEquals(1, soundexFreqIndicator.getValueToDistinctFreq().get("machine").intValue());
        Assert.assertEquals(1, soundexFreqIndicator.getValueToDistinctFreq().get("unique").intValue());

        Assert.assertEquals(4, soundexFreqIndicator.getValueToFreq().get("mic").intValue());
        Assert.assertEquals(3, soundexFreqIndicator.getValueToFreq().get(empty).intValue());
        Assert.assertEquals(2, soundexFreqIndicator.getValueToFreq().get("machine").intValue());
        Assert.assertEquals(1, soundexFreqIndicator.getValueToFreq().get("unique").intValue());

        // java case
        soundexFreqIndicator = IndicatorsFactory.eINSTANCE.createSoundexFreqIndicator();
        soundexFreqIndicator.reset();
        soundexFreqIndicator.setUsedMapDBMode(false);
        soundexFreqIndicator.handle(empty);
        soundexFreqIndicator.handle("mac");
        soundexFreqIndicator.handle("mic");
        soundexFreqIndicator.handle(empty);
        soundexFreqIndicator.handle("mic");
        soundexFreqIndicator.handle("mac");
        soundexFreqIndicator.handle("machine");
        soundexFreqIndicator.handle(empty);
        soundexFreqIndicator.handle("machine");
        soundexFreqIndicator.handle("unique");

        soundexFreqIndicator.finalizeComputation();
        Assert.assertEquals(false, soundexFreqIndicator.getUniqueValues().isEmpty());
        Assert.assertEquals(1, soundexFreqIndicator.getUniqueValues().size());
        Assert.assertEquals(2, soundexFreqIndicator.getValueToDistinctFreq().get("mic").intValue());
        Assert.assertEquals(0, soundexFreqIndicator.getValueToDistinctFreq().get("Null field").intValue());
        Assert.assertEquals(1, soundexFreqIndicator.getValueToDistinctFreq().get("machine").intValue());
        Assert.assertEquals(1, soundexFreqIndicator.getValueToDistinctFreq().get("unique").intValue());

        Assert.assertEquals(4, soundexFreqIndicator.getValueToFreq().get("mic").intValue());
        Assert.assertEquals(3, soundexFreqIndicator.getValueToFreq().get("Null field").intValue());
        Assert.assertEquals(2, soundexFreqIndicator.getValueToFreq().get("machine").intValue());
        Assert.assertEquals(1, soundexFreqIndicator.getValueToDistinctFreq().get("unique").intValue());
    }

}
