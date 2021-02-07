package com.rhcloud.cervex_jsoftware95.tp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.faces.context.FacesContext;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StatisticsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private FacesContext mockFacesContext;

    private Statistics statistics;

    @Before
    public void setUp() {
        statistics = new Statistics();
        Mockito.reset(mockFacesContext);
    }

    @Test
    public void testReset() {
        List<Period> expectedPeriods = new ArrayList<>();
        expectedPeriods.add(new Period(Duration.ofMinutes(90), 0.5f, false));
        // ... add remaining periods

        statistics.reset();

        assertEquals(expectedPeriods, statistics.getPeriods());
        assertTrue(statistics.getRecords().isEmpty());
        assertEquals(LocalTime.of(8, 0, 0), statistics.getStartTime());
        assertEquals(LocalTime.MAX, statistics.getEndTime());
        assertEquals(Duration.ofMinutes(90), statistics.getPeriodDuration());
        assertEquals(1.25f, statistics.getClientsRate());
        assertFalse(statistics.isBreakTime());
        assertEquals(Duration.ofMinutes(1), statistics.getMinServiceDuration());
        assertEquals(Duration.ofMinutes(1).plusSeconds(45), statistics.getAverageServiceDuration());
    }

    @Test
    public void testSetStartTimeHour() {
        statistics.setStartTime_hour(10);
        assertEquals(LocalTime.of(10, 0, 0), statistics.getStartTime());
    }

    @Test
    public void testSetStartTimeMinute() {
        statistics.setStartTime_min(30);
        assertEquals(LocalTime.of(8, 30, 0), statistics.getStartTime());
    }

    @Test
    public void testSetStartTimeSecond() {
        statistics.setStartTime_sec(15);
        assertEquals(LocalTime.of(8, 0, 15), statistics.getStartTime());
    }

    @Test
    public void testSetDurationHour() {
        statistics.setDuration_hour(2);
        assertEquals(Duration.ofMinutes(150), statistics.getPeriodDuration());
    }

    @Test
    public void testSetDurationMinute() {
        statistics.setDuration_min(45);
        assertEquals(Duration.ofMinutes(105), statistics.getPeriodDuration());
    }

    @Test
    public void testSetClientsRatePositive() {
        statistics.setClientsRate(2.0f);
        assertEquals(2.0f, statistics.getClientsRate());
    }

    @Test
    public void testSetClientsRateZero() {
        statistics.setClientsRate(0.0f);
        assertEquals(0.0f, statistics.getClientsRate());
    }

    @Test
    public void testSetClientsRateNegative() {
        thrown.expect(Exception.class);
        statistics.setClientsRate(-1.0f);
    }

    @Test
    public void testSetBreakTimeTrue() {
        statistics.setBreakTime(true);
        assertTrue(statistics.isBreakTime());
    }

    @Test
    public void testSetBreakTimeFalse() {
        statistics.setBreakTime(false);
        assertFalse(statistics.isBreakTime());
    }

    @Test
    public void testSetMinServiceDurationMinute() {
        statistics.setMinServiceDuration_min(2);
        assertEquals(Duration.ofMinutes(2), statistics.getMinServiceDuration());
    }
}
