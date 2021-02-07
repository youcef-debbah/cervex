package com.rhcloud.cervex_jsoftware95.tp;

import com.rhcloud.cervex_jsoftware95.tp.Period;
import com.rhcloud.cervex_jsoftware95.tp.PeriodConverter;
import org.junit.Test;

import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class PeriodConverterTest {

    private PeriodConverter converter = new PeriodConverter();
    private FacesContext mockContext = null; // Assuming you don't rely on FacesContext

    @Test
    public void testGetAsObjectValidString() {
        String value = "true_1.5_3600_10_periodId";

        Period period = (Period) converter.getAsObject(mockContext, null, value);

        assertNotNull(period);
        assertTrue(period.isBreakTime());
        assertEquals(1.5f, period.getClientsRate(), 0.001f);
        assertEquals(Duration.ofHours(1), period.getDuration());
        assertEquals(LocalTime.ofSecondOfDay(10), period.getStart());
        assertEquals("periodId", period.getId());
    }

    @Test(expected = EJBException.class)
    public void testGetAsObjectInvalidString() {
        String value = "invalid_string";
        converter.getAsObject(mockContext, null, value);
    }

    @Test
    public void testGetAsStringValidObject() {
        Period period = new Period(Duration.ofMinutes(30), 2.0f, false, LocalTime.of(12, 30), "testPeriod");

        String convertedValue = converter.getAsString(mockContext, null, period);

        assertEquals("false_2.0_1800_7800_testPeriod", convertedValue);
    }

    @Test(expected = EJBException.class)
    public void testGetAsStringInvalidObject() {
        String value = "not a Period object";
        converter.getAsString(mockContext, null, value);
    }
}

