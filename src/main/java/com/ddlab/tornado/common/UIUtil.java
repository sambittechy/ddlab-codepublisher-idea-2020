/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.common;

import com.ddlab.generator.IGitIgnoreGen;
import com.ddlab.generator.IReadMeGen;
import com.ddlab.generator.gitignore.GitIgnoreGenerator;
import com.ddlab.generator.readme.ReadMeGenerator;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.ddlab.tornado.common.CommonConstants.*;

/**
 * The type Ui util.
 *
 * @author Debadatta Mishra
 */
public class UIUtil {

	/**
	 * Gets panel layout.
	 *
	 * @return the panel layout
	 */
	public static GridBagLayout getPanelLayout() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		return gridBagLayout;
	}

	/**
	 * Gets the balloon builder.
	 *
	 * @param msgType the msg type
	 * @param msg     the msg
	 * @return the balloon builder
	 */
	private static BalloonBuilder getBalloonBuilder(MessageType msgType, String msg) {
		return JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(msg, msgType, null).setCloseButtonEnabled(true)
				.setShadow(true).setHideOnAction(true);
	}

	/**
	 * Gets the popup balloon.
	 *
	 * @param msgType          the msg type
	 * @param msg              the msg
	 * @param isAutoDisposable the is auto disposable
	 * @return the popup balloon
	 */
	private static Balloon getPopupBalloon(MessageType msgType, String msg, boolean isAutoDisposable) {
		long timeInSeconds = 5000L; // 5 seconds
		BalloonBuilder balloonBuilder = getBalloonBuilder(msgType, msg);
		if (isAutoDisposable)
			balloonBuilder.setFadeoutTime(timeInSeconds);

		return balloonBuilder.createBalloon();
	}

	/**
	 * Show error balloon.
	 *
	 * @param project the project
	 * @param errMsg  the err msg
	 */
	public static void showErrorBalloon(Project project, String errMsg) {
		final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
		Balloon errorBalloon = getPopupBalloon(MessageType.ERROR, errMsg, false);
		errorBalloon.show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
	}

	/**
	 * Show info balloon.
	 *
	 * @param project          the project
	 * @param infoMsg          the info msg
	 * @param isAutoDisposable the is auto disposable
	 */
	public static void showInfoBalloon(Project project, String infoMsg, boolean isAutoDisposable) {
		final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
		Balloon errorBalloon = getPopupBalloon(MessageType.INFO, infoMsg, isAutoDisposable);
		errorBalloon.show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
	}

	/**
	 * Notify error.
	 *
	 * @param errorMsg the error msg
	 */
	public static void notifyError(String errorMsg) {
		Notifications.Bus.notify(new Notification(NOTIFY_GRP_ID, ERROR, errorMsg, NotificationType.ERROR));
	}

	/**
	 * Notify info.
	 *
	 * @param infoMsg the info msg
	 */
	public static void notifyInfo(String infoMsg) {
		Notifications.Bus.notify(new Notification(NOTIFY_GRP_ID, INFORMATION, infoMsg, NotificationType.INFORMATION));
	}

	/**
	 * Is blank or null boolean.
	 *
	 * @param str the str
	 * @return the boolean
	 */
	public static boolean isBlankOrNull(String str) {
		return (str == null || str.trim().length() == 0);
	}

	/**
	 * Generate read me file.
	 *
	 * @param selectedFile the selected file
	 * @param description  the description
	 */
	public static void generateReadMeFile(File selectedFile, String description) {
		IReadMeGen readMeGen = new ReadMeGenerator();
		String projectName = selectedFile.getName();
		description = (description == null || description.trim().isEmpty()) ? UPDATED_LATER : description;
		String readMeContents = readMeGen.generateReadMeMdContents(projectName, description, null);
		Path readMePath = Paths.get(selectedFile.getAbsolutePath() + File.separator + README_MD);
		try {
			if (Files.exists(readMePath))
				return;
			Files.write(readMePath, readMeContents.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate git ignore file.
	 *
	 * @param selectedFile the selected file
	 */
	public static void generateGitIgnoreFile(File selectedFile) {
		IGitIgnoreGen gitIgnoreGenerator = new GitIgnoreGenerator();
		String gitIgnoreContents = gitIgnoreGenerator.generateGitIgnoreContents();
		Path gitIgnorePath = Paths.get(selectedFile.getAbsolutePath() + File.separator + GITIGNORE);
		try {
			if (Files.exists(gitIgnorePath))
				return;
			Files.write(gitIgnorePath, gitIgnoreContents.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the text area key listener.
	 *
	 * @param textArea the text area
	 */
	public static void addTextAreaKeyListener(JTextArea textArea) {
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					e.consume();
					KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
				}
				if (e.getKeyCode() == KeyEvent.VK_TAB && e.isShiftDown()) {
					e.consume();
					KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
				}
			}
		});
	}
}
