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

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.ejb.EJB;
import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.StatisticManager;

public class FacesFilter implements Filter {

	private static Logger log = Logger.getLogger(FacesFilter.class);

	private static final String VISITOR = "visitor";
	private static final int VISITOR_COOKIE_MAX_AGE = 24 * 60 * 60;

	@EJB
	private StatisticManager statisticManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;

			// Skip JSF resources (CSS/JS/Images/etc)
			if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
				// count this visitor
				countVisitor(req, res);
				// set no cache headers
				noCache(res);
			}
		}

		chain.doFilter(request, response);
	}

	private void countVisitor(HttpServletRequest request, HttpServletResponse response) {
		if (newVisitor(request)) {
			ZonedDateTime now = ZonedDateTime.now(Meta.ADMIN_ZONE);
			log.info("new visitor: " + request.getRemoteAddr() + " (" + now + ")");

			LocalDate today = now.toLocalDate();

			try {
				statisticManager.countVisitor(today);
			} catch (Exception e) {
				log.error("could not count visitor", e);
			}

			Cookie cookie = new Cookie(VISITOR, today.toString());
			cookie.setMaxAge(VISITOR_COOKIE_MAX_AGE);
			response.addCookie(cookie);
		}
	}

	private boolean newVisitor(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return true;
		} else {
			for (Cookie cookie : cookies) {
				if (VISITOR.equals(cookie.getName())) {
					String visitDate = cookie.getValue();

					try {
						LocalDate visitLocalDate = LocalDate.parse(visitDate);
						return visitLocalDate.isBefore(LocalDate.now(Meta.ADMIN_ZONE));
					} catch (RuntimeException e) {
						log.error("could not parse visit date: " + visitDate);
						return true;
					}

				}
			}
			return true;
		}
	}

	private void noCache(HttpServletResponse response) {
		// HTTP 1.1
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		// HTTP 1.0
		response.setHeader("Pragma", "no-cache");
		// Proxies
		response.setDateHeader("Expires", 0);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("initializing FacesFilter");
	}

	@Override
	public void destroy() {
		log.info("destroying FacesFilter");
	}

}
