package com.github.jferard.fastods;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Objects;

public class DomTester {
	static DomTester tester = new DomTester();
	private DocumentBuilder builder;
	private Logger logger;

	private DomTester() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			this.builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.logger = Logger.getLogger(this.getClass().getName());
	}

	public static boolean equals(final String s1, final String s2)
			throws SAXException, IOException {
		return tester.stringEquals(s1, s2);
	}
		
	private boolean stringEquals(final String s1, final String s2)
			throws SAXException, IOException {
		Document document1 = this.builder.parse(
				new ByteArrayInputStream(("<r>" + s1 + "</r>").getBytes()));
		Document document2 = this.builder.parse(
				new ByteArrayInputStream(("<r>" + s2 + "</r>").getBytes()));
		return this.equals(document1.getDocumentElement().getFirstChild(),
				document2.getDocumentElement().getFirstChild());
	}

	private boolean equals(Node element1, Node element2) {
		return namesEquals(element1, element2)
				&& attributesEquals(element1, element2)
				&& childrenEquals(element1, element2);
	}

	private boolean childrenEquals(Node element1, Node element2) {
		final NodeList nodes1 = element1.getChildNodes();
		final NodeList nodes2 = element2.getChildNodes();
		final int l1 = nodes1.getLength();
		final int l2 = nodes2.getLength();
		if (l1 != l2) {
			this.logger.info("Different children number "+element1+"->"+nodes1+" vs "+element2+"->"+nodes2);
			return false;
		}

		for (int i = 0; i < l1; i++) {
			final Node c1 = nodes1.item(i);
			final Node c2 = nodes2.item(i);
			if (!this.equals(c1, c2)) {
				this.logger.info("Different children "+c1+" vs "+c2);
				return false;
			}
		}

		return true;
	}

	private boolean namesEquals(Node element1, Node element2) {
		return element1.getNodeType() == element2.getNodeType()
				&& Objects.equal(element1.getNodeName(), element2.getNodeName())
				&& Objects.equal(element1.getNodeValue(),
						element2.getNodeValue())
				&& Objects.equal(element1.getNamespaceURI(),
						element2.getNamespaceURI());
	}

	private boolean attributesEquals(Node element1, Node element2) {
		final NamedNodeMap attributes1 = element1.getAttributes();
		final NamedNodeMap attributes2 = element2.getAttributes();
		if (attributes1 == null) {
			if (attributes2 != null)
				return false;
		} else {
			if (attributes2 != null) {
				if (attributes1.getLength() != attributes2.getLength())
					return false;

				for (int i = 0; i < attributes1.getLength(); i++)
					if (!this.equals(attributes1.item(i),
							attributes2.item(i)))
						return false;
			}
		}
		return true;
	}
}
