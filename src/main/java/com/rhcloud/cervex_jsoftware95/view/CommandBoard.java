/*
 * Copyright (c) 2018 youcef debbah (youcef-kun@hotmail.fr)
 *
 * This file is part of cervex.
 *
 * cervex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cervex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cervex.  If not, see <http://www.gnu.org/licenses/>.
 *
 * created on 2018/03/17
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Past;

import org.apache.log4j.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import com.rhcloud.cervex_jsoftware95.beans.StatisticManager;
import com.rhcloud.cervex_jsoftware95.control.CounterResource;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.VisitorsCounter;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class CommandBoard implements LocaleChangeListener, Serializable {

	private static final long serialVersionUID = 2995181812740976709L;

	private static final Logger log = Logger.getLogger(CommandBoard.class);

	@EJB
	private StatisticManager statisticManager;

	private ResourceBundle generalMsgs;
	private ResourceBundle articleMsgs;
	private BarChartModel visitorsChartModel;
	private PieChartModel appliesChartModel;

	private long totalDemandsCount;
	private long pendingDemandsCount;
	private long totalVisitors;

	private String chartStyle;

	@Past
	private Date visitorsStatFrom;
	@Past
	private Date visitorsStatTo;

	public CommandBoard() {
		visitorsChartModel = new BarChartModel();
		appliesChartModel = new PieChartModel();
		chartStyle = "height: 300px; background: transparent; color: white;";

		setVisitorsStatTo(new Date());

		Calendar fromDate = Calendar.getInstance();
		fromDate.setTime(getVisitorsStatTo());
		fromDate.add(Calendar.DAY_OF_MONTH, -30);
		setVisitorsStatFrom(fromDate.getTime());
	}

	@PostConstruct
	public void init() {
		initStat();
		initMsgs();
		initChartModels();
		log.info(CommandBoard.class.getName() + " initialized");
	}

	private void initMsgs() {
		generalMsgs = MessageBundle.GENERAL.getResource();
		articleMsgs = MessageBundle.ARTICLE.getResource();
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
		initChartModels();
		initStat();
	}

	private void initStat() {
		try {
			totalDemandsCount = statisticManager.getTotalDemandsCount();
		} catch (Exception e) {
			totalDemandsCount = 0;
			Meta.handleInternalError(e);
		}

		try {
			pendingDemandsCount = statisticManager.getPendingDemandsCount();
		} catch (Exception e) {
			pendingDemandsCount = 0;
			Meta.handleInternalError(e);
		}

		try {
			totalVisitors = statisticManager.getTotalVisitors();
		} catch (Exception e) {
			totalVisitors = 0;
			Meta.handleInternalError(e);
		}
	}

	private void initChartModels() {
		initVisitorsChartModel();
		initAppliesChartModel();
	}

	private void initVisitorsChartModel() {
		visitorsChartModel.setTitle(generalMsgs.getString("visitorsPerDay"));
		visitorsChartModel.setShadow(true);
		visitorsChartModel.setExtender("jsoftware95.darkExtender");

		Axis xAxis = visitorsChartModel.getAxis(AxisType.X);
		xAxis.setTickAngle(-50);

		Axis yAxis = visitorsChartModel.getAxis(AxisType.Y);
		yAxis.setLabel(generalMsgs.getString("visitors"));

		updateVisitorsModel();
	}

	public void updateVisitorsModel() {
		Locale clientLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", clientLocale);

		Date to = getVisitorsStatTo();
		LocalDate toDate = (to == null) ? Meta.getCurrentDate() : Meta.toLocalDate(to);

		Date from = getVisitorsStatFrom();
		LocalDate fromDate = (from == null) ? toDate.minusDays(30) : Meta.toLocalDate(from);

		visitorsChartModel.clear();

		if (fromDate.isAfter(toDate)) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					generalMsgs.getString("invalidInterval"), generalMsgs.getString("fromMustBeBeforTO")));
		} else {
			try {
				Map<LocalDate, Integer> stats = statisticManager.getVisitorsStatistic(fromDate, toDate);
				ChartSeries visitors = new ChartSeries();

				Iterator<Entry<LocalDate, Integer>> data = stats.entrySet().iterator();
				LocalDate current = fromDate;
				while (data.hasNext()) {
					Entry<LocalDate, Integer> newData = data.next();
					while ((current = current.plusDays(1)).isBefore(newData.getKey())) {
						visitors.set(formatter.format(current), 0);
					}

					visitors.set(formatter.format(newData.getKey()), newData.getValue());
					current = newData.getKey();
				}

				LocalDate limit = toDate.plusDays(1);
				while ((current = current.plusDays(1)).isBefore(limit)) {
					visitors.set(formatter.format(current), 0);
				}

				visitorsChartModel.addSeries(visitors);
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	private void initAppliesChartModel() {
		appliesChartModel.setTitle(articleMsgs.getString("demandsByType"));
		appliesChartModel.setShowDataLabels(true);
		appliesChartModel.setLegendPosition("s");
		appliesChartModel.setShadow(true);
		appliesChartModel.setExtender("jsoftware95.darkExtender");

		Map<String, Long> statics;

		try {
			statics = statisticManager.getDemandsStatics();
		} catch (Exception e) {
			statics = null;
			Meta.handleInternalError(e);
		}

		if (statics != null) {
			for (Entry<String, Long> entry : statics.entrySet()) {
				String key = entry.getKey();
				String label = articleMsgs.getString(key);

				if (label == null)
					label = key;

				appliesChartModel.set(label, entry.getValue());
			}
		}
	}

	public BarChartModel getVisitorsChartModel() {
		return visitorsChartModel;
	}

	public PieChartModel getAppliesChartModel() {
		return appliesChartModel;
	}

	public void setChartStyle(String chartStyle) {
		this.chartStyle = chartStyle;
	}

	public String getChartStyle() {
		return chartStyle;
	}

	public long getTotalDemandsCount() {
		return totalDemandsCount;
	}

	public long getPendingDemandsCount() {
		return pendingDemandsCount;
	}

	public long getTotalVisitors() {
		return totalVisitors;
	}

	public long getCurrentVisitors() {
		return VisitorsCounter.getCurrentVisitors();
	}

	public String getVisitorsCounterChannel() {
		return CounterResource.COUNTER_CHANNEL;
	}

	public void setVisitorsStatFrom(Date visitorsStatFrom) {
		this.visitorsStatFrom = visitorsStatFrom;
	}

	public Date getVisitorsStatFrom() {
		return visitorsStatFrom;
	}

	public void setVisitorsStatTo(Date visitorsStatTo) {
		this.visitorsStatTo = visitorsStatTo;
	}

	public Date getVisitorsStatTo() {
		return visitorsStatTo;
	}

}
