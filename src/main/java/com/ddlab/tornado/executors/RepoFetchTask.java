/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.executors;

import com.ddlab.gitpusher.core.GitType;
import com.ddlab.gitpusher.core.IGitPusher;
import com.ddlab.gitpusher.core.UserAccount;
import com.ddlab.gitpusher.exception.GenericGitPushException;
import com.ddlab.tornado.dialog.CodePublishDialog;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static com.ddlab.tornado.common.CommonConstants.GIT_ACCOUNTS;
import static com.ddlab.tornado.common.CommonConstants.NO_REPO_AVL_MSG;

/**
 * The type Repo fetch task.
 *
 * @author Debadatta Mishra
 */
public class RepoFetchTask extends Task.Backgroundable {

	/** UserAccount object. */
	private final UserAccount userAccount;

	/** Combox box selected git account type. */
	private final String selectedGitType;

	/** Gist Snippet creation dialog. */
	private final CodePublishDialog gitPushDialog;

	/**
	 * Instantiates a new Repo fetch task.
	 *
	 * @param title          the title
	 * @param canBeCancelled the can be cancelled
	 * @param gitPushDialog  the git push dialog
	 */
	public RepoFetchTask(@Nls(capitalization = Nls.Capitalization.Title) @NotNull String title, boolean canBeCancelled,
			CodePublishDialog gitPushDialog) {
		super(gitPushDialog.getProject(), title, canBeCancelled);
		this.gitPushDialog = gitPushDialog;

		String userName = gitPushDialog.getUserNameTxt().getText();
		String password = new String(gitPushDialog.getPasswordField().getPassword());
		this.userAccount = new UserAccount(userName, password);
		this.selectedGitType = GIT_ACCOUNTS[gitPushDialog.getGitActCombo().getSelectedIndex()];
	}

	/**
	 * Thread's default run method to process operations in background and provides
	 * a basic progress info.
	 *
	 * @param indicator of type {@link ProgressIndicator}
	 */
	@Override
	public void run(@NotNull ProgressIndicator indicator) {
		indicator.setFraction(0.1);
		try {
			IGitPusher gitPusher = GitType.fromString(selectedGitType).getGitPusher(userAccount);
			String[] repos = gitPusher.getExistingRepos();
			TimeUnit.SECONDS.sleep(2);
			indicator.setFraction(0.8);
			if (repos.length != 0) {
				for (String repo : repos)
					this.gitPushDialog.getRepoCombo().addItem(repo);
			} else
				gitPushDialog.showInfoMessage(NO_REPO_AVL_MSG);
			indicator.setFraction(1.0);
		} catch (GenericGitPushException | InterruptedException e) {
			gitPushDialog.showErrorMessage(e.getMessage());
		}
	}
}
