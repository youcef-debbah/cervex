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

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.validator.Description;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class ArticleApplyManager implements LocaleChangeListener, Serializable {

	private static final long serialVersionUID = -5673106001604119078L;

	private static final Logger log = Logger.getLogger(ArticleApplyManager.class);

	private static final String CONTROLS;

	static {
		String[] allControls = { "bold", "italic", "underline", "strikethrough", "subscript", "superscript", "font",
				"size", "style", "color", "highlight", "bullets", "numbering", "alignleft", "center", "alignright",
				"justify", "undo", "redo", "rule", "image", "link", "unlink", "cut", "copy", "paste", "pastetext",
				"print", "source", "outdent", "indent", "removeFormat" };

		StringBuilder sb = new StringBuilder();

		for (String control : allControls) {
			sb.append(control);
			sb.append(' ');
		}

		CONTROLS = sb.toString();
	}

	private ResourceBundle msgs;

	private String title;
	private String type;

	private List<SelectItem> articleTypes;
	private LinkedHashSet<File> files = new LinkedHashSet<>(4);

	@Description
	private String description;

	@EJB
	private ArticleManager articleManager;

	@PostConstruct
	public void init() {
		initMsgs();
		initArticleTypes();
		log.info(ArticleApplyManager.class.getName() + " initialized");
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		init();
	}

	private void initMsgs() {
		msgs = MessageBundle.ARTICLE.getResource();
	}

	private void initArticleTypes() {
		articleTypes = new ArrayList<>();
		articleTypes.add(new SelectItem("businessManagement", msgs.getString("businessManagement")));
		articleTypes.add(new SelectItem("administrativeManagement", msgs.getString("administrativeManagement")));
		articleTypes.add(new SelectItem("medicalPracticeManagement", msgs.getString("medicalPracticeManagement")));
		articleTypes.add(new SelectItem("logisticsManagement", msgs.getString("logisticsManagement")));
		articleTypes.add(new SelectItem("other", msgs.getString("other")));
	}

	public String getControls() {
		return CONTROLS;
	}

	public List<SelectItem> getArticleTypes() {
		return articleTypes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LinkedHashSet<File> getFiles() {
		return files;
	}

	public void handleFileUpload(FileUploadEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = event.getComponent().getClientId(context);

		if (files.size() < 4) {
			UploadedFile uploadedFile = event.getFile();

			File file = new File();
			file.setName(uploadedFile.getFileName());
			file.setContents(uploadedFile.getContents());
			file.setSize(uploadedFile.getSize());
			file.setContentType(uploadedFile.getContentType());

			if (!files.contains(file)) {
				files.add(file);
			} else {
				context.addMessage(id, new FacesMessage(msgs.getString("fileAlreadyUploaded")));
			}

		} else {
			context.addMessage(id,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, msgs.getString("manyFilesAlreadyUploaded"), null));
		}
	}

	public String apply() {
		FacesContext context = FacesContext.getCurrentInstance();

		try {
			articleManager.sendDemand(getTitle(), getType(), getDescription(), files);
			context.addMessage("global",
					new FacesMessage(msgs.getString("applySucceeded"), msgs.getString("applySucceededDetail")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			return "business";
		} catch (Exception e) {
			log.info("apply failed");
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR, msgs.getString("applyFailed"),
					msgs.getString("applyFailedDetail")));
			return null;
		}
	}

	@Override
	public String toString() {
		return "ArticleApplyManager [msgs=" + (msgs != null ? msgs.getBaseBundleName() : msgs) + ", title=" + title
				+ ", type=" + type + ", description=" + description + "]";
	}

}
