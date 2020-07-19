/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The Class DialogSettings.
 */
public class DialogSettings implements IDialogSettings {

	/** The name. */
	private String name;

	/** The sections. */
	private Map<String, IDialogSettings> sections;

	/** The items. */
	private Map<String, String> items;

	/** The array items. */
	private Map<String, String[]> arrayItems;

	/** The Constant TAG_SECTION. */
	private static final String TAG_SECTION = "section";

	/** The Constant TAG_NAME. */
	private static final String TAG_NAME = "name";

	/** The Constant TAG_KEY. */
	private static final String TAG_KEY = "key";

	/** The Constant TAG_VALUE. */
	private static final String TAG_VALUE = "value";

	/** The Constant TAG_LIST. */
	private static final String TAG_LIST = "list";

	/** The Constant TAG_ITEM. */
	private static final String TAG_ITEM = "item";

	/**
	 * Instantiates a new dialog settings.
	 *
	 * @param sectionName the section name
	 */
	public DialogSettings(String sectionName) {
		name = sectionName;
		items = new LinkedHashMap<>();
		arrayItems = new LinkedHashMap<>();
		sections = new LinkedHashMap<>();
	}

	/**
	 * Adds the new section.
	 *
	 * @param sectionName the section name
	 * @return the i dialog settings
	 */
	@Override
	public IDialogSettings addNewSection(String sectionName) {
		DialogSettings section = new DialogSettings(sectionName);
		addSection(section);
		return section;
	}

	/**
	 * Adds the section.
	 *
	 * @param section the section
	 */
	@Override
	public void addSection(IDialogSettings section) {
		sections.put(section.getName(), section);
	}

	/**
	 * Removes the section.
	 *
	 * @param section the section
	 */
	public void removeSection(IDialogSettings section) {
		if (sections.get(section.getName()) == section) {
			sections.remove(section.getName());
		}
	}

	/**
	 * Removes the section.
	 *
	 * @param sectionName the section name
	 * @return the i dialog settings
	 */
	public IDialogSettings removeSection(String sectionName) {
		return sections.remove(sectionName);
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	@Override
	public String get(String key) {
		return items.get(key);
	}

	/**
	 * Gets the array.
	 *
	 * @param key the key
	 * @return the array
	 */
	@Override
	public String[] getArray(String key) {
		return arrayItems.get(key);
	}

	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	@Override
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(items.get(key));
	}

	/**
	 * Gets the double.
	 *
	 * @param key the key
	 * @return the double
	 * @throws NumberFormatException the number format exception
	 */
	@Override
	public double getDouble(String key) throws NumberFormatException {
		String setting = items.get(key);
		if (setting == null) {
			throw new NumberFormatException("There is no setting associated with the key \"" + key + "\""); //$NON-NLS-2$ //$NON-NLS-2$
		}
		return Double.parseDouble(setting);
	}

	/**
	 * Gets the float.
	 *
	 * @param key the key
	 * @return the float
	 * @throws NumberFormatException the number format exception
	 */
	@Override
	public float getFloat(String key) throws NumberFormatException {
		String setting = items.get(key);
		if (setting == null) {
			throw new NumberFormatException("There is no setting associated with the key \"" + key + "\"");
		}
		return Float.parseFloat(setting);
	}

	/**
	 * Gets the int.
	 *
	 * @param key the key
	 * @return the int
	 * @throws NumberFormatException the number format exception
	 */
	@Override
	public int getInt(String key) throws NumberFormatException {
		String setting = items.get(key);
		if (setting == null) {
			throw new NumberFormatException("There is no setting associated with the key \"" + key + "\"");
		}
		return Integer.parseInt(setting);
	}

	/**
	 * Gets the long.
	 *
	 * @param key the key
	 * @return the long
	 * @throws NumberFormatException the number format exception
	 */
	@Override
	public long getLong(String key) throws NumberFormatException {
		String setting = items.get(key);
		if (setting == null) {
			throw new NumberFormatException("There is no setting associated with the key \"" + key + "\""); //$NON-NLS-2$ //$NON-NLS-2$
		}

		return Long.parseLong(setting);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Gets the or create section.
	 *
	 * @param settings    the settings
	 * @param sectionName the section name
	 * @return the or create section
	 */
	public static IDialogSettings getOrCreateSection(IDialogSettings settings, String sectionName) {
		IDialogSettings section = settings.getSection(sectionName);
		if (section == null) {
			section = settings.addNewSection(sectionName);
		}
		return section;
	}

	/**
	 * Gets the section.
	 *
	 * @param sectionName the section name
	 * @return the section
	 */
	@Override
	public IDialogSettings getSection(String sectionName) {
		return sections.get(sectionName);
	}

	/**
	 * Gets the sections.
	 *
	 * @return the sections
	 */
	@Override
	public IDialogSettings[] getSections() {
		Collection<IDialogSettings> values = sections.values();
		DialogSettings[] result = new DialogSettings[values.size()];
		values.toArray(result);
		return result;
	}

	/**
	 * Load.
	 *
	 * @param r the r
	 */
	@Override
	public void load(Reader r) {
		Document document = null;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = parser.parse(new InputSource(r));
			Node root = document.getFirstChild();
			while (root.getNodeType() == Node.COMMENT_NODE) {
				document.removeChild(root);
				root = document.getFirstChild();
			}
			load(document, (Element) root);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			// ignore
		}
	}

	/**
	 * Load.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void load(String fileName) throws IOException {
		FileInputStream stream = new FileInputStream(fileName);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			load(reader);
		}
	}

	/**
	 * Load.
	 *
	 * @param document the document
	 * @param root     the root
	 */
	private void load(Document document, Element root) {
		name = root.getAttribute(TAG_NAME);
		NodeList l = root.getElementsByTagName(TAG_ITEM);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (root == n.getParentNode()) {
				String key = ((Element) l.item(i)).getAttribute(TAG_KEY);
				String value = ((Element) l.item(i)).getAttribute(TAG_VALUE);
				items.put(key, value);
			}
		}
		l = root.getElementsByTagName(TAG_LIST);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (root == n.getParentNode()) {
				Element child = (Element) l.item(i);
				String key = child.getAttribute(TAG_KEY);
				NodeList list = child.getElementsByTagName(TAG_ITEM);
				List<String> valueList = new ArrayList<>();
				for (int j = 0; j < list.getLength(); j++) {
					Element node = (Element) list.item(j);
					if (child == node.getParentNode()) {
						valueList.add(node.getAttribute(TAG_VALUE));
					}
				}
				String[] value = new String[valueList.size()];
				valueList.toArray(value);
				arrayItems.put(key, value);
			}
		}
		l = root.getElementsByTagName(TAG_SECTION);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (root == n.getParentNode()) {
				DialogSettings s = new DialogSettings("NoName"); // $NON-NLS-1$
				s.load(document, (Element) n);
				addSection(s);
			}
		}
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, String[] value) {
		if (value == null) {
			arrayItems.remove(key);
		} else {
			arrayItems.put(key, value);
		}
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, double value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, float value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, int value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, long value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, String value) {
		if (value == null) {
			items.remove(key);
		} else {
			items.put(key, value);
		}
	}

	/**
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void put(String key, boolean value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Save.
	 *
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void save(Writer writer) throws IOException {
		try (XMLWriter xmlWriter = new XMLWriter(writer)) {
			save(xmlWriter);
			xmlWriter.flush();
		}
	}

	/**
	 * Save.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void save(String fileName) throws IOException {
		try (XMLWriter writer = new XMLWriter(new FileOutputStream(fileName))) {
			save(writer);
		}
	}

	/**
	 * Save.
	 *
	 * @param out the out
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void save(XMLWriter out) throws IOException {
		Map<String, String> attributes = new LinkedHashMap<>(2);
		attributes.put(TAG_NAME, name == null ? "" : name); // $NON-NLS-1$
		out.startTag(TAG_SECTION, attributes);
		attributes.clear();

		for (Map.Entry<String, String> entry : items.entrySet()) {
			String key = entry.getKey();
			attributes.put(TAG_KEY, key == null ? "" : key); // $NON-NLS-1$
			String string = entry.getValue();
			attributes.put(TAG_VALUE, string == null ? "" : string); // $NON-NLS-1$
			out.printTag(TAG_ITEM, attributes, true);
		}

		attributes.clear();
		for (Map.Entry<String, String[]> entry : arrayItems.entrySet()) {
			String key = entry.getKey();
			attributes.put(TAG_KEY, key == null ? "" : key); // $NON-NLS-1$
			out.startTag(TAG_LIST, attributes);
			String[] value = entry.getValue();
			attributes.clear();
			if (value != null) {
				for (String string : value) {
					attributes.put(TAG_VALUE, string == null ? "" : string); // $NON-NLS-1$
					out.printTag(TAG_ITEM, attributes, true);
				}
			}
			out.endTag(TAG_LIST);
			attributes.clear();
		}
		for (IDialogSettings iDialogSettings : sections.values()) {
			((DialogSettings) iDialogSettings).save(out);
		}
		out.endTag(TAG_SECTION);
	}

	/**
	 * The Class XMLWriter.
	 */
	private static class XMLWriter extends BufferedWriter {

		/** current number of tabs to use for indent. */
		protected int tab;

		/** the xml header. */
		protected static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

		/**
		 * Instantiates a new XML writer.
		 *
		 * @param output the output
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public XMLWriter(OutputStream output) throws IOException {
			this(new OutputStreamWriter(output, StandardCharsets.UTF_8));
		}

		/**
		 * Create a new XMLWriter.
		 *
		 * @param output the write to used when writing to
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public XMLWriter(Writer output) throws IOException {
			super(output);
			tab = 0;
			writeln(XML_VERSION);
		}

		/**
		 * Writeln.
		 *
		 * @param text the text
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		private void writeln(String text) throws IOException {
			write(text);
			newLine();
		}

		/**
		 * End tag.
		 *
		 * @param name the name
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void endTag(String name) throws IOException {
			tab--;
			printTag("/" + name, null, false); // $NON-NLS-1$
		}

		/**
		 * Prints the tabulation.
		 *
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		private void printTabulation() throws IOException {
			for (int i = 0; i < tab; i++) {
				super.write('\t');
			}
		}

		/**
		 * Prints the tag.
		 *
		 * @param name       the name
		 * @param parameters the parameters
		 * @param close      the close
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void printTag(String name, Map<String, String> parameters, boolean close) throws IOException {
			printTag(name, parameters, true, true, close);
		}

		/**
		 * Prints the tag.
		 *
		 * @param name       the name
		 * @param parameters the parameters
		 * @param shouldTab  the should tab
		 * @param newLine    the new line
		 * @param close      the close
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		private void printTag(String name, Map<String, String> parameters, boolean shouldTab, boolean newLine,
				boolean close) throws IOException {
			StringBuilder sb = new StringBuilder();
			sb.append('<');
			sb.append(name);
			if (parameters != null) {
				for (Map.Entry<String, String> entry : parameters.entrySet()) {
					sb.append(" "); // $NON-NLS-1$
					String key = entry.getKey();
					sb.append(key);
					sb.append("=\""); // $NON-NLS-1$
					sb.append(getEscaped(String.valueOf(entry.getValue())));
					sb.append("\""); // $NON-NLS-1$
				}
			}
			if (close) {
				sb.append('/');
			}
			sb.append('>');
			if (shouldTab) {
				printTabulation();
			}
			if (newLine) {
				writeln(sb.toString());
			} else {
				write(sb.toString());
			}
		}

		/**
		 * Start tag.
		 *
		 * @param name       the name
		 * @param parameters the parameters
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public void startTag(String name, Map<String, String> parameters) throws IOException {
			startTag(name, parameters, true);
			tab++;
		}

		/**
		 * Start tag.
		 *
		 * @param name       the name
		 * @param parameters the parameters
		 * @param newLine    the new line
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		private void startTag(String name, Map<String, String> parameters, boolean newLine) throws IOException {
			printTag(name, parameters, true, newLine, false);
		}

		/**
		 * Append escaped char.
		 *
		 * @param buffer the buffer
		 * @param c      the c
		 */
		private static void appendEscapedChar(StringBuilder buffer, char c) {
			String replacement = getReplacement(c);
			if (replacement != null) {
				buffer.append('&');
				buffer.append(replacement);
				buffer.append(';');
			} else {
				buffer.append(c);
			}
		}

		/**
		 * Gets the escaped.
		 *
		 * @param s the s
		 * @return the escaped
		 */
		private static String getEscaped(String s) {
			StringBuilder result = new StringBuilder(s.length() + 10);
			for (int i = 0; i < s.length(); ++i) {
				appendEscapedChar(result, s.charAt(i));
			}
			return result.toString();
		}

		/**
		 * Gets the replacement.
		 *
		 * @param c the c
		 * @return the replacement
		 */
		private static String getReplacement(char c) {
			switch (c) {
			case '<':
				return "lt"; //$NON-NLS-1$
			case '>':
				return "gt"; //$NON-NLS-1$
			case '"':
				return "quot"; //$NON-NLS-1$
			case '\'':
				return "apos"; //$NON-NLS-1$
			case '&':
				return "amp"; //$NON-NLS-1$
			case '\r':
				return "#x0D"; //$NON-NLS-1$
			case '\n':
				return "#x0A"; //$NON-NLS-1$
			case '\u0009':
				return "#x09"; //$NON-NLS-1$
			}
			return null;
		}
	}
}
