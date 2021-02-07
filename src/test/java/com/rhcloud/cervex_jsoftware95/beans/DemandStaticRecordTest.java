package com.rhcloud.cervex_jsoftware95.beans;

import org.junit.Test;

import static org.junit.Assert.*;

public class DemandStaticRecordTest {

    @Test
    public void testDemandStaticRecord_constructor_setsValuesCorrectly() {
        String type = "testType";
        long count = 10;

        DemandStaticRecord record = new DemandStaticRecord(type, count);

        assertEquals(type, record.getType());
        assertEquals(count, record.getCount());
    }

    @Test
    public void testDemandStaticRecord_setters_updateValuesCorrectly() {
        DemandStaticRecord record = new DemandStaticRecord("initialType", 5);

        String newType = "updatedType";
        long newCount = 20;

        record.setType(newType);
        record.setCount(newCount);

        assertEquals(newType, record.getType());
        assertEquals(newCount, record.getCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDemandStaticRecord_constructor_throwsException_onNegativeCount() {
        new DemandStaticRecord("testType", -1);
    }

    @Test(expected = NullPointerException.class)
    public void testDemandStaticRecord_constructor_throwsException_onNullType() {
        new DemandStaticRecord(null, 10);
    }

    @Test
    public void testDemandStaticRecord_equals_returnsTrue_forSameObject() {
        DemandStaticRecord record = new DemandStaticRecord("testType", 10);

        assertTrue(record.equals(record));
    }

    @Test
    public void testDemandStaticRecord_equals_returnsTrue_forEqualObjects() {
        DemandStaticRecord record1 = new DemandStaticRecord("testType", 10);
        DemandStaticRecord record2 = new DemandStaticRecord("testType", 10);

        assertTrue(record1.equals(record2));
    }

    @Test
    public void testDemandStaticRecord_equals_returnsFalse_forDifferentType() {
        DemandStaticRecord record1 = new DemandStaticRecord("testType", 10);
        String anotherObject = "not a DemandStaticRecord";

        assertFalse(record1.equals(anotherObject));
    }

    @Test
    public void testDemandStaticRecord_equals_returnsFalse_forDifferentCount() {
        DemandStaticRecord record1 = new DemandStaticRecord("testType", 10);
        DemandStaticRecord record2 = new DemandStaticRecord("testType", 20);

        assertFalse(record1.equals(record2));
    }

    @Test
    public void testDemandStaticRecord_equals_returnsFalse_forDifferentType2() {
        DemandStaticRecord record1 = new DemandStaticRecord("type1", 10);
        DemandStaticRecord record2 = new DemandStaticRecord("type2", 10);

        assertFalse(record1.equals(record2));
    }
}