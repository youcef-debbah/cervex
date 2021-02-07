package com.rhcloud.cervex_jsoftware95.tp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class ServiceRecordTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructorAllParams() {
        LocalTime visitTime = LocalTime.of(10, 15);
        LocalTime serviceTime = LocalTime.of(10, 30);
        Duration serviceDuration = Duration.ofMinutes(15);

        ServiceRecord record = new ServiceRecord(1, visitTime, serviceTime, serviceDuration);

        assertEquals(1, record.getNumber());
        assertEquals("10:15:00", record.getVisitTime());
        assertEquals("10:30:00", record.getServiceTime());
        assertEquals(serviceDuration, record.calcServiceDuration());
        assertEquals("00:15:00", record.getServiceDuration());
        assertEquals("10:45:00", record.getEndServiceTime());
    }

    @Test
    public void testConstructorNotServed() {
        LocalTime visitTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(9, 30);

        ServiceRecord record = new ServiceRecord(2, visitTime, endTime);

        assertEquals(2, record.getNumber());
        assertEquals("09:00:00", record.getVisitTime());
        assertEquals("not served", record.getServiceTime());
        assertEquals(Duration.ZERO, record.calcServiceDuration());
        assertEquals("not served", record.getServiceDuration());
        assertEquals("not served", record.getEndServiceTime());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorAllParamsNullVisitTime() {
        thrown.expectMessage("visitTime cannot be null");
        new ServiceRecord(1, null, LocalTime.now(), Duration.ofMinutes(10));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorAllParamsNullServiceTime() {
        thrown.expectMessage("serviceTime cannot be null");
        new ServiceRecord(1, LocalTime.now(), null, Duration.ofMinutes(10));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorAllParamsNullServiceDuration() {
        thrown.expectMessage("serviceDuration cannot be null");
        new ServiceRecord(1, LocalTime.now(), LocalTime.now(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNotServedNullVisitTime() {
        thrown.expectMessage("visitTime cannot be null");
        new ServiceRecord(1, null, LocalTime.now());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNotServedNullEndTime() {
        thrown.expectMessage("endTime cannot be null");
        new ServiceRecord(1, LocalTime.now(), null);
    }

    @Test
    public void testCalcWaitingDurationServed() {
        LocalTime visitTime = LocalTime.of(11, 0);
        LocalTime serviceTime = LocalTime.of(11, 15);
        Duration serviceDuration = Duration.ofMinutes(10);

        ServiceRecord record = new ServiceRecord(1, visitTime, serviceTime, serviceDuration);

        assertEquals(Duration.ofMinutes(15), record.calcWaitingDuration());
        assertEquals("00:15:00", record.getWaitingDuration());
    }

    @Test
    public void testCalcWaitingDurationNotServed() {
        LocalTime visitTime = LocalTime.of(12, 30);
        LocalTime endTime = LocalTime.of(13, 0);

        ServiceRecord record = new ServiceRecord(2, visitTime, endTime);

        assertEquals(Duration.ofMinutes(30), record.calcWaitingDuration());
        assertEquals("00:30:00", record.getWaitingDuration());
    }

}
