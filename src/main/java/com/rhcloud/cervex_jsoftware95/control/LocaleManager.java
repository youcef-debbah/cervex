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
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.control;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.log4j.Logger;

/**
 *
 * @author youcef debbah
 */
@Named
@SessionScoped
public class LocaleManager implements Serializable {

	private static final long serialVersionUID = -7848606304114310427L;

	private static Logger log = Logger.getLogger(LocaleManager.class);

	private Locale locale;

	@PostConstruct
	public void init() {
		locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
		log.info("locale manager created with default locale: " + locale);
	}

	public Locale getLocale() {
		return locale;
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	public void setLanguage(String language) {
		locale = new Locale(language);
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		Map<String, Object> viewMap = viewRoot.getViewMap();

		viewRoot.setLocale(locale);
		LocaleChangeEvent event = new LocaleChangeEvent(this, viewRoot.getLocale(), locale);

		for (Object bean : viewMap.values()) {
			if (bean instanceof LocaleChangeListener) {
				LocaleChangeListener changeListener = LocaleChangeListener.class.cast(bean);
				changeListener.installNewLocale(event);
			}
		}
	}

}
