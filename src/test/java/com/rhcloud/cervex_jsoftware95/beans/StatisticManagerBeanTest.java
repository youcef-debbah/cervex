package com.rhcloud.cervex_jsoftware95.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

import com.rhcloud.cervex_jsoftware95.entities.Statistic;

public class StatisticManagerBeanTest {

    private StatisticManagerBean statisticManagerBean;
    @PersistenceContext
    private EntityManager em; // Mocked EntityManager

    @Before
    public void setUp() {
        statisticManagerBean = new StatisticManagerBean();
        em = mock(EntityManager.class);
        // Inject mock into bean
        statisticManagerBean.em = em;
    }

    @Test
    public void testCountVisitor_createsNewStatistic() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        statisticManagerBean.countVisitor(date);

        Statistic expectedStatistic = new Statistic();
        expectedStatistic.setStatisticID(Date.valueOf(date));
        expectedStatistic.setVisitorsCount(1);

        verify(em).find(Statistic.class, Date.valueOf(date));
        verify(em).persist(expectedStatistic);
    }

    @Test
    public void testCountVisitor_incrementsExistingStatistic() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Statistic existingStatistic = new Statistic();
        existingStatistic.setStatisticID(Date.valueOf(date));
        existingStatistic.setVisitorsCount(10);
        when(em.find(Statistic.class, Date.valueOf(date))).thenReturn(existingStatistic);

        statisticManagerBean.countVisitor(date);

        existingStatistic.setVisitorsCount(existingStatistic.getVisitorsCount() + 1);
        verify(em).merge(existingStatistic);
    }

    @Test
    public void testCountVisitor_persistenceError_throwsEJBException() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(em.find(Statistic.class, Date.valueOf(date))).thenThrow(new RuntimeException());

        assertThrows(EJBException.class, () -> statisticManagerBean.countVisitor(date));
    }

    @Test
    public void testGetVisitorsStatistic_validDates_returnsData() {
        LocalDate from = LocalDate.of(2023, 12, 1);
        LocalDate to = LocalDate.of(2024, 1, 1);
        List<Statistic> statistics = new ArrayList<>();
        statistics.add(newStat(from, 10));
        statistics.add(newStat(from, 20));
        SortedMap<LocalDate, Integer> expectedData = new TreeMap<>();
        expectedData.put(from, 10);
        expectedData.put(to, 20);

        SortedMap<LocalDate, Integer> actualData = statisticManagerBean.getVisitorsStatistic(from, to);
        assertEquals(expectedData, actualData);
    }

    private Statistic newStat(LocalDate date, int visitors) {
        Statistic statistic = new Statistic();
        statistic.setVisitorsCount(visitors);
        statistic.setStatisticID(Date.valueOf(date));
        return statistic;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetVisitorsStatistic_fromAfterTo_throwsException() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2023, 12, 1);

        statisticManagerBean.getVisitorsStatistic(from, to);
    }
}
