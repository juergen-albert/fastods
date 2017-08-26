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

package com.github.jferard.fastods.it;

import com.github.jferard.fastods.*;
import com.github.jferard.fastods.style.TableCellStyle;
import com.github.jferard.fastods.testlib.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * This is the test for the README.md
 */
public class ReadmeExampleIT {
    public static final String GENERATED_FILES = "generated_files";
    public static final String README_EXAMPLE_ODS = "readme_example.ods";
    public static final String README_EXAMPLE_WITH_FLUSH_ODS = "readme_example_with_flush.ods";
    public static final String GREEN_CELL_STYLE = "green cell style";
    public static final String GREEN_COLOR = "#00FF00";

    @BeforeClass
    public static final void beforeClass() {
        Util.mkdir(GENERATED_FILES);
    }
    private Logger logger;
    private OdsFactory odsFactory;
    private TableCellStyle style;

    @Before
    public void setUp() {
        this.logger = Logger.getLogger("readme example");
        this.odsFactory = OdsFactory.create(this.logger, Locale.US);
        this.style = TableCellStyle.builder(GREEN_CELL_STYLE).backgroundColor(GREEN_COLOR).build();
    }


    @Test
    public void readmeIT() throws Exception {
        this.readme();
        this.validateReadme(README_EXAMPLE_ODS);
    }

    @Test
    public void readmeWithFlushIT() throws Exception {
        this.readmeWithFlush();
        this.validateReadme(README_EXAMPLE_WITH_FLUSH_ODS);
    }

    private void validateReadme(final String documentName) throws Exception {
        final SpreadsheetDocument document = SpreadsheetDocument.loadDocument(new File(GENERATED_FILES, documentName));
        Assert.assertEquals(1, document.getSheetCount());
        final org.odftoolkit.simple.table.Table sheet = document.getSheetByName("test");
        Assert.assertNotNull(sheet);
        Assert.assertEquals(50, sheet.getRowCount());
        final OdfStyle gcs = document.getStylesDom().getOfficeStyles().getStyle(GREEN_CELL_STYLE, OdfStyleFamily.TableCell);
        Assert.assertEquals("Default", gcs.getStyleParentStyleNameAttribute());
        final Node properties = gcs.getElementsByTagName("style:table-cell-properties").item(0);
        final NamedNodeMap attributes = properties.getAttributes();
        Assert.assertEquals(GREEN_COLOR, attributes.getNamedItem("fo:background-color").getTextContent());
        for (int y = 0; y < 50; y++) {
            for (int x = 0; x < 5; x++) {
                final org.odftoolkit.simple.table.Cell cell = sheet.getCellByPosition(x, y);
                Assert.assertEquals(Double.valueOf(x * y), cell.getDoubleValue());
                Assert.assertEquals("float", cell.getValueType());

                final TableTableCellElementBase element = cell.getOdfElement();
                Assert.assertEquals(GREEN_CELL_STYLE + "@@float-data", element.getStyleName());
                Assert.assertEquals("table-cell", element.getStyleFamily().toString());
                Assert.assertEquals(GREEN_CELL_STYLE, element.getAutomaticStyle().getStyleParentStyleNameAttribute());
            }
        }
    }

    private void readme() throws IOException {
        final AnonymousOdsFileWriter writer = this.odsFactory.createWriter();
        final OdsDocument document = writer.document();

        this.createTable(document);

        writer.saveAs(new File(GENERATED_FILES, README_EXAMPLE_ODS));
    }

    private void createTable(final OdsDocument document) throws IOException {
        final Table table = document.addTable("test");
        for (int y = 0; y < 50; y++) {
            final TableRow row = table.nextRow();
            final TableCellWalker cell = row.getWalker();
            for (int x = 0; x < 5; x++) {
                cell.setFloatValue(x * y);
                cell.setStyle(this.style);
                cell.next();
            }
        }
    }

    private void readmeWithFlush() throws IOException {
        final OdsFileWriter writer =
                this.odsFactory.createWriter(new File(GENERATED_FILES, README_EXAMPLE_WITH_FLUSH_ODS));
        final OdsDocument document = writer.document();

        document.addObjectStyle(this.style);
        document.addChildCellStyle(TableCell.Type.FLOAT);
        document.addChildCellStyle(this.style, TableCell.Type.FLOAT);
        document.freezeStyles(); // if this crashes, use debugStyles to log the errors

        this.createTable(document);

        document.save();
    }
}