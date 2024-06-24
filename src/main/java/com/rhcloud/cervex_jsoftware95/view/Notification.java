package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Supplier;

import com.rhcloud.cervex_jsoftware95.control.Meta;

public class Notification implements Comparable<Notification>, Serializable {

	private static final long serialVersionUID = -967315181475158762L;

	private String title;
	private String contents;
	private String icon;
	private Date date;
	private Runnable remover;
	private Runnable updater;
	private Supplier<String> outcomeSupplier;

	public Notification(String title) {
		this.title = title;
	}

	public Notification(String title, Runnable remover) {
		this(title);
		this.remover = remover;
	}

	public Notification(String title, String icon, Runnable remover) {
		this(title, remover);
		this.icon = icon;
	}

	public Notification(String title, String contents, String icon, Runnable remover) {
		this(title, icon, remover);
		this.contents = contents;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Runnable getRemover() {
		return remover;
	}

	public void setRemover(Runnable remover) {
		this.remover = remover;
	}

	public void remove() {
		try {
			if (remover != null) {
				remover.run();
				update();
			}
		} catch (Exception e) {
			Meta.handleInternalError(e);
		}
	}

	public void setUpdater(Runnable updater) {
		this.updater = updater;
	}

	public Runnable getUpdater() {
		return updater;
	}

	public void update() {
		if (updater != null)
			updater.run();
	}

	public Supplier<String> getOutcomeSupplier() {
		return outcomeSupplier;
	}

	public void setOutcomeSupplier(Supplier<String> outcomeSupplier) {
		this.outcomeSupplier = outcomeSupplier;
	}

	public String outcome() {
		if (outcomeSupplier != null) {
			return outcomeSupplier.get();
		} else {
			return null;
		}
	}

	@Override
	public int compareTo(Notification notification) {
		Date anotherDate = notification.getDate();
		if (date != null && anotherDate != null) {
			return -date.compareTo(anotherDate);
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Notification [title=" + title + ", contents=" + contents + ", date=" + date + "]";
	}

}
