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

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.util.SmartKit;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class newPostView implements Serializable {

	private static final long serialVersionUID = 3518263758804288051L;

	private static final Logger log = Logger.getLogger(newPostView.class);

	private enum PartsToUpdate {
		ALL(true, true, true, true), ONLY_INTRO(false, true, false, false), ONLY_CONTENT(false, false, true,
				false), ONLY_THUMBNAIL(false, false, false, true), NOHTING(true, true, true, true);
		private boolean updateTitle, updateIntro, updateContent, updateThumbnail;

		private PartsToUpdate(boolean updateTitle, boolean updateIntro, boolean updateContent,
				boolean updateThumbnail) {
			this.updateTitle = updateTitle;
			this.updateIntro = updateIntro;
			this.updateContent = updateContent;
			this.updateThumbnail = updateThumbnail;
		}

		public boolean isUpdateTitle() {
			return updateTitle;
		}

		public boolean isUpdateIntro() {
			return updateIntro;
		}

		public boolean isUpdateContent() {
			return updateContent;
		}

		public boolean isUpdateThumbnail() {
			return updateThumbnail;
		}
	}

	@Size(max = 200)
	private String title;

	@Size(max = 5000)
	private String intro;

	@Size(max = 10000)
	private String content;

	private File thumbnail;

	private String defaultTitle;
	private String defaultIntro;

	@EJB
	private BlogManager blogManager;

	private String draftID;

	private ResourceBundle blogMsgs;
	private ResourceBundle generalMsgs;

	private String postedBlog;

	private LinkedHashSet<File> files = new LinkedHashSet<>();

	@PostConstruct
	public void init() {
		blogMsgs = MessageBundle.BLOG.getResource();
		generalMsgs = MessageBundle.GENERAL.getResource();

		defaultTitle = blogMsgs.getString("defaultTitle");
		defaultIntro = blogMsgs.getString("defaultIntro");

		title = defaultTitle;
		intro = defaultIntro;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LinkedHashSet<File> getFiles() {
		return files;
	}

	public void setFiles(LinkedHashSet<File> files) {
		this.files = files;
	}

	public String formatSize(long bytes) {
		return SmartKit.formatSize(bytes, generalMsgs.getString("byteSymbol"));
	}

	public void handleFileUpload(FileUploadEvent event) {
		boolean draftExist = draftID != null;
		try {
			partialSave("draftAddedAuto", PartsToUpdate.NOHTING);
			UploadedFile file = event.getFile();
			File savedFile = blogManager.addFile(file.getFileName(), file.getContentType(), file.getSize(),
					file.getContents(), draftID);
			files.add(savedFile);

			if (draftExist)
				FacesContext.getCurrentInstance().addMessage("global",
						new FacesMessage(blogMsgs.getString("draftSaved"), blogMsgs.getString("fileUploaded")));
		} catch (Exception e) {
			Meta.handleInternalError(e);
		}
	}

	public void handleThumbnailUpload(FileUploadEvent event) {
		try {
			UploadedFile uploadedFile = event.getFile();

			File thumbnail = new File();
			thumbnail.setName(uploadedFile.getFileName());
			thumbnail.setContentType(uploadedFile.getContentType());
			thumbnail.setContents(uploadedFile.getContents());
			thumbnail.setSize(uploadedFile.getSize());

			setThumbnail(thumbnail);
		} catch (Exception e) {
			Meta.handleInternalError(e);
		}
	}

	public void removeFile(String fileID) {
		if (fileID != null && !fileID.isEmpty() && files != null) {
			try {
				blogManager.removeFile(fileID);
				files.removeIf(file -> fileID.equals(file.getfileID()));
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	public void removeThumbnail() {
		if (thumbnail != null) {
			try {
				if (draftID != null)
					blogManager.removeThumbnail(draftID);
				thumbnail = null;
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	public void saveDraft() {
		boolean newDraft = Objects.isNull(draftID);

		try {
			save(true, PartsToUpdate.ALL);
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}

		FacesContext.getCurrentInstance().addMessage("global", new FacesMessage(blogMsgs.getString("draftSaved"),
				blogMsgs.getString(newDraft ? "draftAdded" : "draftUpdated")));
	}

	private void partialSave(String message, PartsToUpdate parts) {
		Objects.requireNonNull(message);
		Objects.requireNonNull(parts);

		if (draftID == null) {
			try {
				save(true, parts);
			} catch (Exception e) {
				Meta.handleInternalError(e);
				return;
			}

			FacesContext.getCurrentInstance().addMessage("global",
					new FacesMessage(blogMsgs.getString("draftSaved"), blogMsgs.getString(message)));
		} else if (parts != PartsToUpdate.NOHTING) {
			try {
				save(true, parts);
			} catch (Exception e) {
				Meta.handleInternalError(e);
				return;
			}

			FacesContext.getCurrentInstance().addMessage("global", new FacesMessage(blogMsgs.getString("draftSaved"),
					blogMsgs.getString((parts != PartsToUpdate.ONLY_THUMBNAIL) ? "draftUpdated" : "fileUploaded")));
		}
	}

	public void saveIntro() {
		partialSave("draftAdded", PartsToUpdate.ONLY_INTRO);
	}

	public void saveMain() {
		partialSave("draftAdded", PartsToUpdate.ONLY_CONTENT);
	}

	public File getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(File newThumbnail) {
		thumbnail = newThumbnail;

		if (thumbnail != null)
			partialSave("draftAddedAuto", PartsToUpdate.ONLY_THUMBNAIL);
		if (draftID == null || thumbnail == null || thumbnail.getfileID() == null || thumbnail.getfileID().isEmpty()) {
			this.thumbnail = null;
			if (draftID != null)
				blogManager.removeThumbnail(draftID);
		}
	}

	private void save(boolean isDraft, PartsToUpdate parts) {
		Objects.requireNonNull(parts);

		String title = getTitle();
		if (title == null || title.isEmpty())
			title = defaultTitle;

		String intro = getIntro();
		if (intro == null || intro.isEmpty())
			intro = defaultIntro;

		if (draftID != null) {
			try {
				Blog draft = blogManager.getBlog(draftID);

				if (parts.isUpdateTitle())
					draft.setTitle(title);
				if (parts.isUpdateIntro())
					draft.setIntro(intro);
				if (parts.isUpdateContent())
					draft.setContent(getContent());

				draft.setDraft(isDraft ? Blog.DRAFT : Blog.POSTED);
				blogManager.updateBlog(draft);

				if (parts.isUpdateThumbnail()) {
					try {
						blogManager.removeThumbnail(draftID);
					} catch (Exception e) {
						Meta.handleInternalError(e);
					}

					try {
						thumbnail = blogManager.setThumbnail(thumbnail.getName(), thumbnail.getContentType(),
								thumbnail.getSize(), thumbnail.getContents(), draft.getBlogID());
					} catch (Exception e) {
						Meta.handleInternalError(e);
					}
				}

				if (!isDraft) {
					setPostedBlog(draftID);
				}
			} catch (Exception e) {
				LinkedHashSet<File> uploadedFiles = files;
				File uploadedThumbnail = thumbnail;
				log.error("cannot update blog (trying to delete and upload new one instead): " + draftID);
				deleteBlog();

				draftID = blogManager.createBlog(title, intro, getContent(), uploadedThumbnail, isDraft);
				thumbnail = uploadedThumbnail;

				if (!isDraft) {
					setPostedBlog(draftID);
				}

				boolean fileMissing = false;
				if (uploadedFiles != null) {
					for (File file : uploadedFiles) {
						try {
							File savedFile = blogManager.addFile(file.getName(), file.getContentType(), file.getSize(),
									file.getContents(), draftID);
							files.add(savedFile);
						} catch (Exception cannotAddFile) {
							fileMissing = true;
							log.error("could not add file: " + file + ", to blog: " + draftID);
						}
					}
				}

				if (fileMissing) {
					FacesContext.getCurrentInstance().addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
							blogMsgs.getString("filesLost"), blogMsgs.getString("filesLostDetail")));
				}
			}
		} else {
			draftID = blogManager.createBlog(title, intro, getContent(), thumbnail, isDraft);
			if (!isDraft) {
				setPostedBlog(draftID);
			}
		}
	}

	private void deleteBlog() {
		blogManager.deleteBlog(draftID);
		draftID = null;
		files = new LinkedHashSet<>();
		thumbnail = null;
		postedBlog = null;
	}

	public String getPostedBlog() {
		return postedBlog;
	}

	public void setPostedBlog(String postedBlog) {
		this.postedBlog = postedBlog;
	}

	public String post() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (getThumbnail() == null) {
			context.addMessage("postForm:thumbnail", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					blogMsgs.getString("noThumbnail"), blogMsgs.getString("noThumbnailDetaill")));
			return null;
		}

		try {
			save(false, PartsToUpdate.ALL);
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return null;
		}

		context.addMessage("global", new FacesMessage(blogMsgs.getString("newPostPublished")));
		context.getExternalContext().getFlash().setKeepMessages(true);

		return "blog";
	}

	public void delete() {
		if (draftID != null) {
			try {
				deleteBlog();
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	public String getDraftID() {
		return draftID;
	}

	public void setDraftID(String draftID) {
		postedBlog = null;
		try {
			this.draftID = draftID;
			Blog draft = blogManager.getDraft(draftID);
			files = new LinkedHashSet<>(draft.getAttachedFiles());
			thumbnail = draft.getThumbnail();
			title = draft.getTitle();
			intro = draft.getIntro();
			content = draft.getContent();
		} catch (Exception e) {
			this.draftID = null;
			files = new LinkedHashSet<>();
			thumbnail = null;
			title = null;
			intro = null;
			content = null;
			Meta.handleInternalError(e);
		}
	}
}
