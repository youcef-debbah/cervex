package com.rhcloud.cervex_jsoftware95.tp;

import java.io.Serializable;
import java.time.Duration;

public class Counter implements Serializable {
	private static final long serialVersionUID = -5737592907342751100L;
	private static final String NO_RESULT = "no data";
	static final int SECONDS_PER_MINUTE = 60;
	static final int MINUTES_PER_HOUR = 60;
	static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

	private Duration totalDuration;
	private Duration totalIdleDuration;
	private Duration totalWaitingDuration;
	private Duration totalServiceDuration;
	private Duration totalInterarrivalDuration;

	private int totalCustomers;
	private int servedCustomers;
	private int customersHasToWait;

	public Counter() {
		clear();
	}

	public void clear() {
		totalDuration = Duration.ZERO;
		totalIdleDuration = Duration.ZERO;
		totalWaitingDuration = Duration.ZERO;
		totalServiceDuration = Duration.ZERO;
		totalInterarrivalDuration = Duration.ZERO;
		totalCustomers = 0;
		servedCustomers = 0;
		customersHasToWait = 0;
	}

	public void setTotalDuration(Duration totalDuration) {
		this.totalDuration = totalDuration;
	}

	public void newArrivalAfter(long seconds) {
		totalInterarrivalDuration = totalInterarrivalDuration.plusSeconds(seconds);
	}

	public void newServiceMade(ServiceRecord record) {
		totalServiceDuration = totalServiceDuration.plus(record.calcServiceDuration());
		Duration waitingDuration = record.calcWaitingDuration();

		totalCustomers++;
		if (!record.isNotServed())
			servedCustomers++;

		if (!waitingDuration.isZero()) {
			totalWaitingDuration = totalWaitingDuration.plus(waitingDuration);
			customersHasToWait++;
		}
	}

	public void serverRemainedIdle(Duration idleDuration) {
		totalIdleDuration = totalIdleDuration.plus(idleDuration);
	}

	//////////////////////////////////////////////////////////////////////////

	public static String formatProportion(float proportion) {
		return String.format("%.2f%%", proportion);
	}

	public static String formatDuration(Duration duration) {
		if (duration != null && !duration.isZero()) {
			long seconds = duration.getSeconds();
			long hours = seconds / SECONDS_PER_HOUR;
			int minutes = (int) ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
			int secs = (int) (seconds % SECONDS_PER_MINUTE);

			StringBuilder buf = new StringBuilder();

			if (hours > 0)
				buf.append(hours + " h ");

			if (minutes > 0)
				buf.append(minutes + " min ");

			if (secs > 0)
				buf.append(secs + " sec");

			return buf.toString().trim();
		} else {
			return NO_RESULT;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	
	public int getTotalCustomers() {
		return totalCustomers;
	}

	public String getServedProportion() {
		if (totalCustomers > 0)
			return formatProportion((servedCustomers * 100f) / totalCustomers);
		else
			return NO_RESULT;
	}

	public String getIdleProportion() {
		if (!totalDuration.isZero())
			return formatProportion((totalIdleDuration.getSeconds() * 100f) / totalDuration.getSeconds());
		else
			return NO_RESULT;

	}

	public String getAverageWaitingTime() {
		if (totalCustomers > 0)
			return formatDuration(totalWaitingDuration.dividedBy(totalCustomers));
		else
			return NO_RESULT;
	}

	public String getCustomersHasToWaitProportion() {
		if (totalCustomers > 0)
			return formatProportion((customersHasToWait * 100f) / totalCustomers);
		else
			return NO_RESULT;

	}

	public String getAverageWaitingTimeOfWhoWait() {
		if (customersHasToWait > 0)
			return formatDuration(totalWaitingDuration.dividedBy(customersHasToWait));
		else
			return NO_RESULT;
	}

	public String getAverageDurationBetweenArrivals() {
		if (totalCustomers > 0)
			return formatDuration(totalInterarrivalDuration.dividedBy(totalCustomers));
		else
			return NO_RESULT;
	}

	public String getAverageDurationInSystem() {
		if (totalCustomers > 0)
			return formatDuration(calcTotalDurationInSystem().dividedBy(totalCustomers));
		else
			return NO_RESULT;
	}

	public Duration calcTotalDurationInSystem() {
		return totalServiceDuration.plus(totalWaitingDuration);
	}

	public String getTotalWaitingDuration() {
		return formatDuration(totalWaitingDuration);
	}

	public String getTotalDurationInSystem() {
		return formatDuration(calcTotalDurationInSystem());
	}
}
