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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.indicators.WellFormNationalPhoneCountIndicator;

/**
 * test for WellFormNationalPhoneCountIndicatorImpl class.
 */
public class WellFormNationalPhoneCountIndicatorImplTest {

    private WellFormNationalPhoneCountIndicator wellFormNationalPhoneCountIndicator;

    // test data
    private Object data[] = { "+41446681800", "+041446681800", "+044 668 18 00", "86 18611281173", "+8613521588310", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "086 13693273494", "+08618611281173", "+86 18611281173", "+41 44 668 18 00", "041 44 668 18 00", "+86 13521588310", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "044 668 18 00", "446681800", "186 1128 1173", "134 8877 8542", "158 1061 0794", "1581061 0794", null }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 

    /**
     * setUp method.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        wellFormNationalPhoneCountIndicator = new WellFormNationalPhoneCountIndicatorImpl();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.indicators.impl.WellFormNationalPhoneCountIndicatorImpl#handle(java.lang.Object)}.
     * 
     * note: this test depends on the local language, so when run on local computer maybe get a error result like expected:<1>
     * but was:<4>. but on the test server is right.so no need to modify this test.
     */
    @Test
    public void testHandle() {
        for (Object obj : data) {
            this.wellFormNationalPhoneCountIndicator.handle(obj);
        }
        Assert.assertEquals(1, wellFormNationalPhoneCountIndicator.getWellFormNatiPhoneCount().intValue());
    }

}