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
 * created on 2018/01/03
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.rhcloud.cervex_jsoftware95.beans.MessageManager;
import com.rhcloud.cervex_jsoftware95.control.Meta;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class MessagingManager implements Serializable {

    private static final long  serialVersionUID		= -3676620186699553904L;
    public static final String SENDER_PARAMETER		= "sender";
    public static final String RECEIVER_PARAMETER	= "receiver";
    public static final String RECEIVERS_LIST_PARAMETER	= "receivers";

    private static final Logger log = Logger.getLogger(MessagingManager.class);

    private String sender;
    private String receiver;
    private String receiverName;

    private List<SelectItem> receivers;

    private String title;
    private String text;

    @EJB
    private MessageManager messageManager;
    
    @PostConstruct
    public void init() {
	Map<String, String[]> parameters = FacesContext.getCurrentInstance().getExternalContext()
		.getRequestParameterValuesMap();

	String[] senderParameter = parameters.get(SENDER_PARAMETER);
	if (senderParameter != null && senderParameter.length > 0) {
	    sender = senderParameter[0];
	}

	String[] receiverParameter = parameters.get(RECEIVER_PARAMETER);
	if (receiverParameter != null && receiverParameter.length > 0) {
	    receiver = receiverParameter[0];

	    if (receiverParameter.length > 1) {
		receiverName = receiverParameter[1];
	    } else {
		receiverName = receiver;
	    }
	}

	String[] receivers_list = parameters.get(RECEIVERS_LIST_PARAMETER);

	if (receivers_list != null && receivers_list.length > 0) {
	    if (receivers_list.length == 1) {
		receiver = receivers_list[0];
		receiverName = receiver;
	    } else {
		receivers = new ArrayList<>();
		for (String receiver : receivers_list) {
		    receivers.add(new SelectItem(receiver));
		}
	    }
	}

	log.info("bean initialized " + toString());
    }

    public String getSender() {
	return sender;
    }

    public void setSender(String sender) {
	this.sender = sender;
    }

    public String getReceiver() {
	return receiver;
    }

    public void setReceiver(String receiver) {
	this.receiver = receiver;
    }

    public String getReceiverName() {
	return receiverName;
    }

    public void setReceiverName(String receiverName) {
	this.receiverName = receiverName;
    }

    public List<SelectItem> getReceivers() {
	return receivers;
    }

    public void setReceivers(List<SelectItem> receivers) {
	this.receivers = receivers;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public void sendMessage() {
	String result = null;
	try {
	    messageManager.sendMessage(getSender(), getReceiver(), getTitle(), getText());
	    result = getTitle();
	    log.info("succeeded to send message:" + getTitle());
	} catch (Exception e) {
	    Meta.handleInternalError(e);
	} finally {
	    RequestContext.getCurrentInstance().closeDialog(result);
	}
    }

    @Override
    public String toString() {
	return "MessagingManager [sender=" + sender + ", receiver=" + receiver + ", title=" + title + ", text=" + text
		+ "]";
    }

}
