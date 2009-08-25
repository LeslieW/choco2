/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package parser.absconparseur;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import choco.kernel.common.logging.ChocoLogging;

public final class XMLManager {

	protected final static Logger LOGGER = ChocoLogging.getParserLogger();

	private static boolean useStyleSheet = true; // true; // TODO //true; //false; // true // a style sheet can seriously degrade performances

	private static final class ErrorHandler extends DefaultHandler {
		
		private MessageFormat message = new MessageFormat("({0}: {1}, {2}): {3}");

		private void print(SAXParseException x) {
			String msg = message.format(new Object[] { x.getSystemId(), x.getLineNumber(), x.getColumnNumber(), x.getMessage() });
			LOGGER.info(msg);
		}
		
		@Override
		public void warning(SAXParseException x) {
			print(x);
		}

		@Override
		public void error(SAXParseException x) {
			print(x);
		}

		@Override
		public void fatalError(SAXParseException x) throws SAXParseException {
			print(x);
			throw x;
		}
	}
		private static void dealWithException(Exception e) {
			if (e instanceof SAXParseException) {
				SAXParseException ee = (SAXParseException) e;
				LOGGER.log(Level.WARNING,"\n** Parsing error" + ", line " + ee.getLineNumber() + ", uri " + ee.getSystemId(),e);
			} else if (e instanceof SAXException) {
				SAXException ee = (SAXException) e;
				Exception x = (ee.getException() == null ? ee : ee.getException());
				x.printStackTrace();
			} else if (e instanceof TransformerConfigurationException) {
				LOGGER.log(Level.WARNING,"\n** Transformer Factory error\n",e);
			} else if (e instanceof TransformerException) {
				LOGGER.log(Level.WARNING,"\n** Transformation error", e);
			} else {
				LOGGER.log(Level.WARNING,"Unknown error", e);
			}
			System.exit(1);
		}
	
		public static Document createNewDocument() {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				return builder.newDocument();
			} catch (ParserConfigurationException e) {
				dealWithException(e);
				return null;
			}
		}

		/**
		 * Build a DOM object that corresponds to the given input stream.
		 * 
		 * @param is the input stream that denotes the XML document to be loaded.
		 * @param schemaUrl the schema to be used (<code> null </code> if not used) to validate the document
		 * @return a DOM object
		 */
		public static Document load(InputStream is, URL schemaUrl) {
			try {
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilderFactory.setNamespaceAware(true);
				if (schemaUrl != null) {
					SchemaFactory schemaFactory = SchemaFactory.newInstance(InstanceTokens.W3C_XML_SCHEMA);
					Schema schema = schemaFactory.newSchema(schemaUrl); // new File(schemaFileName));
					documentBuilderFactory.setSchema(schema);
				}
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				documentBuilder.setErrorHandler(new ErrorHandler());
				return documentBuilder.parse(is);
			} catch (Exception e) {
				dealWithException(e);
				return null;
			}
		}

		
		public static Document load(File file, URL schemaUrl) {
			try {
				return load(new FileInputStream(file), schemaUrl);
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.INFO, "File {0} does not exist", file);
				System.exit(1);
				return null;
			}
		}

		public static Document load(InputStream is) {
			return load(is, null);
		}

		public static Document load(File file) {
			return load(file, null);
		}

		public static Document load(String fileName) {
			if (fileName.endsWith("xml.bz2")) {
				try {
					Process p = Runtime.getRuntime().exec("bunzip2 -c " + fileName);
					Document document = load(p.getInputStream());
					p.waitFor();
					p.exitValue();
					p.destroy();
					return document;

				} catch (Exception e) {
					LOGGER.log(Level.INFO, "Problem with {0}", fileName);
					System.exit(1);
					return null;
				}
			}
			return load(new File(fileName), null);
		}

		private static Transformer buildTransformer(InputStream styleSheetInputStream) {
			try {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				if (styleSheetInputStream == null || !useStyleSheet)
					return transformerFactory.newTransformer();
				Document document = load(styleSheetInputStream, null);
				DOMSource source = new DOMSource(document);
				return transformerFactory.newTransformer(source);
			} catch (TransformerConfigurationException e) {
				dealWithException(e);
				return null;
			}
		}

		public static void save(Document document, PrintWriter writer, InputStream styleSheetInputStream) {
			try {
				Transformer transformer = buildTransformer(styleSheetInputStream);
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(writer);
				transformer.transform(source, result);
			} catch (TransformerException e) {
				dealWithException(e);
			}
		}

		public static void save(Document document, String fileName, InputStream styleSheetInputStream) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
				save(document, out, styleSheetInputStream);
				out.close();
			} catch (IOException e) {
				dealWithException(e);
			}
		}

		public static void save(Document document, String fileName) {
			save(document, fileName, (InputStream)null);
		}

		public static void save(Document document, String fileName, String styleSheetFileName) {
			save(document, fileName, styleSheetFileName == null ? null : XMLManager.class.getResourceAsStream(styleSheetFileName));
		}

		public static Element getElementByTagNameFrom(Element element, String tagName, int i) {
			NodeList nodeList = element.getElementsByTagName(tagName);
			if (nodeList == null || nodeList.getLength() <= i)
				return null;
			return (Element) (nodeList.item(i));
		}

		public static Element getFirstElementByTagNameFromRoot(Document document, String tagName) {
			return getElementByTagNameFrom(document.getDocumentElement(), tagName, 0);
		}

		public static void deleteElementByTagNameFrom(Element element, String tagName, int i) {
			NodeList list = element.getElementsByTagName(tagName);
			if (list.getLength() <= i)
				throw new IllegalArgumentException();
			element.removeChild(list.item(i));
		}

		public static Node getChildElement(Document document, String elementName, int index) {
			NodeList list = document.getElementsByTagName(elementName);
			return list.item(index);
		}

		public static void deleteChildElement(Document document, String elementName, int index) {
			Element element = (Element) document.getElementsByTagName(elementName).item(0);
			element.removeChild(getChildElement(document, elementName, index));
	}

		public static String[] displayAttributes(Element element) {
			NamedNodeMap map = element.getAttributes();
			String[] values = new String[map.getLength()];
			for (int i = 0; i < map.getLength(); i++) {
				Attr attribut = (Attr) map.item(i);
				values[i] = attribut.getValue();
				LOGGER.info(attribut.getName() + " = " + values[i]);
			}
			return values;
		}

	public static void displayElement(Document document, String elementName) {
		Node node = document.getElementsByTagName(elementName).item(0);
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			displayAttributes(element);
		}
		}

	}
