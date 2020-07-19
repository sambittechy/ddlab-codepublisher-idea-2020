/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.common;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * The Interface IDialogSettings.
 */
public interface IDialogSettings {

	/**
	 * Adds the new section.
	 *
	 * @param name the name
	 * @return the i dialog settings
	 */
	IDialogSettings addNewSection(String name);

	/**
	 * Adds the section.
	 *
	 * @param section the section
	 */
	void addSection(IDialogSettings section);

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	String get(String key);

	/**
	 * Gets the array.
	 *
	 * @param key the key
	 * @return the array
	 */
	String[] getArray(String key);

	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	boolean getBoolean(String key);

	/**
	 * Gets the double.
	 *
	 * @param key the key
	 * @return the double
	 * @throws NumberFormatException the number format exception
	 */
	double getDouble(String key) throws NumberFormatException;

	/**
	 * Gets the float.
	 *
	 * @param key the key
	 * @return the float
	 * @throws NumberFormatException the number format exception
	 */
	float getFloat(String key) throws NumberFormatException;

	/**
	 * Gets the int.
	 *
	 * @param key the key
	 * @return the int
	 * @throws NumberFormatException the number format exception
	 */
	int getInt(String key) throws NumberFormatException;

	/**
	 * Gets the long.
	 *
	 * @param key the key
	 * @return the long
	 * @throws NumberFormatException the number format exception
	 */
	long getLong(String key) throws NumberFormatException;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the section.
	 *
	 * @param sectionName the section name
	 * @return the section
	 */
	IDialogSettings getSection(String sectionName);

	/**
	 * Gets the sections.
	 *
	 * @return the sections
	 */
	IDialogSettings[] getSections();

	/**
	 * Load.
	 *
	 * @param reader the reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void load(Reader reader) throws IOException;

	/**
	 * Load.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void load(String fileName) throws IOException;

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, String[] value);

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, double value);

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, float value);

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, int value);

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, long value);

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, String value);

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void put(String key, boolean value);

	/**
	 * Save.
	 *
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void save(Writer writer) throws IOException;

	/**
	 * Save.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void save(String fileName) throws IOException;
}
