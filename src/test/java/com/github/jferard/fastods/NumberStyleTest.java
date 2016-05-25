/*
 * FastODS - a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016 J. Férard
 * SimpleODS - A lightweight java library to create simple OpenOffice spreadsheets
 *    Copyright (C) 2008-2013 Martin Schulz <mtschulz at users.sourceforge.net>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.jferard.fastods;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Julien Férard Copyright (C) 2016 J. Férard Copyright 2008-2013 Martin
 *         Schulz <mtschulz at users.sourceforge.net>
 *
 *         This file BenchTest.java is part of FastODS.
 */
public class NumberStyleTest {

	@Test
	public final void testEmpty() throws IOException {
		NumberStyle ns = NumberStyle.builder().name("test").build();
		StringBuilder sb = new StringBuilder();
		ns.appendXML(Util.getInstance(), sb, null);
		Assert.assertEquals("<number:number-style style:name=\"test\">"
				+ "<number:number number:decimal-places=\"2\" number:min-integer-digits=\"1\"/>"
				+ "</number:number-style>", sb.toString());
	}

	@Test
	public final void testNegative() throws IOException {
		NumberStyle ns = NumberStyle.builder().name("test").negativeValuesRed(true).build();
		StringBuilder sb = new StringBuilder();
		ns.appendXML(Util.getInstance(), sb, null);
		Assert.assertEquals(
				"<number:number-style style:name=\"testnn\" style:volatile=\"true\">"+
				"<number:number number:decimal-places=\"2\" number:min-integer-digits=\"1\"/>"+
				"</number:number-style>"+
				"<number:number-style style:name=\"test\">"+
				"<style:text-properties fo:color=\"#FF0000\"/>"+
				"<number:text>-</number:text>"+
				"<number:number number:decimal-places=\"2\" number:min-integer-digits=\"1\"/>"+
				"<style:map style:condition=\"value()&gt;=0\" style:apply-style-name=\"testnn\"/>"+
				"</number:number-style>",
				sb.toString());
	}
}