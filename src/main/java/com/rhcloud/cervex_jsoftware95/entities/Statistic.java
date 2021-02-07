
package com.rhcloud.cervex_jsoftware95.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the statistic database table.
 * 
 */
@Entity
@Table(name = "statistic")
@NamedQueries({
	@NamedQuery(name = "Statistic.findAll", query = "SELECT s FROM Statistic s"),
	@NamedQuery(name = "Statistic.findByPeriod",query = "SELECT s FROM Statistic s WHERE s.statisticID BETWEEN :from AND :to"),
	@NamedQuery(name = "Statistic.findTotalVisitors",query = "SELECT SUM(s.visitorsCount) FROM Statistic s")
})
public class Statistic implements Serializable {

    private static final long serialVersionUID = 928504791039114923L;

    @Id
    @Column(unique = true, nullable = false, length = 80)
    private Date statisticID;

    @Column(nullable = false)
    private int visitorsCount;

    public Date getStatisticID() {
	return statisticID;
    }

    public void setStatisticID(Date statisticID) {
	this.statisticID = statisticID;
    }

    public int getVisitorsCount() {
	return visitorsCount;
    }

    public void setVisitorsCount(int visitorsCount) {
	this.visitorsCount = visitorsCount;
    }

    @Override
    public String toString() {
	return "Statistic [statisticID=" + statisticID + ", visitorsCount=" + visitorsCount + "]";
    }

}
