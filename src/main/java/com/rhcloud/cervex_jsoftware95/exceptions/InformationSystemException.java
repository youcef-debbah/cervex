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

package com.rhcloud.cervex_jsoftware95.exceptions;

import javax.ejb.ApplicationException;

/**
 * This Exception indicate some confuse in the underlying information system.
 * For example the database is empty or does not have enough rows to perform the
 * expected operation.
 * 
 * @author youcef debbah
 */
@ApplicationException(rollback = true)
public class InformationSystemException extends RuntimeException {
	private static final long serialVersionUID = -8878988649749538539L;

	public InformationSystemException() {
		super();
	}

	public InformationSystemException(String message) {
		super(message);
	}

	/**
	 * @param string
	 * @param e
	 */
	public InformationSystemException(String message, Exception e) {
		super(message, e);
	}

}
