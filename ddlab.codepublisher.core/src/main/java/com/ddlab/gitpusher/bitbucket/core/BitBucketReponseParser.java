/*
 * Copyright 2018 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.gitpusher.bitbucket.core;

import com.ddlab.gitpusher.core.IErrorResponseParser;
import com.ddlab.gitpusher.core.IResponseParser;
import com.ddlab.gitpusher.exception.GenericGitPushException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class BitBucketReponseParser.
 *
 * @author Debadatta Mishra
 */
public class BitBucketReponseParser implements IResponseParser<String, String[]> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IResponseParser#getAllRepos(java.lang.Object)
	 */
	@Override
	public String[] getAllRepos(String jsonResponse) throws GenericGitPushException {
		List<String> repoList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(jsonResponse);
			JsonNode valuesNode = rootNode.get("values");
			if (valuesNode.isArray()) {
				for (final JsonNode objNode : valuesNode)
					repoList.add(objNode.get("name").asText());
			}
		} catch (IOException e) {
			throw new GenericGitPushException(e.getMessage());
		}
		return repoList.toArray(new String[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ddlab.gitpusher.core.IResponseParser#getUser(java.lang.Object)
	 */
	@Override
	public String[] getUser(String jsonResponse) throws GenericGitPushException {
		String userName = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			JsonNode rootNode = mapper.readTree(jsonResponse);
			userName = rootNode.get("username").asText();
		} catch (IOException e) {
			throw new GenericGitPushException(e.getMessage());
		}
		return new String[] { userName };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ddlab.gitpusher.core.IResponseParser#getNewlyCreatedHostedRepo(java.lang.
	 * Object, com.ddlab.gitpusher.core.IErrorResponseParser)
	 */
	@Override
	@Deprecated
	public String[] getNewlyCreatedHostedRepo(String jsonResponse, IErrorResponseParser<String, String> errorParser)
			throws GenericGitPushException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ddlab.gitpusher.core.IResponseParser#getAllGistSnippets(java.lang.Object)
	 */
	@Override
	public String[] getAllGistSnippets(String jsonResponse) throws GenericGitPushException {
		List<String> gistSnippetList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			JsonNode rootNode = mapper.readTree(jsonResponse);
			JsonNode valuesNode = rootNode.get("values");
			if (valuesNode.isArray()) {
				for (JsonNode objNode : valuesNode)
					gistSnippetList.add(objNode.get("id").asText());
			}

		} catch (IOException e) {
			throw new GenericGitPushException(e.getMessage());
		}
		return gistSnippetList.toArray(new String[0]);
	}
}
