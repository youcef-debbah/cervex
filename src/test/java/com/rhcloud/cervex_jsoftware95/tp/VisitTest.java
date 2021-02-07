package com.rhcloud.cervex_jsoftware95.tp;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class VisitTest {

    @Test
    public void testConstructor() {
        LocalTime visitTime = LocalTime.of(11, 10);
        Duration serviceDuration = Duration.ofMinutes(30);

        Visit visit = new Visit(1, visitTime, serviceDuration);

        assertEquals(1, visit.getNumber());
        assertEquals(visitTime, visit.getVisitTime());
        assertEquals(serviceDuration, visit.getServiceDuration());
    }

    @Test
    public void testGetNumber() {
        Visit visit = new Visit(2, LocalTime.of(9, 30), Duration.ofMinutes(15));
        assertEquals(2, visit.getNumber());
    }

    @Test
    public void testGetVisitTime() {
        Visit visit = new Visit(1, LocalTime.of(10, 0), Duration.ofHours(1));
        assertEquals(LocalTime.of(10, 0), visit.getVisitTime());
    }

    @Test
    public void testGetServiceDuration() {
        Visit visit = new Visit(3, LocalTime.of(13, 45), Duration.ofMinutes(45));
        assertEquals(Duration.ofMinutes(45), visit.getServiceDuration());
    }

    @Test
    public void testToString() {
        Visit visit = new Visit(4, LocalTime.of(12, 15), Duration.ofMinutes(20));
        String expectedString = "Visit [number=4, visitTime=" + visit.getVisitTime() + ", serviceDuration=" + visit.getServiceDuration() + "]";
        assertEquals(expectedString, visit.toString());
    }
}

