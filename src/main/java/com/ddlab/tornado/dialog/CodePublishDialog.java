/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.dialog;

import com.ddlab.tornado.common.*;
import com.ddlab.tornado.executors.RepoExecutor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static com.ddlab.tornado.common.CommonConstants.*;

/**
 * The type Git push dialog.
 *
 * @author Debadatta Mishra
 */
public class CodePublishDialog extends DialogWrapper {

	/** The git act combo. */
	private ComboBox<String> gitActCombo;

	/** The user name txt. */
	private JBTextField userNameTxt;

	/** The password field. */
	private JBPasswordField passwordField;

	/** The repo btn test and show. */
	private JButton repoBtnTestAndShow;

	/** The repo combo. */
	private ComboBox<String> repoCombo;

	/** The read me txt area. */
	private JTextArea readMeTxtArea;

	/** The short desc txt. */
	private JTextArea shortDescTxt;

	/** The ui desinger. */
	private final UICommonDesigner uiDesinger;

	/** The project. */
	private final Project project;

	/** The selected repo. */
	private final File selectedRepo;

	/** The save credential btn. */
	private JBCheckBox saveCredentialBtn;

	/**
	 * Instantiates a new code publish dialog.
	 *
	 * @param project      the project
	 * @param selectedRepo the selected repo
	 * @param canBeParent  the can be parent
	 */
	public CodePublishDialog(@Nullable Project project, File selectedRepo, boolean canBeParent) {
		super(project, canBeParent);
		setTitle(DLG_TITLE_TXT);
		super.setSize(300, 200);
		this.project = project;
		this.selectedRepo = selectedRepo;
		uiDesinger = new UICommonDesigner();
		init();
	}

	/**
	 * Creates the center panel.
	 *
	 * @return the j component
	 */
	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		JPanel contentPanel = new JPanel(UIUtil.getPanelLayout());
		createGitTypeCombo(contentPanel);
		createUserNameTxt(contentPanel);
		createPasswordTxt(contentPanel);
		createPopulateCombo(contentPanel);
		createShortDescTxt(contentPanel);
		createDescTxt(contentPanel);
		contentPanel.setPreferredSize(new Dimension(500, 300));
		populateSavedData(null);
		addGitComboListener();
		return contentPanel;
	}

	/**
	 * Do validate.
	 *
	 * @return the validation info
	 */
	@Nullable
	@Override
	protected ValidationInfo doValidate() {
		ValidationInfo validationInfo = null;
		String userName = userNameTxt.getText();
		String password = new String(passwordField.getPassword());
		String shortDescStr = shortDescTxt.getText();

		if (userName == null || userName.trim().length() == 0)
			validationInfo = new ValidationInfo(UNAME_NOT_EMPTY_TXT, userNameTxt);
		else if (passwordField.getPassword() == null || password.trim().length() == 0)
			validationInfo = new ValidationInfo(PWD_NOT_EMPTY_TXT, passwordField);
		else if (shortDescStr == null || shortDescStr.trim().length() == 0) {
			validationInfo = new ValidationInfo("Short description cannot be empty or blank", shortDescTxt);
			Border border = BorderFactory.createLineBorder(JBColor.RED);
			shortDescTxt.setBorder(border);
		} else {
			shortDescTxt.setBorder(null);
		}
		return validationInfo;
	}

	/**
	 * Do OK action.
	 */
	@Override
	protected void doOKAction() {
		saveSettings();
		close(1);
		new RepoExecutor(this).createRepo();
	}

	/**
	 * Show info message.
	 *
	 * @param infoMsg the info msg
	 */
	public void showInfoMessage(String infoMsg) {
		SwingUtilities.invokeLater(() -> {
			UIUtil.showInfoBalloon(project, infoMsg, true);
			UIUtil.notifyInfo(infoMsg);
		});
	}

	/**
	 * Show error message.
	 *
	 * @param errorMsg the error msg
	 */
	public void showErrorMessage(String errorMsg) {
		SwingUtilities.invokeLater(() -> {
			setErrorText(errorMsg);
			UIUtil.notifyError(errorMsg);
			UIUtil.showErrorBalloon(project, errorMsg);
		});
	}

	/**
	 * Gets the git act combo.
	 *
	 * @return the git act combo
	 */
	public ComboBox<String> getGitActCombo() {
		return gitActCombo;
	}

	/**
	 * Gets the user name txt.
	 *
	 * @return the user name txt
	 */
	public JBTextField getUserNameTxt() {
		return userNameTxt;
	}

	/**
	 * Gets the password field.
	 *
	 * @return the password field
	 */
	public JBPasswordField getPasswordField() {
		return passwordField;
	}

	/**
	 * Gets the repo combo.
	 *
	 * @return the repo combo
	 */
	public ComboBox<String> getRepoCombo() {
		return repoCombo;
	}

	/**
	 * Gets the read me txt area.
	 *
	 * @return the read me txt area
	 */
	public JTextArea getReadMeTxtArea() {
		return readMeTxtArea;
	}

	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Gets the selected repo.
	 *
	 * @return the selected repo
	 */
	public File getSelectedRepo() {
		return selectedRepo;
	}

	/**
	 * Gets the short desc txt.
	 *
	 * @return the short desc txt
	 */
	public JTextArea getShortDescTxt() {
		return shortDescTxt;
	}

	/**
	 * Creates the git type combo.
	 *
	 * @param contentPanel the content panel
	 */
	private void createGitTypeCombo(JPanel contentPanel) {
		uiDesinger.createGitTypeComboLbl(contentPanel);
		gitActCombo = uiDesinger.getGitTypeCombo(contentPanel);
	}

	/**
	 * Creates the user name txt.
	 *
	 * @param contentPanel the content panel
	 */
	private void createUserNameTxt(JPanel contentPanel) {
		uiDesinger.createUserNameLbl(contentPanel);
		userNameTxt = uiDesinger.getUserNameText(contentPanel);
	}

	/**
	 * Creates the password txt.
	 *
	 * @param contentPanel the content panel
	 */
	private void createPasswordTxt(JPanel contentPanel) {
		uiDesinger.createPasswordLbl(contentPanel);
		passwordField = uiDesinger.getPasswordtext(contentPanel);
		saveCredentialBtn = uiDesinger.getSaveCheckBox(contentPanel);
	}

	/**
	 * Creates the populate combo.
	 *
	 * @param contentPanel the content panel
	 */
	private void createPopulateCombo(JPanel contentPanel) {
		repoBtnTestAndShow = uiDesinger.getTestAndShowBtn(contentPanel, REPO_BTN_TXT);
		repoCombo = uiDesinger.getPopulateCombo(contentPanel);
		addTestBtnActionListener();
	}

	/**
	 * Adds the test btn action listener.
	 */
	private void addTestBtnActionListener() {
		repoBtnTestAndShow.addActionListener((listener) -> {
			if (isCredentialValid()) {
				populateRepos();
			}
		});
	}

	/**
	 * Method to populate the repos.
	 */
	private void populateRepos() {
		repoCombo.removeAllItems();
		new RepoExecutor(this).fetchRepos();
	}

	/**
	 * Checks if is credential valid.
	 *
	 * @return true, if is credential valid
	 */
	private boolean isCredentialValid() {
		boolean validFlag = false;
		String userName = userNameTxt.getText();
		String password = new String(passwordField.getPassword());
		if (UIUtil.isBlankOrNull(userName)) {
			setErrorText(UNAME_NOT_EMPTY_TXT, userNameTxt);
		} else if (UIUtil.isBlankOrNull(password)) {
			setErrorText(PWD_NOT_EMPTY_TXT, passwordField);
		} else {
			setErrorText(null);
			validFlag = true;
		}
		return validFlag;
	}

	/**
	 * Creates the short desc txt.
	 *
	 * @param contentPanel the content panel
	 */
	private void createShortDescTxt(JPanel contentPanel) {
		uiDesinger.createShortDescTxtLbl(contentPanel, "Short description for project");
		shortDescTxt = uiDesinger.getShortDescTxtArea(contentPanel);
		UIUtil.addTextAreaKeyListener(shortDescTxt);
	}

	/**
	 * Creates the desc txt.
	 *
	 * @param contentPanel the content panel
	 */
	private void createDescTxt(JPanel contentPanel) {
		uiDesinger.createDescTxtLbl(contentPanel, READ_ME_INFO_TXT);
		readMeTxtArea = uiDesinger.getDescTxtArea(contentPanel);
		UIUtil.addTextAreaKeyListener(readMeTxtArea);
	}

	/**
	 * Populate saved data.
	 *
	 * @param gitType the git type
	 */
	private void populateSavedData(String gitType) {
		DialogSettings settings = CommonCoreUtil.loadSaveSettings();
		if (settings != null) {
			// Get the latest lastSaved
			IDialogSettings latestSetting = settings.getSection(LATEST);
			gitType = (gitType == null) ? latestSetting.get(LAST_SAVED) : gitType;
			IDialogSettings gitSetting = settings.getSection(gitType);
			int gitSelectIndex = Arrays.asList(GIT_ACCOUNTS).indexOf(gitType);
			gitActCombo.setSelectedIndex(gitSelectIndex);

			if (gitSetting != null) {
				String userName = gitSetting.get(USER_NAME) == null ? "" : gitSetting.get(USER_NAME);
				String password = gitSetting.get(PASSWORD) == null ? "" : gitSetting.get(PASSWORD);
				String decryptedUserName = CryptoUtil.getDecryptedValue(userName);
				String decryptedPassword = CryptoUtil.getDecryptedValue(password);
				userNameTxt.setText(decryptedUserName);
				passwordField.setText(decryptedPassword);
			} else {
				userNameTxt.setText("");
				passwordField.setText("");
			}
		}
	}

	/**
	 * Save settings.
	 */
	private void saveSettings() {
		int selectedGitIndex = gitActCombo.getSelectedIndex();
		String selectedGitType = GIT_ACCOUNTS[selectedGitIndex];
		if (saveCredentialBtn.isSelected()) {
			String userName = userNameTxt.getText();
			String password = new String(passwordField.getPassword());
			CommonCoreUtil.saveLatest(selectedGitType);
			CommonCoreUtil.saveOrUpdate(selectedGitType, userName, password, true);
		} else {
			CommonCoreUtil.removeSaveSettings(selectedGitType);
		}
	}

	/**
	 * Adds the git combo listener.
	 */
	private void addGitComboListener() {
		gitActCombo.addActionListener((listener) -> {
			if (gitActCombo.getSelectedItem() != null) {
				System.out.println("You Selected: " + gitActCombo.getSelectedItem().toString());
				String selectedGitType = gitActCombo.getSelectedItem().toString();
				repoCombo.removeAllItems();
				populateSavedData(selectedGitType);
			}
		});
	}
}
