/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.dialog;

import com.ddlab.tornado.common.*;
import com.ddlab.tornado.executors.SnippetExecutor;
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
 * The type Gist snippet dialog.
 *
 * @author Debadatta Mishra
 */
public class SnippetCreateDialog extends DialogWrapper {

	/** The git act combo. */
	private ComboBox<String> gitActCombo;

	/** The user name txt. */
	private JBTextField userNameTxt;

	/** The password field. */
	private JBPasswordField passwordField;

	/** The btn test and show. */
	private JButton btnTestAndShow;

	/** The snippet combo. */
	private ComboBox<String> snippetCombo;

	/** The desc txt area. */
	private JTextArea descTxtArea;

	/** The project. */
	private final Project project;

	/** The ui desinger. */
	private final UICommonDesigner uiDesinger;

	/** The selected file. */
	private final File selectedFile;

	/** The save credential btn. */
	private JBCheckBox saveCredentialBtn;

	/**
	 * Instantiates a new Gist snippet dialog.
	 *
	 * @param project          the project
	 * @param selectedFilePath the selected file path
	 * @param canBeParent      the can be parent
	 */
	public SnippetCreateDialog(@Nullable Project project, File selectedFilePath, boolean canBeParent) {
		super(project, canBeParent);
		setTitle(DLG_TITLE_TXT);
		this.project = project;
		this.selectedFile = selectedFilePath;
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
		createDescTxt(contentPanel);
		contentPanel.setPreferredSize(new Dimension(500, 200));
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
		String descriptionTxt = descTxtArea.getText();
		if (UIUtil.isBlankOrNull(userName))
			validationInfo = new ValidationInfo(UNAME_NOT_EMPTY_TXT, userNameTxt);
		else if (UIUtil.isBlankOrNull(password))
			validationInfo = new ValidationInfo(PWD_NOT_EMPTY_TXT, passwordField);
		else if (UIUtil.isBlankOrNull(descriptionTxt)) {
			validationInfo = new ValidationInfo(GIST_NOT_EMPTY_TXT, descTxtArea);
			Border border = BorderFactory.createLineBorder(JBColor.RED);
			descTxtArea.setBorder(border);
		} else {
			descTxtArea.setBorder(null);
		}
		return validationInfo;
	}

	/**
	 * Method to perform action on click of Ok button.
	 */
	@Override
	protected void doOKAction() {
		saveSettings();
		close(1);
		new SnippetExecutor(this).createGistSnippet();
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
	 * Gets git act combo.
	 *
	 * @return the git act combo
	 */
	public ComboBox<String> getGitActCombo() {
		return gitActCombo;
	}

	/**
	 * Gets user name txt.
	 *
	 * @return the user name txt
	 */
	public JBTextField getUserNameTxt() {
		return userNameTxt;
	}

	/**
	 * Gets password field.
	 *
	 * @return the password field
	 */
	public JBPasswordField getPasswordField() {
		return passwordField;
	}

	/**
	 * Gets snippet combo.
	 *
	 * @return the snippet combo
	 */
	public ComboBox<String> getSnippetCombo() {
		return snippetCombo;
	}

	/**
	 * Gets desc txt area.
	 *
	 * @return the desc txt area
	 */
	public JTextArea getDescTxtArea() {
		return descTxtArea;
	}

	/**
	 * Gets project.
	 *
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Gets selected file.
	 *
	 * @return the selected file
	 */
	public File getSelectedFile() {
		return selectedFile;
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
		btnTestAndShow = uiDesinger.getTestAndShowBtn(contentPanel, GIST_BTN_TXT);
		snippetCombo = uiDesinger.getPopulateCombo(contentPanel);
		addTestBtnActionListener();
	}

	/**
	 * Creates the desc txt.
	 *
	 * @param contentPanel the content panel
	 */
	private void createDescTxt(JPanel contentPanel) {
		uiDesinger.createDescTxtLbl(contentPanel, GIST_LBL_TXT);
		descTxtArea = uiDesinger.getDescTxtArea(contentPanel);
		UIUtil.addTextAreaKeyListener(descTxtArea);
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
			}
		}
	}

	/** Adds the git combo listener. */
	private void addGitComboListener() {
		gitActCombo.addActionListener((listener) -> {
			if (gitActCombo.getSelectedItem() != null) {
				System.out.println("You Selected: " + gitActCombo.getSelectedItem().toString());
				String selectedGitType = gitActCombo.getSelectedItem().toString();
				snippetCombo.removeAllItems();
				populateSavedData(selectedGitType);
			}
		});
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
	 * Listener for test button.
	 */
	private void addTestBtnActionListener() {
		btnTestAndShow.addActionListener((listener) -> {
			if (isCredentialValid()) {
				populateGistSnippets();
			}
		});
	}

	/**
	 * Method to populate Snippets in the combo box.
	 */
	private void populateGistSnippets() {
		snippetCombo.removeAllItems();
		new SnippetExecutor(this).fetchSnippets();
	}
}
