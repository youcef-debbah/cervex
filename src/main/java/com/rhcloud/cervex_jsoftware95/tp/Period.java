package com.rhcloud.cervex_jsoftware95.tp;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class Period implements Serializable {
	private static final long serialVersionUID = -3274653272133374959L;

	private Duration duration;
	private float clientsRate;
	private boolean breakTime;
	private String id;

	private LocalTime start;

	public Period(Duration duration, float clientsRate, boolean breakTime) {
		setDuration(duration);
		setClientsRate(clientsRate);
		setBreakTime(breakTime);
		setId(UUID.randomUUID().toString());
	}

	public Period(Duration duration, float clientsRate, boolean breakTime, LocalTime start, String id) {
		setDuration(duration);
		setClientsRate(clientsRate);
		setBreakTime(breakTime);
		setStart(start);
		setId(id);
	}

	public Duration getDuration() {
		return duration;
	}

	public long getDurationInSeconds() {
		return getDuration().getSeconds();
	}

	public void setDuration(Duration duration) {
		Objects.requireNonNull(duration);
		if (duration.isZero() || duration.isNegative())
			throw new IllegalArgumentException("duration can not be negative or zero");
		this.duration = duration;
	}

	public float getClientsRate() {
		return clientsRate;
	}

	public void setClientsRate(float clientsRate) {
		if (clientsRate < 0)
			throw new IllegalArgumentException("clientsRate can not be negative");
		this.clientsRate = clientsRate;
	}

	public boolean isBreakTime() {
		return breakTime;
	}

	public void setBreakTime(boolean breakTime) {
		this.breakTime = breakTime;
		if (breakTime)
			setClientsRate(0);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	public LocalTime getStart() {
		return start;
	}

	public void setStart(LocalTime start) {
		Objects.requireNonNull(start);
		this.start = start;
	}

	public LocalTime getEnd() {
		return start.plus(duration);
	}

	@Override
	public String toString() {
		return Counter.formatDuration(getDuration());
	}

}
