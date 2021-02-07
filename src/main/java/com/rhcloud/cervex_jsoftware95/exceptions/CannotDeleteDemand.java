package com.rhcloud.cervex_jsoftware95.exceptions;

import java.util.List;

public class CannotDeleteDemand extends InformationSystemException {

	private static final long serialVersionUID = 3388762664862270413L;

	private String articleToDelete;
	private List<String> filesToDelete;

	public CannotDeleteDemand(String articleToDelete, List<String> filesToDelete) {
		super("you can't delete this demand, first delete any related article or files");
		this.articleToDelete = articleToDelete;
		this.filesToDelete = filesToDelete;
	}

	public CannotDeleteDemand(String articleToDelete) {
		this(articleToDelete, null);
	}

	public CannotDeleteDemand(List<String> filesToDelete) {
		this(null, filesToDelete);
	}

	public String getArticleToDelete() {
		return articleToDelete;
	}

	public void setArticleToDelete(String articleToDelete) {
		this.articleToDelete = articleToDelete;
	}

	public List<String> getFilesToDelete() {
		return filesToDelete;
	}

	public void setFilesToDelete(List<String> filesToDelete) {
		this.filesToDelete = filesToDelete;
	}
}
