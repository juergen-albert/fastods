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

package com.github.jferard.fastods;

import com.github.jferard.fastods.odselement.ContentElement;
import com.github.jferard.fastods.odselement.SettingsElement;
import com.github.jferard.fastods.util.XMLUtil;
import com.github.jferard.fastods.util.ZipUTF8Writer;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jferard on 09/05/17.
 */
public class RowsFlusherTest {

    private XMLUtil util;
    private ZipUTF8Writer w;

    @Before
    public void setUp() {
        this.util = XMLUtil.create();
        this.w = PowerMock.createMock(ZipUTF8Writer.class);
    }

    @Test
    public void flushIntoEmptyList() throws Exception {
        final List<TableRow> rows = Collections.emptyList();

        PowerMock.replayAll();
        final RowsFlusher flusher = new RowsFlusher(rows);
        flusher.flushInto(this.util, this.w);
        PowerMock.verifyAll();
    }

    @Test
    public void flushInto() throws Exception {
        final TableRow r1 = PowerMock.createMock(TableRow.class);
        final TableRow r2 = PowerMock.createMock(TableRow.class);
        final List<TableRow> rows = Arrays.asList(r1, r2);

        r1.appendXMLToTable(this.util, this.w);
        r2.appendXMLToTable(this.util, this.w);

        PowerMock.replayAll();
        final RowsFlusher flusher = new RowsFlusher(rows);
        flusher.flushInto(this.util, this.w);
        PowerMock.verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void flushIntoNullRow() throws Exception {
        final List<TableRow> rows = new ArrayList<TableRow>();
        rows.add(null);

        PowerMock.replayAll();
        final RowsFlusher flusher = new RowsFlusher(rows);
        flusher.flushInto(this.util, this.w);
        PowerMock.verifyAll();
    }
}