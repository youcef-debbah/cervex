
package com.rhcloud.cervex_jsoftware95.beans;

import java.time.LocalDate;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.Local;

/**
 * @author KratosPOP
 *
 */
@Local
public interface StatisticManager {

	/**
	 * Increments visitors count by 1
	 */
	public void countVisitor(LocalDate date);

	/**
	 * Returns the total number of counted visitors for each day in the period
	 * between {@code from} and {@code to}.
	 * <p>
	 * Map keys represent days and values are the total visitors count for
	 * correspond day
	 * <p>
	 * days with 0 visitors are not included in the returned map
	 * 
	 * @param from
	 *            The first included day in this statistic
	 * @param to
	 *            The last included day in this statistic
	 * @return total number of visitor per days
	 * @throws NullPointerException
	 *             if either {@code from} or {@code to} are {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code from} represent a day later than {@code to} date
	 * @throws EJBException
	 *             if any other unexpected error occurs
	 */
	public Map<LocalDate, Integer> getVisitorsStatistic(LocalDate from, LocalDate to);

	public Map<String, Long> getDemandsStatics();

	/**
	 * Returns count of all demands
	 * 
	 * @return total number of demands
	 */

	public long getTotalDemandsCount();

	/**
	 * Returns count of demands with no corresponded article
	 * 
	 * @return number of demands that has no article yet
	 */

	public long getPendingDemandsCount();

	/**
	 * Returns the total number of visitors (sum of visitors in all days)
	 * 
	 * @return total visitors count
	 */

	public long getTotalVisitors();

}
