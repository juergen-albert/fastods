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
package com.github.jferard.fastods.tool;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Desktop.class, FastOds.class})
public class FastOdsTest {
    @Before
    public void setUp() {
        PowerMock.resetAll();
    }

    @After
    public void tearDown() {
        PowerMock.verifyAll();
    }

    @Test
    public final void testOpenDir() {
        PowerMock.replayAll();
        Assert.assertFalse(FastOds.openFile(new File(".")));
    }

    @Test
    public final void testOpenFile() throws IOException {
        PowerMock.mockStatic(Desktop.class);
        final Desktop d = PowerMock.createMock(Desktop.class);
        final File f = new File(".", "pom.xml");

        // PLAY
        EasyMock.expect(Desktop.getDesktop()).andReturn(d);
        d.open(f);

        PowerMock.replayAll();
        Assert.assertTrue(FastOds.openFile(f));
    }

    @Test
    public final void testOpenInexistentFile() throws IOException {
        final File f = PowerMock.createMock(File.class);

        // PLAY
        EasyMock.expect(f.exists()).andReturn(false);

        PowerMock.replayAll();
        Assert.assertFalse(FastOds.openFile(f));
    }

    @Test
    public final void testOpenDirMock() throws IOException {
        final File f = PowerMock.createMock(File.class);

        // PLAY
        EasyMock.expect(f.exists()).andReturn(true);
        EasyMock.expect(f.isFile()).andReturn(false);

        PowerMock.replayAll();
        Assert.assertFalse(FastOds.openFile(f));
    }

    @Test
    public final void testOpenFileError() throws IOException {
        // let's hide logging infos
        final Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);
        for (final Handler h : rootLogger.getHandlers())
            h.setLevel(Level.OFF);

        PowerMock.mockStatic(Desktop.class);
        final Desktop d = PowerMock.createMock(Desktop.class);
        final File f = new File(".", "pom.xml");

        // PLAY
        EasyMock.expect(Desktop.getDesktop()).andReturn(d);
        d.open(f);
        EasyMock.expectLastCall().andThrow(new IOException());

        PowerMock.replayAll();
        Assert.assertFalse(FastOds.openFile(f));
    }

    @Test
    public final void testOpenNonExisting() {
        PowerMock.replayAll();
        Assert.assertFalse(FastOds.openFile(new File(".", "@")));
    }

    @Test
    public final void testXMLUtil() {
        PowerMock.replayAll();
        FastOds.getXMLUtil();
        FastOds.getXMLUtil();
    }
}
