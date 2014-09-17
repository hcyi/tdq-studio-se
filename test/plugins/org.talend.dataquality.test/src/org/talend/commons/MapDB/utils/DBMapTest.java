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
package org.talend.commons.MapDB.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

/**
 * created by talend on Jul 15, 2014 Detailled comment
 * 
 */
public class DBMapTest {

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#hashCode()}.
     */
    @Test
    public void testHashCode() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(dbMap1.hashCode(), dbMap2.hashCode());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(dbMap1.hashCode(), dbMap2.hashCode());
        dbMap1.put("key2", 100l); //$NON-NLS-1$
        Assert.assertNotSame(dbMap1.hashCode(), dbMap2.hashCode());

    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#size()}.
     */
    @Test
    public void testSize() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(0, dbMap1.size());
        Assert.assertEquals(0, dbMap2.size());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(1, dbMap1.size());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(1, dbMap2.size());
        dbMap2.put("key2", 100l); //$NON-NLS-1$
        Assert.assertEquals(2, dbMap2.size());
        dbMap1.put("key2", 100l); //$NON-NLS-1$
        Assert.assertEquals(2, dbMap1.size());
        dbMap1.close();
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap1.isEmpty());
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap1.remove("key1"); //$NON-NLS-1$
        dbMap2.remove("key1"); //$NON-NLS-1$
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.close();
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#clear()}.
     */
    @Test
    public void testClear() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap1.isEmpty());
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap1.clear();
        dbMap2.clear();
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.close();
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#DBMap(DBMapParameter, boolean)} .
     */
    @Test
    public void testDBMapDBMapParameter() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#close()}.
     */
    @Test
    public void testClose() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap2.close();
        Assert.assertTrue(dbMap2.isClosed());
        try {
            dbMap2.put("key2", 100l); //$NON-NLS-1$
        } catch (IllegalAccessError e) {
            // nothing to do
            Assert.assertEquals("already closed", e.getMessage()); //$NON-NLS-1$
        }
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#entrySet()}.
     */
    @Test
    public void testEntrySet() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        for (Entry<String, Long> entry : dbMap2.entrySet()) {
            Assert.assertEquals("key1", entry.getKey()); //$NON-NLS-1$
            Assert.assertEquals(100l, entry.getValue().longValue());
        }
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#containsValue(java.lang.Object)}.
     */
    @Test
    public void testContainsValueObject() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        boolean containsValue = dbMap2.containsValue(100l);
        Assert.assertEquals(true, containsValue);
        containsValue = dbMap2.containsValue(101l);
        Assert.assertEquals(false, containsValue);
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#containsKey(java.lang.Object)}.
     */
    @Test
    public void testContainsKeyObject() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        boolean containsKey = dbMap2.containsKey("key1"); //$NON-NLS-1$
        Assert.assertEquals(true, containsKey);
        containsKey = dbMap2.containsKey("key2"); //$NON-NLS-1$
        Assert.assertEquals(false, containsKey);
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#get(java.lang.Object)}.
     */
    @Test
    public void testGetObject() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Long value = dbMap2.get("key1"); //$NON-NLS-1$
        Assert.assertEquals(100l, value.longValue());
        value = dbMap2.get("key2"); //$NON-NLS-1$
        Assert.assertNull(value);
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#put(java.lang.String, java.lang.Long)}.
     */
    @Test
    public void testPutStringLong() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        try {
            dbMap2.put(null, 100l);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }
        try {
            dbMap2.put("key2", null); //$NON-NLS-1$
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }
        dbMap2.close();
        // BtreeMap
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#remove(java.lang.Object)}.
     */
    @Test
    public void testRemoveObject() {
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        dbMap2.remove("key1"); //$NON-NLS-1$
        Assert.assertEquals(true, dbMap2.isEmpty());
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#putAll(java.util.Map)}.
     */
    @Test
    public void testPutAllMapOfQextendsStringQextendsLong() {
        Map<String, Long> tempMap = new HashMap<String, Long>();
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        DBMap<String, Long> dbMap2 = new DBMap<String, Long>();
        tempMap.put("name1", 1l); //$NON-NLS-1$
        tempMap.put("name2", 2l); //$NON-NLS-1$
        dbMap1.putAll(tempMap);
        dbMap2.putAll(tempMap);
        Assert.assertEquals(1l, dbMap1.get("name1").longValue()); //$NON-NLS-1$
        Assert.assertEquals(2l, dbMap2.get("name2").longValue()); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#keySet()}.
     */
    @Test
    public void testKeySet() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        dbMap1.put("name1", 1l); //$NON-NLS-1$
        dbMap1.put("name2", 2l); //$NON-NLS-1$
        for (String key : dbMap1.keySet()) {
            Assert.assertNotNull(key);
            Assert.assertEquals(true, dbMap1.containsKey(key));
            Assert.assertNotNull(dbMap1.get(key));
        }
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#values()}.
     */
    @Test
    public void testValues() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        dbMap1.put("name1", 1l); //$NON-NLS-1$
        dbMap1.put("name2", 2l); //$NON-NLS-1$
        for (long value : dbMap1.values()) {
            Assert.assertNotNull(value);
            Assert.assertEquals(true, dbMap1.containsValue(value));
        }
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        DBMap dbMap2 = dbMap1;
        DBMap<String, Long> dbMap3 = new DBMap<String, Long>();
        Assert.assertEquals(dbMap1, dbMap2);
        Assert.assertEquals(dbMap1, dbMap3);
        Assert.assertEquals(dbMap2, dbMap3);

        dbMap1.put("name1", 1l); //$NON-NLS-1$
        Assert.assertEquals(dbMap1, dbMap2);
        Assert.assertEquals(false, dbMap3.equals(dbMap1));
        Assert.assertEquals(false, dbMap3.equals(dbMap2));

        dbMap3.put("name1", 1l); //$NON-NLS-1$
        Assert.assertEquals(dbMap1, dbMap2);
        Assert.assertEquals(dbMap1, dbMap3);
        Assert.assertEquals(dbMap2, dbMap3);

    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.DBMap#toString()}.
     */
    @Test
    public void testToString() {
        DBMap<String, Long> dbMap1 = new DBMap<String, Long>();
        Assert.assertEquals("{}", dbMap1.toString()); //$NON-NLS-1$
        dbMap1.put("name1", 1l); //$NON-NLS-1$
        dbMap1.put("name2", 2l); //$NON-NLS-1$
        Assert.assertEquals("{name1=1, name2=2}", dbMap1.toString()); //$NON-NLS-1$
    }

}
