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
 * created on 2018/01/07
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
*
* @author youcef debbah
*/
@Named
@ApplicationScoped
public class ProxyView implements Serializable {
	
	private static final long serialVersionUID = -1790827338171208100L;
	private String source = "/";
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}

}
