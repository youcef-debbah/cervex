package com.rhcloud.cervex_jsoftware95.tp;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class ServiceRecord implements Serializable {
	private static final long serialVersionUID = -2198806021253110096L;

	private int number;
	private LocalTime visitTime;
	private LocalTime serviceTime;
	private Duration serviceDuration;

	private boolean notServed;
	private LocalTime endTime;

	public ServiceRecord(int number, LocalTime visitTime, LocalTime serviceTime, Duration serviceDuration) {
		Objects.requireNonNull(visitTime);
		Objects.requireNonNull(serviceTime);
		Objects.requireNonNull(serviceDuration);

		this.number = number;
		this.visitTime = visitTime;
		this.serviceTime = serviceTime;
		this.serviceDuration = serviceDuration;
	}

	public ServiceRecord(int number, LocalTime visitTime, LocalTime endTime) {
		Objects.requireNonNull(visitTime);
		Objects.requireNonNull(endTime);

		this.number = number;
		this.visitTime = visitTime;

		notServed = true;
		this.endTime = endTime;
	}

	public int getNumber() {
		return number;
	}

	public String getVisitTime() {
		return visitTime.toString();
	}

	public String getServiceTime() {
		if (notServed)
			return "not served";
		else
			return serviceTime.toString();
	}

	public Duration calcServiceDuration() {
		if (notServed)
			return Duration.ZERO;
		else
			return serviceDuration;
	}

	public String getServiceDuration() {
		if (notServed)
			return "not served";
		else
			return Counter.formatDuration(serviceDuration);
	}

	public String getEndServiceTime() {
		if (notServed)
			return "not served";
		else
			return serviceTime.plus(serviceDuration).toString();
	}

	public Duration calcWaitingDuration() {
		if (notServed)
			return Duration.between(visitTime, endTime);
		else
			return Duration.between(visitTime, serviceTime);
	}

	public String getWaitingDuration() {
		return Counter.formatDuration(calcWaitingDuration());
	}

	public Duration calcTotalDurationInsideSystem() {
		if (notServed)
			return Duration.between(visitTime, endTime);
		else
			return Duration.between(visitTime, serviceTime).plus(serviceDuration);
	}

	public String getTotalDurationInsideSystem() {
		return Counter.formatDuration(calcTotalDurationInsideSystem());
	}
	
	public boolean isNotServed() {
		return notServed;
	}

	@Override
	public String toString() {
		return "ServiceRecord [number=" + number + ", visitTime=" + visitTime + ", serviceTime=" + serviceTime
				+ ", serviceDuration=" + serviceDuration + "]";
	}

}
