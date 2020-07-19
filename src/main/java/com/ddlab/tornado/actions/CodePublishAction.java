/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.actions;

import com.ddlab.tornado.dialog.CodePublishDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

import static com.ddlab.tornado.common.CommonConstants.PROJECT_MENU_DESC;
import static com.ddlab.tornado.common.CommonConstants.PROJECT_MENU_TXT;
import static com.ddlab.tornado.common.PluginIcons.GIT_PUSH_ACTION_IMG;

/**
 * The type Git push action.
 *
 * @author Debadatta Mishra
 */
public class CodePublishAction extends AnAction {

	/** Instantiates a new Git push action. */
	public CodePublishAction() {
		super(PROJECT_MENU_TXT, PROJECT_MENU_DESC, GIT_PUSH_ACTION_IMG);
	}

	/**
	 * Method used perform an action upon clicking on a menu.
	 *
	 * @param anActionEvent the an action event
	 */
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
		VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
		File selectedFile = new File(virtualFile.getPath());

		CodePublishDialog gitPushDialog = new CodePublishDialog(project, selectedFile, true);
		gitPushDialog.show();
		if (gitPushDialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
			gitPushDialog.getExitCode();
		}
	}

	/**
	 * Method used to enable or disable menu based upon conditions.
	 *
	 * @param e the {@link AnActionEvent}
	 */
	@Override
	public void update(AnActionEvent e) {
		e.getPresentation().setVisible(false);
		e.getPresentation().setEnabled(false);
		VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
		if (virtualFile != null) {
			File filePath = new File(virtualFile.getPath());
			if (filePath.isDirectory()) {
				e.getPresentation().setEnabledAndVisible(true);
			} else
				e.getPresentation().setEnabledAndVisible(false);
		}
	}
}
