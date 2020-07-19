/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.gitpusher.bitbucket.core;

import static com.ddlab.gitpusher.util.CommonConstants.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.ddlab.gitpusher.core.GitResponse;
import com.ddlab.gitpusher.core.IErrorResponseParser;
import com.ddlab.gitpusher.core.IGitHandler;
import com.ddlab.gitpusher.core.IResponseParser;
import com.ddlab.gitpusher.core.UserAccount;
import com.ddlab.gitpusher.exception.GenericGitPushException;
import com.ddlab.gitpusher.util.HTTPUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class BitBucketHandlerImpl.
 *
 * @author Debadatta Mishra
 */
public class BitBucketHandlerImpl implements IGitHandler {

	/** The user account. */
	private UserAccount userAccount;

	/**
	 * Instantiates a new bit bucket handler impl.
	 *
	 * @param userAccount the user account
	 */
	public BitBucketHandlerImpl(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#getUserName()
	 */
	@Override
	public String getUserName() throws Exception {
		String userName = null;
		String uri = BITBUCKET_API_URI + BITBUCKET_USER_API_URI;
		HttpGet httpGet = HTTPUtil.getHttpGet(uri, userAccount);
		try {
			GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
			if (gitResponse.getStatusCode().equals(HTTP_401))
				throw new GenericGitPushException(GENERIC_LOGIN_ERR_MSG);
			IResponseParser<String, String[]> responseParser = new BitBucketReponseParser();
			userName = (responseParser.getUser(gitResponse.getResponseText()))[0];
		} catch (GenericGitPushException e) {
			throw e;
		}
		return userName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#getAllRepositories()
	 */
	@Override
	public String[] getAllRepositories() throws Exception {
		String[] repoNames = null;
		String uri = BITBUCKET_API_URI + BITBUCKET_ALL_REPO_API_URI;
		String userName = getUserName();
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { userName });
		HttpGet httpGet = HTTPUtil.getHttpGet(uri, userAccount);
		try {
			GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
			if (gitResponse.getStatusCode().equals(HTTP_401))
				throw new GenericGitPushException(GENERIC_LOGIN_ERR_MSG);
			IResponseParser<String, String[]> responseParser = new BitBucketReponseParser();
			repoNames = responseParser.getAllRepos(gitResponse.getResponseText());
		} catch (GenericGitPushException e) {
			throw e;
		}
		return repoNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#repoExists(java.lang.String)
	 */
	@Override
	public boolean repoExists(String repoName) throws Exception {
		boolean existsFlag = false;
		String uri = BITBUCKET_API_URI + BITBUCKET_EXISTING_REPO_API_URI;
		MessageFormat formatter = new MessageFormat(uri);
		String loginUser = getUserName();
		uri = formatter.format(new String[] { loginUser, repoName });
		try {
			HttpGet httpGet = HTTPUtil.getHttpGet(uri, userAccount);
			GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
			if (gitResponse.getStatusCode().equals(HTTP_401))
				throw new GenericGitPushException(GENERIC_LOGIN_ERR_MSG);
			existsFlag = gitResponse.getStatusCode().equals(HTTP_200) ? true : false;
		} catch (GenericGitPushException e) {
			throw e;
		}
		return existsFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ddlab.gitpusher.core.IGitHandler#getUrlFromLocalRepsitory(java.io.File)
	 */
	@Override
	public String getUrlFromLocalRepsitory(File gitDirPath) throws Exception {
		String existingRepoUrl = null;
		Git git = null;
		Repository repository = null;
		try {
			git = Git.open(gitDirPath);
			repository = git.getRepository();
			existingRepoUrl = repository.getConfig().getString(REMOTE, ORIGIN, URL);
		} catch (IOException e) {
			throw new GenericGitPushException(GENERIC_LOCAL_GIT_EXIST);
		}
		return existingRepoUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#isGitDirAvailable(java.io.File)
	 */
	@Override
	public boolean isGitDirAvailable(File gitDirPath) throws Exception {
		boolean isAvailable = false;
		Git git = null;
		try {
			git = Git.open(gitDirPath);
			isAvailable = true;
		} catch (IOException e) {
		} finally {
			if (git != null)
				git.close();
		}
		return isAvailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#createHostedRepo(java.lang.String)
	 */
	@Override
	public void createHostedRepo(String repoName, String projDesc) throws Exception {
		String loginUser = getUserName();
		String jsonRepo = new BitbucketHostedRepo(projDesc).toJson();
		String uri = BITBUCKET_API_URI + BITBUCKET_CREATE_API_URI;
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { loginUser, repoName });
		HttpPost httpPost = HTTPUtil.getHttpPost(uri, userAccount);
		StringEntity jsonBodyRequest = new StringEntity(jsonRepo, ContentType.APPLICATION_JSON);
		httpPost.setEntity(jsonBodyRequest);
		IErrorResponseParser<String, String> iErrorResponseParser = new BitBucketRepoErrorParserImpl();
		try {
			GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpPost);
			if (gitResponse.getStatusCode().equals(HTTP_400)) {
				String errMsg = iErrorResponseParser.parseError(gitResponse.getResponseText());
				throw new GenericGitPushException(errMsg);
			} else if (gitResponse.getStatusCode().equals(HTTP_401))
				throw new GenericGitPushException(GENERIC_LOGIN_ERR_MSG);
			else if (!gitResponse.getStatusCode().equals(HTTP_200)) {
				String errMsg = new MessageFormat(REPO_CREATE_ERR_MSG).format(new String[] { BITBUCKET });
				throw new GenericGitPushException(errMsg);
			}
		} catch (GenericGitPushException e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#clone(java.lang.String,
	 * java.io.File)
	 */
	@Override
	public void clone(String repoName, File dirPath) throws Exception {
		String uri = BITBUCKET_CLONE_API_URI;
		String loginUser = getUserName();
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { loginUser, loginUser, repoName });
		CredentialsProvider crProvider = new UsernamePasswordCredentialsProvider(userAccount.getUserName(),
				userAccount.getPassword());
		Git git = null;
		try {
			git = Git.cloneRepository().setCredentialsProvider(crProvider).setURI(uri).setDirectory(dirPath).call();
		} catch (GitAPIException e) {
			String errMsg = new MessageFormat(CLONE_ERR_MSG).format(new String[] { BITBUCKET });
			throw new GenericGitPushException(errMsg);
		} finally {
			if (git != null)
				git.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#update(java.io.File,
	 * java.lang.String)
	 */
	@Override
	public void update(File cloneDirPath, String message) throws Exception {
		Git git = null;
		RevCommit revisionCommit = null;
		String comitMsg = GENERIC_COMIT_MSG;
		MessageFormat formatter = new MessageFormat(comitMsg);
		String userName = getUserName();
		userName = (userName == null) ? userAccount.getUserName() : userName;
		PersonIdent authorDetails = new PersonIdent(userName, userAccount.getUserName());
		message = (message == null) ? formatter.format(new String[] { userAccount.getUserName() }) : message;
		try {
			git = Git.open(cloneDirPath);
			git.add().addFilepattern(".").call();
			CommitCommand commitCommand = git.commit().setAuthor(authorDetails).setCommitter(authorDetails).setAll(true)
					.setMessage(message);
			revisionCommit = commitCommand.call();

			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userAccount.getUserName(),
					userAccount.getPassword());
			PushCommand pushCommand = git.push();
			pushCommand.setCredentialsProvider(cp).setForce(true).setPushAll();

			Set<RemoteRefUpdate.Status> statusSet = new HashSet<RemoteRefUpdate.Status>();
			Iterable<PushResult> resultIterable = pushCommand.call();
			PushResult pushResult = resultIterable.iterator().next();
			for (final RemoteRefUpdate rru : pushResult.getRemoteUpdates()) {
				RemoteRefUpdate.Status status = rru.getStatus();
				statusSet.add(status);
			}
			if (statusSet.contains(RemoteRefUpdate.Status.OK)) {
				System.out.println("All files pushed to Bitbucket successfully");
			}
		} catch (IOException | GitAPIException e) {
			revertChanges(git, revisionCommit);
			String reasonMsg = ONE_ANOTHER_REPO_ERR;
			throw new GenericGitPushException(e.getMessage() + reasonMsg);
		} finally {
			if (git != null)
				git.close();
		}
	}

	/**
	 * Revert changes.
	 *
	 * @param git            the git
	 * @param revisionCommit the revision commit
	 */
	private void revertChanges(Git git, RevCommit revisionCommit) {
//		System.out.println("Going to revert the changes");
		try {
			git.revert().call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#getGists()
	 */
	@Override
	public String[] getGists() throws Exception {
		String[] gistSnippets = null;
		String uri = BITBUCKET_API_URI + BITBUCKET_GET_OR_CREATE_GIST_API_URI;
		String loginUser = getUserName();
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { loginUser });
		HttpGet httpGet = HTTPUtil.getHttpGet(uri, userAccount);
		try {
			GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
			if (gitResponse.getStatusCode().equals(HTTP_401))
				throw new GenericGitPushException(GENERIC_LOGIN_ERR_MSG);
			IResponseParser<String, String[]> responseParser = new BitBucketReponseParser();
			gistSnippets = responseParser.getAllGistSnippets(gitResponse.getResponseText());
		} catch (GenericGitPushException e) {
			throw e;
		}
		return gistSnippets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IGitHandler#createGist(java.io.File,
	 * java.lang.String)
	 */
	@Override
	public void createGist(File file, String description) throws Exception {
		String uri = BITBUCKET_API_URI + BITBUCKET_GET_OR_CREATE_GIST_API_URI;
		String loginUser = getUserName();
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { loginUser });
		HttpPost httpPost = HTTPUtil.getHttpPost(uri, userAccount);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody(FILE, file);
		builder.addTextBody(TITLE, description);
		httpPost.setEntity(builder.build());

		try {
			GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpPost);
			if (gitResponse.getStatusCode().equals(HTTP_401))
				throw new GenericGitPushException(GENERIC_LOGIN_ERR_MSG);
			if (!gitResponse.getStatusCode().equals(HTTP_201)) {
				String errMsg = new MessageFormat(SNIPPET_CREATE_ERR_MSG).format(new String[] { BITBUCKET });
				throw new GenericGitPushException(errMsg);
			}
		} catch (GenericGitPushException e) {
			throw e;
		}
	}

	/**
	 * The Class BitbucketHostedRepo.
	 *
	 * @author Debadatta Mishra
	 */
	private static class BitbucketHostedRepo {

		/** The scm. */
		@JsonProperty("scm")
		private String scm = "git";

		@JsonProperty("description")
		private String shortDescription;

		@SuppressWarnings("unused")
		public BitbucketHostedRepo(){

		}

		public BitbucketHostedRepo(String shortDescription) {
			this.shortDescription = shortDescription;
		}

		/**
		 * Gets the scm.
		 *
		 * @return the scm
		 */
		@SuppressWarnings("unused")
		public String getScm() {
			return scm;
		}

		/**
		 * Sets the scm.
		 *
		 * @param scm the new scm
		 */
		@SuppressWarnings("unused")
		public void setScm(String scm) {
			this.scm = scm;
		}

		@SuppressWarnings("unused")
		public String getShortDescription() {
			return shortDescription;
		}

		@SuppressWarnings("unused")
		public void setShortDescription(String shortDescription) {
			this.shortDescription = shortDescription;
		}

		/**
		 * To json.
		 *
		 * @return the string
		 */
		public String toJson() {
			ObjectMapper mapper = new ObjectMapper();
			String toJson = null;
			try {
				toJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return toJson;
		}
	}
}
