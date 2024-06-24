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

import java.util.EventObject;
import java.util.Locale;

/**
 * 
 * @author youcef debbah
 */
public class LocaleChangeEvent extends EventObject {

    private static final long serialVersionUID = 6510191376751575363L;

    private Locale oldLocale;
    private Locale newLocale;

    /**
     * @param source
     */
    public LocaleChangeEvent(Object source, Locale oldLocale, Locale newLocale) {
	super(source);
	this.oldLocale = oldLocale;
	this.newLocale = newLocale;
    }

    public Locale getOldLocale() {
	return oldLocale;
    }

    public void setOldLocale(Locale oldLocale) {
	this.oldLocale = oldLocale;
    }

    public Locale getNewLocale() {
	return newLocale;
    }

    public void setNewLocale(Locale newLocale) {
	this.newLocale = newLocale;
    }

}
