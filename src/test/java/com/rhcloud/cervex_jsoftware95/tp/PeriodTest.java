package com.rhcloud.cervex_jsoftware95.tp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class PeriodTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructorDurationClientsRateBreakTime() {
        Period period = new Period(Duration.ofHours(1), 2.0f, true);

        assertEquals(Duration.ofHours(1), period.getDuration());
        assertEquals(2.0f, period.getClientsRate(), 0.001f);
        assertTrue(period.isBreakTime());
        assertNotNull(period.getId()); // Randomly generated UUID should not be null
    }

    @Test
    public void testConstructorAllParams() {
        LocalTime startTime = LocalTime.of(10, 30);
        Period period = new Period(Duration.ofMinutes(30), 1.5f, false, startTime, "testId");

        assertEquals(Duration.ofMinutes(30), period.getDuration());
        assertEquals(1.5f, period.getClientsRate(), 0.001f);
        assertFalse(period.isBreakTime());
        assertEquals(startTime, period.getStart());
        assertEquals("testId", period.getId());
    }

    @Test
    public void testGetDurationInSeconds() {
        Period period = new Period(Duration.ofMinutes(2), 1.0f, false);

        assertEquals(120, period.getDurationInSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDurationNegative() {
        thrown.expectMessage("duration can not be negative or zero");
        new Period(Duration.ofSeconds(-1), 2.0f, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDurationZero() {
        thrown.expectMessage("duration can not be negative or zero");
        new Period(Duration.ZERO, 2.0f, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetClientsRateNegative() {
        thrown.expectMessage("clientsRate can not be negative");
        new Period(Duration.ofHours(1), -1.0f, false);
    }

    @Test
    public void testSetClientsRateZeroForBreakTime() {
        Period period = new Period(Duration.ofMinutes(30), 2.0f, true);

        period.setBreakTime(true);
        assertEquals(0.0f, period.getClientsRate(), 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNull() {
        thrown.expectMessage("id cannot be null");
        new Period(Duration.ofHours(1), 2.0f, false, LocalTime.now(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetStartNull() {
        thrown.expectMessage("start cannot be null");
        new Period(Duration.ofHours(1), 2.0f, false, null, "testId");
    }

    @Test
    public void testGetEnd() {
        Period period = new Period(Duration.ofMinutes(30), 1.0f, false, LocalTime.of(11, 0), "testId");

        assertEquals(LocalTime.of(11, 30), period.getEnd());
    }

    @Test
    public void testToString() {
        Period period = new Period(Duration.ofHours(2), 3.0f, false);

        String durationString = period.toString();
        assertTrue(durationString.contains("h")); // Should contain hours
    }
}
