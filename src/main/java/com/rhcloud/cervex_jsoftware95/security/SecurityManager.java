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

import java.util.Objects;

import org.apache.log4j.Logger;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * @author youcef debbah
 */
public class SecurityManager {

    private static final Logger log = Logger.getLogger(SecurityManager.class);

    public static final String USER_ROLE = "user";
    public static final String ADMIN_ROLE = "admin";
    public static final String USER_DOMAIN = "/user";
    public static final String ADMIN_DOMAIN = "/admin";
    public static final String SECURED_DOMAIN = "/secured";

    public static String hashPassword(String password) {
        Objects.requireNonNull(password);

        Argon2 argon2 = Argon2Factory.create();

        try {
            return argon2.hash(2, 65536, 1, password);
        } catch (Exception e) {
            log.error("error while hashing password: " + password, e);
            return null;
        }
    }

    /**
     * @param inputPassword
     * @param expectedPassword
     * @return
     */
    public static boolean validatePassword(String inputPassword, String expectedPassword) {
        if (inputPassword == null || expectedPassword == null) return false;

        Argon2 argon2 = Argon2Factory.create();
        return argon2.verify(expectedPassword, inputPassword);
    }

}
