/*
 * FastODS - a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016-2017 J. Férard <https://github.com/jferard>
 * SimpleODS - A lightweight java library to create simple OpenOffice spreadsheets
 *    Copyright (C) 2008-2013 Martin Schulz <mtschulz at users.sourceforge.net>
 *
 * This file is part of FastODS.
 *
 * FastODS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * FastODS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.jferard.fastods.datastyle;

import java.io.IOException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.jferard.fastods.testlib.DomTester;
import com.github.jferard.fastods.util.XMLUtil;

public class ScientificNumberStyleTest {
	private DataStyleBuilderFactory factory;
	private Locale locale;
	private XMLUtil util;

	@Before
	public void setUp() {
		this.util = XMLUtil.create();
		this.locale = Locale.US;
		this.factory = new DataStyleBuilderFactory(this.util, this.locale);
	}

	@Test
	public final void test1() throws IOException, SAXException {
		final ScientificNumberStyle s = this.factory
				.scientificNumberStyleBuilder("name").country("FR")
				.language("en").volatileStyle(true).minExponentDigits(1)
				.groupThousands(true).minIntegerDigits(8).negativeValueRed()
				.build();
		final StringBuilder sb = new StringBuilder();
		s.appendXML(this.util, sb);
		final String str = "<number:number-style style:name=\"name\" number:language=\"en\" number:country=\"FR\" style:volatile=\"true\">"
				+ "<number:scientific-number number:min-exponent-digits=\"1\" number:decimal-places=\"0\" number:min-integer-digits=\"8\" number:grouping=\"true\"/>"
				+ "</number:number-style>"
				+ "<number:number-style style:name=\"name-neg\" number:language=\"en\" number:country=\"FR\" style:volatile=\"true\">"
				+ "<style:text-properties fo:color=\"#FF0000\"/>"
				+ "<number:text>-</number:text>"
				+ "<number:scientific-number number:min-exponent-digits=\"1\" number:decimal-places=\"0\" number:min-integer-digits=\"8\" number:grouping=\"true\"/>"
				+ "<style:map style:condition=\"value()&gt;=0\" style:apply-style-name=\"name\"/>"
				+ "</number:number-style>";
		DomTester.assertEquals(str, sb.toString());
	}

	@Test
	public final void test2() throws IOException, SAXException {
		final ScientificNumberStyle s = this.factory
				.scientificNumberStyleBuilder("name").country("FR")
				.language("en").locale(Locale.GERMANY).volatileStyle(true)
				.minExponentDigits(2).groupThousands(true).minIntegerDigits(8)
				.negativeValueRed().build();
		final StringBuilder sb = new StringBuilder();
		s.appendXML(this.util, sb);
		final String str = "<number:number-style style:name=\"name\" number:language=\"de\" number:country=\"DE\" style:volatile=\"true\">"
				+ "<number:scientific-number number:min-exponent-digits=\"2\" number:decimal-places=\"0\" number:min-integer-digits=\"8\" number:grouping=\"true\"/>"
				+ "</number:number-style>"
				+ "<number:number-style style:name=\"name-neg\" number:language=\"de\" number:country=\"DE\" style:volatile=\"true\">"
				+ "<style:text-properties fo:color=\"#FF0000\"/>"
				+ "<number:text>-</number:text>"
				+ "<number:scientific-number number:min-exponent-digits=\"2\" number:decimal-places=\"0\" number:min-integer-digits=\"8\" number:grouping=\"true\"/>"
				+ "<style:map style:condition=\"value()&gt;=0\" style:apply-style-name=\"name\"/>"
				+ "</number:number-style>";
		DomTester.assertEquals(str, sb.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testWithNoName() {
		final ScientificNumberStyle sns = this.factory
				.scientificNumberStyleBuilder(null).locale(this.locale).build();
	}
}