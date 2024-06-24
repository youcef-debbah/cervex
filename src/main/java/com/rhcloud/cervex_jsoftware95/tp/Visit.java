package com.rhcloud.cervex_jsoftware95.tp;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

public class Visit implements Serializable {
	
	private static final long serialVersionUID = -144654887620587809L;
	
	private int number;
	private LocalTime visitTime;
	private Duration serviceDuration;
	
	public Visit(int number, LocalTime visitTime, Duration serviceDuration) {
		this.number = number;
		this.visitTime = visitTime;
		this.serviceDuration = serviceDuration;
	}

	public int getNumber() {
		return number;
	}

	public LocalTime getVisitTime() {
		return visitTime;
	}

	public Duration getServiceDuration() {
		return serviceDuration;
	}

	@Override
	public String toString() {
		return "Visit [number=" + number + ", visitTime=" + visitTime + ", serviceDuration=" + serviceDuration + "]";
	}

}
