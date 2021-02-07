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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.security.StandardPrincipal;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

@Named
@SessionScoped
public class Meta implements Serializable {

	private static final long serialVersionUID = -4201458211474409189L;

	private static final Logger log = Logger.getLogger(Meta.class);

	public static final String FULL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final ZoneId ADMIN_ZONE = ZoneId.of("GMT+1");

	public StandardPrincipal getPrincipal() {
		return (StandardPrincipal) WebKit.getRequest().getUserPrincipal();
	}

	public boolean isPrincipalInUserRole() {
		try {
			return WebKit.getRequest().isUserInRole(SecurityManager.USER_ROLE);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isPrincipalInAdminRole() {
		try {
			return WebKit.getRequest().isUserInRole(SecurityManager.ADMIN_ROLE);
		} catch (Exception e) {
			return false;
		}
	}

	public String getSession() {
		return WebKit.getRequest().getSession(false).toString();
	}

	public String logout() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = WebKit.getRequest();
			Principal principal = request.getUserPrincipal();

			if (principal != null) {
				log.info("disconnecting: " + principal.getName());
				request.logout();
				ResourceBundle msgs = MessageBundle.LOGIN.getResource();
				FacesMessage message = new FacesMessage(msgs.getString("userDisconnected"),
						msgs.getString("seeYouAgain"));
				context.addMessage("global", message);
				context.getExternalContext().getFlash().setKeepMessages(true);
			}

			context.getExternalContext().invalidateSession();

			return "homePage";
		} catch (Exception e) {
			log.error("couldn't logout", e);
			return null;
		}

	}

	public StreamedContent asStreamedContent(File file) {
		return new DefaultStreamedContent(new ByteArrayInputStream(file.getContents()), file.getContentType(),
				file.getName());
	}

	public static void handleInternalError(Exception e) {
		ResourceBundle msgs = MessageBundle.GENERAL.getResource();
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgs.getString("internErrorTitle"),
				msgs.getString("internError"));
		context.addMessage("global", message);
		log.error("internal error: " + e.getMessage());
	}

	public Date getBeginningOfTime() {
		return Date.from(LocalDate.of(2018, 01, 01).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public Date getCurrentTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT);
		SimpleDateFormat simpleFormatter = new SimpleDateFormat(FULL_DATE_TIME_FORMAT);

		try {
			return simpleFormatter.parse(ZonedDateTime.now(ADMIN_ZONE).format(formatter));
		} catch (Exception e) {
			return new Date();
		}
	}

	public static LocalDate getCurrentDate() {
		return LocalDate.now(ADMIN_ZONE);
	}

	public static LocalDate toLocalDate(Date date) {
		Objects.requireNonNull(date);
		return date.toInstant().atZone(Meta.ADMIN_ZONE).toLocalDate();
	}

	public static <T> void broadcastOverViewScop(Class<T> type, Consumer<T> consumer) {
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		Map<String, Object> viewMap = viewRoot.getViewMap();

		for (Object bean : viewMap.values()) {
			if (type.isInstance(bean)) {
				consumer.accept(type.cast(bean));
			}
		}
	}

	public String getFileURI(String fileID) {
		if (fileID == null || fileID.isEmpty())
			return "";
		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put(FileServer.FILE_ID, fileID);
			return WebKit.buildURI(FileServer.FILE_SERVER_PATTERN, parameters);
		} catch (URISyntaxException e) {
			log.error("cannot build URI for file: " + fileID);
			return "";
		}
	}
	
	public String getCommentURI(String commentID) {
		if (commentID == null)
			return null;

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put(PageServer.COMMENT, commentID);
			try {
				parameters.put(PageServer.LANG,
						FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage());
			} catch (Exception e) {
				log.error("could not get locale from view root", e);
			}
			return WebKit.buildURI(PageServer.PAGE_SERVER_PATTERN, parameters);
		} catch (Exception e) {
			log.error("cannot build comment URI for: " + commentID);
			return null;
		}
	}

}
