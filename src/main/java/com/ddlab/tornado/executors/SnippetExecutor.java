/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.executors;

import com.ddlab.tornado.dialog.SnippetCreateDialog;
import com.intellij.openapi.progress.ProgressManager;

import static com.ddlab.tornado.common.CommonConstants.CREATE_GIST_MSG;
import static com.ddlab.tornado.common.CommonConstants.FETCH_GIST_MSG;

/**
 * The type Snippet executor.
 *
 * @author Debadatta Mishra
 */
public class SnippetExecutor {

	/** Snippet creation dialog. */
	private final SnippetCreateDialog snippetDialog;

	/**
	 * Instantiates a new Snippet executor.
	 *
	 * @param snippetDialog the snippet dialog
	 */
	public SnippetExecutor(SnippetCreateDialog snippetDialog) {
		this.snippetDialog = snippetDialog;
	}

	/** Fetch snippets. */
	public void fetchSnippets() {
		GistSnippetFetchTask gistSnippetFetchTask = new GistSnippetFetchTask(FETCH_GIST_MSG, true, snippetDialog);
		ProgressManager.getInstance().run(gistSnippetFetchTask);
	}

	/** Create gist snippet. */
	public void createGistSnippet() {
		GistSnippetCreateTask gistSnippetCreateTask = new GistSnippetCreateTask(CREATE_GIST_MSG, true, snippetDialog);
		ProgressManager.getInstance().run(gistSnippetCreateTask);
	}
}
