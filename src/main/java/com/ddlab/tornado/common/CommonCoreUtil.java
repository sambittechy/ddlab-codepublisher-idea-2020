/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.common;

import static com.ddlab.tornado.common.CommonConstants.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * The Class CommonCoreUtil.
 */
public class CommonCoreUtil {

	/**
	 * Gets the save file name.
	 *
	 * @return the save file name
	 */
	private static String getSaveFileName() {
		String userHomeDirPath = System.getProperty(USER_HOME);
		File gitpuhserFile = Paths.get(userHomeDirPath, CODEPUBLISHER).toFile();
		if (!gitpuhserFile.exists())
			gitpuhserFile.mkdirs();
		return Paths.get(userHomeDirPath, CODEPUBLISHER, SETTING_FILE_NAME).toString();
	}

	/**
	 * Save or update.
	 *
	 * @param selectedGitType the selected git type
	 * @param userName        the user name
	 * @param password        the password
	 * @param isChecked       the is checked
	 */
	public static void saveOrUpdate(String selectedGitType, String userName, String password, boolean isChecked) {
		DialogSettings settings;
		if (settingsExist()) {
			// It means setting file exists, load the setting and update
			settings = loadSaveSettings();
		} else {
			// It means setting file does not exist, create a new one.
			settings = new DialogSettings(CREDENTIALS);
		}
		IDialogSettings gitSection = settings.addNewSection(selectedGitType);
		String encUserName = CryptoUtil.getEncryptedValue(userName);
		String encPwd = CryptoUtil.getEncryptedValue(password);
		gitSection.put(USER_NAME, encUserName);
		gitSection.put(PASSWORD, encPwd);
		gitSection.put(SAVE, isChecked);
		save(settings);
	}

	/**
	 * Save latest.
	 *
	 * @param selectedGitType the selected git type
	 */
	public static void saveLatest(String selectedGitType) {
		DialogSettings settings;
		if (settingsExist()) {
			// It means setting file exists, load the setting and update
			settings = loadSaveSettings();
		} else {
			// It means setting file does not exist, create a new one.
			settings = new DialogSettings(CREDENTIALS);
		}
		IDialogSettings latestSection = settings.addNewSection(LATEST);
		latestSection.put(LAST_SAVED, selectedGitType);
		save(settings);
	}

	/**
	 * Removes the save settings.
	 *
	 * @param selectedGitType the selected git type
	 */
	public static void removeSaveSettings(String selectedGitType) {
		DialogSettings settings;
		if (settingsExist()) {
			settings = loadSaveSettings();
			settings.removeSection(selectedGitType);
			save(settings);
		}
	}

	/**
	 * Load save settings.
	 *
	 * @return the dialog settings
	 */
	public static DialogSettings loadSaveSettings() {
		DialogSettings settings = new DialogSettings(CREDENTIALS);
		String settingsFileName = getSaveFileName();
		try {
			if (settingsExist())
				settings.load(settingsFileName);
			else
				settings = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return settings;
	}

	/**
	 * Save.
	 *
	 * @param credSettings the cred settings
	 */
	private static void save(DialogSettings credSettings) {
		String filename = getSaveFileName();
		try {
			credSettings.save(filename);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Settings exist.
	 *
	 * @return true, if successful
	 */
	public static boolean settingsExist() {
		File saveFile = new File(getSaveFileName());
		return saveFile.exists();
	}
}
