
package com.rhcloud.cervex_jsoftware95.beans;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.entities.Statistic;

@Stateless
public class StatisticManagerBean implements StatisticManager {

	private static Logger log = Logger.getLogger(StatisticManagerBean.class);

	@PersistenceContext(unitName = "cervex")
	EntityManager em;

	@Override
	public void countVisitor(LocalDate date) {
		Date current = Date.valueOf(date);
		try {

			Statistic statistic = em.find(Statistic.class, current);

			if (statistic == null) {
				log.debug("Creating new Statistic Entity for date: " + current);
				statistic = new Statistic();
				statistic.setStatisticID(current);
				statistic.setVisitorsCount(1);
				em.persist(statistic);
			} else {
				log.debug("Incrementing visitors count for date: " + statistic.getStatisticID());
				statistic.setVisitorsCount(statistic.getVisitorsCount() + 1);
				em.merge(statistic);
			}

		} catch (Exception e) {
			throw new EJBException("Failed to increment visitors count for date: " + current, e);
		}
	}

	@Override
	public SortedMap<LocalDate, Integer> getVisitorsStatistic(LocalDate from, LocalDate to) {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);

		if (from.isAfter(to))
			throw new IllegalArgumentException();

		try {
			List<Statistic> l = em.createNamedQuery("Statistic.findByPeriod", Statistic.class)
					.setParameter("from", Date.valueOf(from)).setParameter("to", Date.valueOf(to)).getResultList();
			SortedMap<LocalDate, Integer> data = new TreeMap<>();
			for (Statistic state : l)
				data.put(state.getStatisticID().toLocalDate(), state.getVisitorsCount());
			return data;
		} catch (Exception e) {
			throw new EJBException("Can not retreive Statistics from " + from + " to " + to, e);
		}
	}

	@Override
	public Map<String, Long> getDemandsStatics() {
		try {
			List<DemandStaticRecord> demandRecords = em
					.createNamedQuery("getDemandStaticRecord", DemandStaticRecord.class).getResultList();
			Map<String, Long> map = new HashMap<>();
			for (DemandStaticRecord d : demandRecords) {
				map.put(d.getType(), d.getCount());
			}
			return map;
		} catch (Exception e) {
			throw new EJBException("Error happened during retreiving Demands Statics", e);
		}
	}

	@Override
	public long getTotalDemandsCount() {
		try {
			return em.createNamedQuery("Demand.findCount", Long.class).getSingleResult();
		} catch (Exception e) {
			throw new EJBException("Error during retreiving Demands count", e);
		}
	}

	@Override
	public long getPendingDemandsCount() {
		try {
			return em.createNamedQuery("Demand.findCountPending", Long.class).getSingleResult();
		} catch (Exception e) {
			throw new EJBException("Error during retreiving Pending Demands count ", e);
		}
	}

	@Override
	public long getTotalVisitors() {
		try {
			Long visit = em.createNamedQuery("Statistic.findTotalVisitors", Long.class).getSingleResult();
			if (visit != null)
				return visit;
			else
				return 0;
		} catch (Exception e) {
			throw new EJBException("Error during retreiving Total Visitors Count ", e);
		}
	}

}
