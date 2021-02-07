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

package com.rhcloud.cervex_jsoftware95.util;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;

public class WebKit {

	public static HttpServletRequest getRequest() {
		Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();

		if (request instanceof HttpServletRequest) {
			return (HttpServletRequest) request;
		} else {
			throw new RuntimeException("Sorry the only supported protocol is HTTP");
		}
	}

	public static HttpServletResponse getResponse() {
		Object response = FacesContext.getCurrentInstance().getExternalContext().getResponse();

		if (response instanceof HttpServletResponse) {
			return (HttpServletResponse) response;
		} else {
			throw new RuntimeException("Sorry the only supported protocol is HTTP");
		}
	}

	public static String getOriginalURL(FacesContext context, boolean includeQuery) {
		String originalURL = null;
		Map<String, ?> requestMap = context.getExternalContext().getRequestMap();
		Object forwardServletPath = requestMap.get(RequestDispatcher.FORWARD_SERVLET_PATH);

		if (forwardServletPath != null) {
			originalURL = forwardServletPath.toString();

			if (includeQuery) {
				Object forwardQueryString = requestMap.get(RequestDispatcher.FORWARD_QUERY_STRING);
				if (forwardQueryString != null) {
					originalURL += "?" + forwardQueryString;
				}
			}
		}

		return originalURL;
	}

	public static String getURL(String contextPath, String path, ArrayList<String> parameters) {
		StringBuilder sb = new StringBuilder();
		if (contextPath != null)
			sb.append(contextPath);

		if (path != null)
			sb.append(path);

		if (parameters != null && !parameters.isEmpty()) {
			sb.append("?");
			sb.append(parameters.get(0));
			for (int i = 1; i < parameters.size(); i++) {
				sb.append("&");
				sb.append(parameters.get(i));
			}
		}

		return sb.toString();
	}
	
	public static String buildURI(String path, Map<String, String> parameters) throws URISyntaxException {
		Objects.requireNonNull(path);
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String host = context.getRequestServerName();
		String contextName = context.getRequestContextPath();

		URIBuilder builder = new URIBuilder().setScheme("http").setHost(host).setPath(contextName + path);

		if (parameters != null) {
			for (Entry<String, String> parameter : parameters.entrySet()) {
				builder.setParameter(parameter.getKey(), parameter.getValue());
			}
		}

		return builder.build().toString();
	}

}
