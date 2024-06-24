package com.rhcloud.cervex_jsoftware95.tp;

import java.time.Duration;
import java.time.LocalTime;

import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "periodConverter")
public class PeriodConverter implements Converter {

	public static final String SEPARATOR = "_";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.equals("null"))
			return null;

		try {
			String[] args = value.split(SEPARATOR, 5);
			boolean breakTime = Boolean.parseBoolean(args[0]);
			float clientsRate = Float.parseFloat(args[1]);
			Duration duration = Duration.ofSeconds(Long.parseLong(args[2]));
			LocalTime start = LocalTime.ofSecondOfDay(Long.parseLong(args[3]));
			return new Period(duration, clientsRate, breakTime, start, args[4]);
		} catch (Exception e) {
			throw new EJBException("could not convert: " + value, e);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return "";

		if (value instanceof Period) {
			Period period = Period.class.cast(value);

			StringBuilder buff = new StringBuilder();

			buff.append(period.isBreakTime());
			buff.append(SEPARATOR);

			buff.append(period.getClientsRate());
			buff.append(SEPARATOR);

			buff.append(period.getDuration().getSeconds());
			buff.append(SEPARATOR);

			buff.append(period.getStart().toSecondOfDay());
			buff.append(SEPARATOR);

			buff.append(period.getId());
			return buff.toString();
		} else {
			throw new EJBException("could not stringfy: " + value);
		}
	}

}
