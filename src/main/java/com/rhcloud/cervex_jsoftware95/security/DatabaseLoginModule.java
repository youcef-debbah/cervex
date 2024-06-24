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

package com.rhcloud.cervex_jsoftware95.security;

import org.jboss.security.auth.spi.DatabaseServerLoginModule;

/**
 * @author youcef debbah
 */
public class DatabaseLoginModule extends DatabaseServerLoginModule {

    /*
     * (non-Javadoc)
     * @see
     * org.jboss.security.auth.spi.UsernamePasswordLoginModule#validatePassword(java.lang.String,
     * java.lang.String)
     */
    @Override
    protected boolean validatePassword(String inputPassword, String expectedPassword) {
        return SecurityManager.validatePassword(inputPassword, expectedPassword);
    }

}
