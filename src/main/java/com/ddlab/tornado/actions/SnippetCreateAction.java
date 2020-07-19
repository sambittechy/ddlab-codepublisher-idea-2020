/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.actions;

import static com.ddlab.tornado.common.CommonConstants.CODE_MENU_DESC;
import static com.ddlab.tornado.common.CommonConstants.CODE_MENU_TXT;
import static com.ddlab.tornado.common.PluginIcons.GIT_SNIPPET_ACTION_IMG;

import java.io.File;

import com.ddlab.tornado.dialog.SnippetCreateDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * The type Gist snippet action.
 *
 * @author Debadatta Mishra
 */
public class SnippetCreateAction extends AnAction {

	/** Instantiates a new Gist snippet action. */
	public SnippetCreateAction() {
		super(CODE_MENU_TXT, CODE_MENU_DESC, GIT_SNIPPET_ACTION_IMG);
	}

	/**
	 * Method used to perform an action upon click.
	 *
	 * @param event the event
	 */
	@Override
	public void actionPerformed(AnActionEvent event) {
		VirtualFile virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
		File filePath = new File(virtualFile.getPath());

		SnippetCreateDialog gistSnippetDialog = new SnippetCreateDialog(event.getProject(), filePath, true);
		gistSnippetDialog.show();
		if (gistSnippetDialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
			gistSnippetDialog.getExitCode();
		}
	}

	/**
	 * Method used enable disable menu based upon the condition.
	 *
	 * @param e the {@link AnActionEvent}
	 */
	@Override
	public void update(AnActionEvent e) {
		e.getPresentation().setVisible(false);
		e.getPresentation().setEnabled(false);
		PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
		if (psiFile != null) {
			e.getPresentation().setEnabledAndVisible(true);
		}
	}
}
