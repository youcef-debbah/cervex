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
 * created on 2018/01/06
 * @header
 */

package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;

import javax.ejb.Local;

import com.rhcloud.cervex_jsoftware95.entities.Message;

/**
 * 
 * @author youcef debbah
 */
@Local
public interface MessageManager {

    public void sendMessage(String senderUsername, String receiverUsername, String title, String text);

    public List<Message> getMessagesFor(String userID);

    public void markAsRead(String messageID);
}
