package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Objects;

public enum ArticleState {
	NEW_ARTICLE("new"), UPDATED_ARTICLE("updated"), OLD_ARTICLE("old");
	private String stateName;

	private ArticleState(String stateName) {
		Objects.requireNonNull(stateName);
		this.stateName = stateName;
	}

	public String getStateName() {
		return stateName;
	}

	@Override
	public String toString() {
		return stateName;
	}
}