/*
 * FastODS - A very fast and lightweight (no dependency) library for creating ODS
 *    (Open Document Spreadsheet, mainly for Calc) files in Java.
 *    It's a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016-2018 J. Férard <https://github.com/jferard>
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

package com.github.jferard.fastods;

import com.github.jferard.fastods.odselement.StylesContainer;
import com.github.jferard.fastods.util.Container;
import com.github.jferard.fastods.util.XMLUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.IOException;

public class SimpleFooterHeaderTest {

    private XMLUtil util;
    private StylesContainer stylesContainer;

    @Before
    public void setUp() {
        this.util = XMLUtil.create();
        this.stylesContainer = PowerMock.createMock(StylesContainer.class);
        PowerMock.resetAll();
    }

    @After
    public void tearDown() {
        PowerMock.verifyAll();
    }

    @Test
    public void nullText() throws IOException {
        final PageSectionContent content = new SimpleFooterHeader(null);
        PowerMock.replayAll();
        final StringBuilder sb = new StringBuilder();
        content.appendXMLToMasterStyle(this.util, sb);
        Assert.assertEquals("", sb.toString());
        content.addEmbeddedStyles(this.stylesContainer);
        content.addEmbeddedStyles(this.stylesContainer, Container.Mode.UPDATE);
    }

    @Test
    public void emptyText() throws IOException {
        final Text text = PowerMock.createMock(Text.class);
        final PageSectionContent content = new SimpleFooterHeader(text);

        EasyMock.expect(text.isEmpty()).andReturn(true).times(3);
        PowerMock.replayAll();
        final StringBuilder sb = new StringBuilder();
        content.appendXMLToMasterStyle(this.util, sb);
        Assert.assertEquals("", sb.toString());
        content.addEmbeddedStyles(this.stylesContainer);
        content.addEmbeddedStyles(this.stylesContainer, Container.Mode.UPDATE);
    }

    @Test
    public void nonEmptyText() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final Text text = PowerMock.createMock(Text.class);
        final PageSectionContent content = new SimpleFooterHeader(text);

        EasyMock.expect(text.isEmpty()).andReturn(false).times(3);
        text.appendXMLContent(this.util, sb);
        text.addEmbeddedStylesFromFooterHeader(this.stylesContainer, Container.Mode.CREATE);
        text.addEmbeddedStylesFromFooterHeader(this.stylesContainer, Container.Mode.UPDATE);
        PowerMock.replayAll();
        content.appendXMLToMasterStyle(this.util, sb);
        Assert.assertEquals("", sb.toString());
        content.addEmbeddedStyles(this.stylesContainer);
        content.addEmbeddedStyles(this.stylesContainer, Container.Mode.UPDATE);
    }

}