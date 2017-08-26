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
import com.github.jferard.fastods.style.TableColumnStyle;
import com.github.jferard.fastods.style.TableRowStyle;
import com.github.jferard.fastods.testlib.Fibonacci;
import com.github.jferard.fastods.testlib.OdfToolkitUtil;
import com.github.jferard.fastods.testlib.Util;
import com.github.jferard.fastods.util.SimpleLength;
import org.junit.*;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfSettingsDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * @author Julien Férard
 */
public class OdsFileCreationIT {
    public static final String GENERATED_FILES = "generated_files";
    public static final String STANDARD_DOCUMENT_ODS = "standard_document.ods";
    public static final String STANDARD_DOCUMENT_WITH_FLUSH_ODS = "standard_document_with_flush.ods";

    @BeforeClass
    public static final void beforeClass() {
        Util.mkdir(GENERATED_FILES);
    }
    private Logger logger;
    private OdsFactory odsFactory;
    private Fibonacci fibonacci;
    private TableColumnStyle tcns;
    private TableRowStyle trs;
    private TableCellStyle tcls;
    private TableCellStyle tcs0;
    private TableCellStyle tcs1;
    private TableCellStyle tcs2;
    private TableCellStyle tcs3;
    private long t1;

    @Before
    public void setUp() {
        this.logger = Logger.getLogger("standard document");
        this.odsFactory = OdsFactory.create(this.logger, Locale.US);
        this.fibonacci = Fibonacci.create();
        this.logger.info("Filling a 5 rows, 5 columns spreadsheet");
        this.t1 = System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        final long t2 = System.currentTimeMillis();
        this.logger.info("Filled in " + (t2 - this.t1) + " ms");
    }

    @Test
    public final void testStandardIT() throws Exception {
        this.standardDocument();
        this.validateStandardDocument(STANDARD_DOCUMENT_ODS);
    }

    @Test
    public final void testStandardWithFlushIT() throws Exception {
        this.standardDocumentWithFlush();
        this.validateStandardDocument(STANDARD_DOCUMENT_WITH_FLUSH_ODS);
    }

    private void validateStandardDocument(final String documentName) throws Exception {
        final Fibonacci validateFibonacci = Fibonacci.create();
        final SpreadsheetDocument document = SpreadsheetDocument.loadDocument(new File(GENERATED_FILES, documentName));
        Assert.assertEquals(1, document.getSheetCount());
        final org.odftoolkit.simple.table.Table sheet = document.getSheetByName("test");
        Assert.assertNotNull(sheet);
        Assert.assertEquals(5, sheet.getRowCount());

        final OdfSettingsDom settingsDom = document.getSettingsDom();
        Assert.assertEquals("207", settingsDom.getXPath().evaluate("//config:config-item[@config:name='ZoomValue']", settingsDom.getRootElement()));
        Assert.assertEquals("206", settingsDom.getXPath().evaluate("//config:config-item-map-entry[@config:name='test']//config:config-item[@config:name='ZoomValue']", settingsDom.getRootElement()));
        final OdfContentDom contentDom = document.getContentDom();
        Assert.assertEquals("5cm", contentDom.getXPath().evaluate("//style:style[@style:name='rr']//style:table-row-properties/@style:row-height", contentDom.getRootElement()));

        final OdfStylesDom stylesDom = document.getStylesDom();
        Assert.assertEquals("#dddddd", stylesDom.getXPath().evaluate("//style:style[@style:name='cc']//@fo:background-color", stylesDom.getRootElement()));
        Assert.assertEquals("bold", stylesDom.getXPath().evaluate("//style:style[@style:name='cc']//@fo:font-weight", stylesDom.getRootElement()));

        Assert.assertEquals("#0000ff", stylesDom.getXPath().evaluate("//style:style[@style:name='tcs0']//@fo:background-color", stylesDom.getRootElement()));
        Assert.assertEquals("#00FF00", stylesDom.getXPath().evaluate("//style:style[@style:name='tcs1']//@fo:background-color", stylesDom.getRootElement()));
        Assert.assertEquals("bold", stylesDom.getXPath().evaluate("//style:style[@style:name='tcs2']//@fo:font-weight", stylesDom.getRootElement()));
        Assert.assertEquals("italic", stylesDom.getXPath().evaluate("//style:style[@style:name='tcs3']//@fo:font-style", stylesDom.getRootElement()));

        // FIRST ROW
        Row row = sheet.getRowByIndex(0);
        Assert.assertEquals("Accented characters: àäéèëêïîöôüûÿÿ", OdfToolkitUtil.getStringValue(row.getCellByIndex(0)));
        Assert.assertEquals("Symbols: €", OdfToolkitUtil.getStringValue(row.getCellByIndex(1)));
        Assert.assertEquals("Symbols: £", OdfToolkitUtil.getStringValue(row.getCellByIndex(2)));

        // SECOND ROW
        row = sheet.getRowByIndex(1);
        Cell c = row.getCellByIndex(0);
        Assert.assertEquals(validateFibonacci.nextInt(100000), c.getDoubleValue().intValue());
        Assert.assertEquals("tcs0", OdfToolkitUtil.getParentStyleName(c));
        c = row.getCellByIndex(1);
        Assert.assertEquals(validateFibonacci.nextInt(100000), c.getDoubleValue().intValue());
        Assert.assertEquals("tcs1", OdfToolkitUtil.getParentStyleName(c));
        c = row.getCellByIndex(2);
        Assert.assertEquals(validateFibonacci.nextInt(100000), c.getDoubleValue().intValue());
        Assert.assertEquals("tcs2", OdfToolkitUtil.getParentStyleName(c));
        c = row.getCellByIndex(3);
        Assert.assertEquals(validateFibonacci.nextInt(100000), c.getDoubleValue().intValue());
        Assert.assertEquals("tcs3", OdfToolkitUtil.getParentStyleName(c));

        // THIRD ROW
        row = sheet.getRowByIndex(2);
        Assert.assertEquals(true, row.getCellByIndex(0).getBooleanValue());
        Assert.assertEquals(true, row.getCellByIndex(1).getBooleanValue());
        c = row.getCellByIndex(2);
        Assert.assertEquals(150.5, c.getCurrencyValue().doubleValue(), 0.01);
        Assert.assertEquals("EUR", c.getCurrencyCode());
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        cal.setTimeInMillis(1234567891011L);
        cal.set(Calendar.HOUR_OF_DAY, -1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Calendar actualCal = row.getCellByIndex(3).getDateValue();
        Assert.assertEquals(cal.getTime(), actualCal.getTime());
        Assert.assertEquals(70.3, row.getCellByIndex(4).getPercentageValue().doubleValue(), 0.01);
        Assert.assertEquals("foobar", OdfToolkitUtil.getStringValue(row.getCellByIndex(5)));

        // FOURTH ROW
        row = sheet.getRowByIndex(3);
        Assert.assertEquals(2, row.getCellByIndex(0).getColumnSpannedNumber());
        c = row.getCellByIndex(1);
        Assert.assertEquals(-150.5, c.getCurrencyValue().doubleValue(), 0.01);
        Assert.assertEquals("€", c.getCurrencyCode());
        c = row.getCellByIndex(2);
        Assert.assertEquals("x", OdfToolkitUtil.getStringValue(c));
        Assert.assertEquals("cc", c.getStyleName());
        Assert.assertEquals(3 * 60 * 1000, row.getCellByIndex(3).getTimeValue().getTimeInMillis() + 3600000);
        c = row.getCellByIndex(4);
        Assert.assertEquals("formula result", OdfToolkitUtil.getStringValue(c));
        Assert.assertEquals("=1+1", c.getFormula());

        // FIFTH ROW
        row = sheet.getRowByIndex(4);
        Assert.assertEquals("That's a <tooltip>with a newline !", row.getCellByIndex(0).getNoteText());

        // filter
        Assert.assertEquals("test.A1:test.F4", contentDom.getXPath().evaluate("//table:database-range[@table:display-filter-buttons='true']//@table:target-range-address", contentDom.getRootElement()));
    }

    private void standardDocument() throws IOException, FastOdsException {
        final AnonymousOdsFileWriter writer = this.odsFactory.createWriter();
        final OdsDocument document = writer.document();
        this.createStyles();

        this.fillDocument(document);

        writer.saveAs(new File(GENERATED_FILES, STANDARD_DOCUMENT_ODS));
    }

    private void createStyles() {
        this.tcns = TableColumnStyle.builder("ccs")
                .columnWidth(SimpleLength.cm(10.0)).defaultCellStyle(this.tcls).buildHidden();
        this.trs = TableRowStyle.builder("rr").rowHeight(SimpleLength.cm(5.0))
                .buildHidden();
        this.tcls = TableCellStyle.builder("cc")
                .backgroundColor("#dddddd").fontWeightBold().build();
        this.tcs0 = TableCellStyle.builder("tcs0")
                .backgroundColor("#0000ff").build();
        this.tcs1 = TableCellStyle.builder("tcs1")
                .backgroundColor("#00FF00").build();
        this.tcs2 = TableCellStyle.builder("tcs2")
                .fontWeightBold().build();
        this.tcs3 = TableCellStyle.builder("tcs3")
                .fontStyleItalic().build();
    }

    private void fillDocument(final OdsDocument document) throws IOException, FastOdsException {
        document.setViewSetting("View1", "ZoomValue", "207");
        final Table table = document.addTable("test", 50, 5);
        table.setColumnStyle(0, this.tcns);
        table.setSettings("View1", "ZoomValue", "206");
        TableRow row = table.getRow(0);
        row.setStyle(this.trs);
        row.setDefaultCellStyle(this.tcls);

        // FIRST ROW
        row = table.getRow(0);
        row.getOrCreateCell(0).setStringValue("Accented characters: àäéèëêïîöôüûÿÿ");
        row.getOrCreateCell(1).setStringValue("Symbols: €");
        row.getOrCreateCell(2).setStringValue("Symbols: £");

        // SECOND ROW
        row = table.getRow(1);
        TableCell c = row.getOrCreateCell(0);
        c.setFloatValue(this.fibonacci.nextInt(100000));
        c.setStyle(this.tcs0);
        c = row.getOrCreateCell(1);
        c.setFloatValue(this.fibonacci.nextInt(100000));
        c.setStyle(this.tcs1);
        c = row.getOrCreateCell(2);
        c.setFloatValue(this.fibonacci.nextInt(100000));
        c.setStyle(this.tcs2);
        c = row.getOrCreateCell(3);
        c.setFloatValue(this.fibonacci.nextInt(100000));
        c.setStyle(this.tcs3);

        // THIRD ROW
        row = table.getRow(2);
        row.getOrCreateCell(0).setBooleanValue(true);
        row.getOrCreateCell(1).setBooleanValue(true);
        c = row.getOrCreateCell(2);
        c.setCurrencyValue(150.5, "EUR");
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        cal.setTimeInMillis(1234567891011L);
        row.getOrCreateCell(3).setDateValue(cal);
        row.getOrCreateCell(4).setPercentageValue(70.3);
        row.getOrCreateCell(5).setStringValue("foobar");

        // FOURTH ROW
        row = table.getRow(3);
        row.getOrCreateCell(0).setColumnsSpanned(2);
        row.getOrCreateCell(1).setCurrencyValue(-150.5, "€");
        c = row.getOrCreateCell(2);
        c.setStringValue("x");
        c.setStyle(this.tcls);
        row.getOrCreateCell(3).setTimeValue(3 * 60 * 1000);
        c = row.getOrCreateCell(4);
        c.setStringValue("formula result");
        c.setFormula("1+1");

        // FIFTH ROW
        row = table.getRow(4);
        row.getOrCreateCell(0).setTooltip("That's a <tooltip>\nwith a newline !", SimpleLength.cm(20.0), SimpleLength.cm(10.0), true);

        document.addAutofilter(table, 0, 0, 3, 5);
    }

    private void standardDocumentWithFlush() throws FastOdsException, IOException {
        final OdsFileWriter writer =
                this.odsFactory.createWriter(new File("generated_files", STANDARD_DOCUMENT_WITH_FLUSH_ODS));
        final OdsDocument document = writer.document();

        this.createStyles();
        document.addObjectStyle(this.trs);
        document.addObjectStyle(this.tcls);
        document.addObjectStyle(this.tcns);
        document.addObjectStyle(this.tcs0);
        document.addObjectStyle(this.tcs1);
        document.addObjectStyle(this.tcs2);
        document.addObjectStyle(this.tcs3);

        document.addChildCellStyle(this.tcs0, TableCell.Type.FLOAT);
        document.addChildCellStyle(this.tcs1, TableCell.Type.FLOAT);
        document.addChildCellStyle(this.tcs2, TableCell.Type.FLOAT);
        document.addChildCellStyle(this.tcs3, TableCell.Type.FLOAT);
        document.addChildCellStyle(this.tcls, TableCell.Type.FLOAT);
        document.addChildCellStyle(TableCell.Type.BOOLEAN);
        document.addChildCellStyle(TableCell.Type.CURRENCY);
        document.addChildCellStyle(TableCell.Type.FLOAT);
        document.addChildCellStyle(TableCell.Type.DATE);
        document.addChildCellStyle(TableCell.Type.PERCENTAGE);
        document.addChildCellStyle(TableCell.Type.TIME);
        document.freezeStyles(); // if this crashes, use debugStyles to log the errors

        this.fillDocument(document);

        document.save();
    }
}