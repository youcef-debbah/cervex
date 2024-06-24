package com.rhcloud.cervex_jsoftware95.tp;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.rhcloud.cervex_jsoftware95.control.Meta;

@ViewScoped
@Named("statistics")
public class Statistics implements Serializable {
	private static final long serialVersionUID = 7302044831027686162L;

	private List<Period> periods;
	private LinkedList<Visit> visits;
	private List<ServiceRecord> records;

	private Counter counter;

	private int startTime_hour;
	private int startTime_min;
	private int startTime_sec;

	private LocalTime endTime;

	private int duration_hour;
	private int duration_min;
	private float clientsRate;
	private boolean breakTime;

	private int minServiceDuration_min;
	private int minServiceDuration_sec;
	private int averageServiceDuration_min;
	private int averageServiceDuration_sec;

	public Statistics() {
		reset();
	}

	public void reset() {
		periods = new ArrayList<>();

		periods.add(new Period(Duration.ofMinutes(90), 0.5f, false));
		periods.add(new Period(Duration.ofMinutes(60), 1f, false));
		periods.add(new Period(Duration.ofMinutes(60), 1.5f, false));
		periods.add(new Period(Duration.ofMinutes(150), 0f, true));
		periods.add(new Period(Duration.ofMinutes(90), 1f, false));
		periods.add(new Period(Duration.ofMinutes(90), 0.25f, false));

		visits = new LinkedList<>();
		records = new ArrayList<>();

		counter = new Counter();

		startTime_hour = 8;
		startTime_min = 0;
		startTime_sec = 0;

		endTime = LocalTime.MAX;

		duration_hour = 1;
		duration_min = 30;
		clientsRate = 1.25f;
		breakTime = false;

		minServiceDuration_min = 1;
		minServiceDuration_sec = 0;
		averageServiceDuration_min = 1;
		averageServiceDuration_sec = 45;

		refresh();
	}

	public int getStartTime_hour() {
		return startTime_hour;
	}

	public void setStartTime_hour(int startTime_hour) {
		this.startTime_hour = startTime_hour;
	}

	public int getStartTime_min() {
		return startTime_min;
	}

	public void setStartTime_min(int startTime_min) {
		this.startTime_min = startTime_min;
	}

	public int getStartTime_sec() {
		return startTime_sec;
	}

	public void setStartTime_sec(int startTime_sec) {
		this.startTime_sec = startTime_sec;
	}

	public LocalTime getStartTime() {
		return LocalTime.of(startTime_hour, startTime_min, startTime_sec);
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public int getDuration_hour() {
		return duration_hour;
	}

	public void setDuration_hour(int duration_hour) {
		this.duration_hour = duration_hour;
	}

	public int getDuration_min() {
		return duration_min;
	}

	public void setDuration_min(int duration_min) {
		this.duration_min = duration_min;
	}

	public Duration getPeriodDuration() {
		return Duration.ofMinutes(duration_hour * 60 + duration_min).abs();
	}

	public float getClientsRate() {
		return clientsRate;
	}

	public void setClientsRate(float clientsRate) {
		this.clientsRate = clientsRate;
	}

	public boolean isBreakTime() {
		return breakTime;
	}

	public void setBreakTime(boolean breakTime) {
		this.breakTime = breakTime;
	}

	public int getMinServiceDuration_min() {
		return minServiceDuration_min;
	}

	public void setMinServiceDuration_min(int minServiceDuration_min) {
		this.minServiceDuration_min = minServiceDuration_min;
	}

	public int getMinServiceDuration_sec() {
		return minServiceDuration_sec;
	}

	public void setMinServiceDuration_sec(int minServiceDuration_sec) {
		this.minServiceDuration_sec = minServiceDuration_sec;
	}

	public Duration getMinServiceDuration() {
		return Duration.ofSeconds(minServiceDuration_min * 60 + minServiceDuration_sec).abs();
	}

	public int getAverageServiceDuration_min() {
		return averageServiceDuration_min;
	}

	public void setAverageServiceDuration_min(int averageServiceDuration_min) {
		this.averageServiceDuration_min = averageServiceDuration_min;
	}

	public int getAverageServiceDuration_sec() {
		return averageServiceDuration_sec;
	}

	public void setAverageServiceDuration_sec(int averageServiceDuration_sec) {
		this.averageServiceDuration_sec = averageServiceDuration_sec;
	}

	public Duration getAverageServiceDuration() {
		return Duration.ofSeconds(averageServiceDuration_min * 60 + averageServiceDuration_sec).abs();
	}

	public List<Period> getPeriods() {
		return periods;
	}

	public void setPeriods(List<Period> periods) {
		if (periods == null)
			periods = new ArrayList<>();
		this.periods = periods;
	}

	public List<ServiceRecord> getRecords() {
		return records;
	}

	public Counter getCounter() {
		return counter;
	}

	public void addWorkPeriod() {
		try {
			Duration duration = getPeriodDuration();
			if (duration.isZero()) {
				FacesMessage message = new FacesMessage("Illegal input", "Period duration can not be zero");
				FacesContext.getCurrentInstance().addMessage("global", message);
				return;
			}

			if (clientsRate < 0) {
				FacesMessage message = new FacesMessage("Illegal input", "clients rate can not be negative");
				FacesContext.getCurrentInstance().addMessage("global", message);
				return;
			}

			periods.add(new Period(duration, clientsRate, breakTime));
			refresh();
		} catch (Exception e) {
			Meta.handleInternalError(e);
		}
	}

	public void removeWorkPeriod() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		periods.removeIf(period -> period.getId().equals(params.get("period")));
		refresh();
	}

	public void refresh() {
		visits.clear();
		records.clear();
		counter.clear();

		LocalTime startTime = getStartTime();
		LocalTime currentTime = startTime;
		LocalTime endTime = startTime;
		LinkedList<Period> workPeriods = new LinkedList<>();

		for (Period period : periods) {
			if (period != null) {
				period.setStart(currentTime);
				currentTime = currentTime.plus(period.getDuration());
				if (!period.isBreakTime()) {
					workPeriods.add(period);
					endTime = period.getEnd();
				} else {
					generateVisits(workPeriods, startTime);
					startTime = currentTime;
					workPeriods = new LinkedList<>();
				}
			}
		}

		generateVisits(workPeriods, startTime);
		this.endTime = endTime;
		counter.setTotalDuration(Duration.between(getStartTime(), getEndTime()));
		serveVisitors();
	}

	private void generateVisits(LinkedList<Period> periods, LocalTime startTime) {
		if (periods != null && !periods.isEmpty()) {
			checkServiceDurationParameters();

			long totalSeconds = periods.stream().mapToLong(Period::getDurationInSeconds).sum();
			LocalTime endTime = startTime.plusSeconds(totalSeconds);
			LocalTime lastArrivalTime = startTime;

			boolean moreClientsCanArrive = true;
			for (Period period : periods) {
				while (moreClientsCanArrive) {
					long secondsUntilNextArrival = Math.round(expQuantiles(period.getClientsRate()) * 60);
					lastArrivalTime = lastArrivalTime.plusSeconds(secondsUntilNextArrival);

					if (lastArrivalTime.isBefore(endTime)) {
						visits.add(new Visit(visits.size() + 1, lastArrivalTime, generateServiceDuration()));
						counter.newArrivalAfter(secondsUntilNextArrival);
					} else
						moreClientsCanArrive = false;
				}
			}
		}
	}

	private void checkServiceDurationParameters() {
		Duration min = getMinServiceDuration();
		Duration average = getAverageServiceDuration();
		if (average.minus(min).isNegative()) {
			averageServiceDuration_min = minServiceDuration_min;
			averageServiceDuration_sec = minServiceDuration_sec;
			FacesMessage message = new FacesMessage("Illegal input", "Average service duration can not be less than min service duration");
			FacesContext.getCurrentInstance().addMessage("global", message);
		}
	}

	private Duration generateServiceDuration() {
		Duration minServiceDuration = getMinServiceDuration();
		Duration averageExtraDuration = getAverageServiceDuration().minus(minServiceDuration);

		double extraDurationRate = 1d / averageExtraDuration.getSeconds();
		long extraSeconds = Math.round(expQuantiles(extraDurationRate));

		return minServiceDuration.plusSeconds(extraSeconds);
	}

	private void serveVisitors() {
		if (visits != null && !visits.isEmpty()) {
			LocalTime currentTime = getStartTime();
			boolean serveMore = true;

			while (!visits.isEmpty() && serveMore) {
				Visit visit = visits.pop();
				LocalTime visitTime = visit.getVisitTime();

				if (currentTime.isBefore(visitTime)) {
					counter.serverRemainedIdle(Duration.between(currentTime, visitTime));
					currentTime = visitTime;
				}

				if (!currentTime.isAfter(endTime)) {
					Duration serviceDuration = visit.getServiceDuration();
					ServiceRecord record = new ServiceRecord(visit.getNumber(), visitTime, currentTime,
							serviceDuration);
					records.add(record);
					counter.newServiceMade(record);
					currentTime = currentTime.plus(serviceDuration);
				} else {
					serveMore = false;
				}
			}

			while (!visits.isEmpty()) {
				Visit visit = visits.pop();
				ServiceRecord record = new ServiceRecord(visit.getNumber(), visit.getVisitTime(), endTime);
				records.add(record);
				counter.newServiceMade(record);
			}
		}
	}

	private static double expQuantiles(double lambda) {
		double ln = -Math.log(1 - Math.random());

		if (Double.isInfinite(ln))
			ln = 39d;

		return ln / lambda;
	}

}
