package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jzedlitz
 */
public class DocumentTest extends AbstractBaseTest {

    private static final String CONTENT_ONE_EMPTY_TABLE = "<office:document-content"
            + " xmlns:office='urn:oasis:names:tc:opendocument:xmlns:office:1.0'"
            + " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'>"
            + "<office:body><office:spreadsheet>"
            + "<table:table table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">"
            + "</table:table></office:spreadsheet></office:body></office:document-content>";

    private static final String CONTENT_ONE_TWO_ROWS = "<office:document-content"
            + " xmlns:office='urn:oasis:names:tc:opendocument:xmlns:office:1.0'"
            + " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'>"
            + "<office:body><office:spreadsheet>"
            + "<table:table table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">"
            + "<table:table-row/><table:table-row><table:table-cell /></table:table-row>"
            + "</table:table><table:table table:name=\"Tabelle2\" table:style-name=\"ta1\" table:print=\"false\"/>"
            + "</office:spreadsheet></office:body></office:document-content>";

    @Test
    public void testEmptyTable() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_EMPTY_TABLE));
        assertNotNull(doc.nextTable(), "one table");
        assertNull(doc.nextTable(), "no second table");
        assertNull(doc.nextTable(), "no second table");
    }

    @Test
    public void testEmptyTableCheckRows() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_EMPTY_TABLE));
        final Table tab = doc.nextTable();
        assertNotNull(tab, "one table");
        assertNull(tab.nextRow(), "no row");
        assertNull(doc.nextTable(), "no second table");
    }

    @Test
    public void testSkipRows() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_TWO_ROWS));

        final Table tab1 = doc.nextTable();
        assertNotNull(tab1, "1st table");
        assertEquals("Tabelle1", tab1.getName(), "1st table correct name");

        final Table tab2 = doc.nextTable();
        assertNotNull(tab2, "2nd table");
        assertEquals("Tabelle2", tab2.getName(), "2nd table correct name");
    }

    @Test
    public void testReadRows() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_TWO_ROWS));

        final Table tab1 = doc.nextTable();
        assertNotNull(tab1, "1st table");
        assertEquals("Tabelle1", tab1.getName(), "1st table correct name");

        assertNotNull(tab1.nextRow(), "1st row ok");
        assertNotNull(tab1.nextRow(), "2nd row");
        assertNull(tab1.nextRow(), "no 3rd row");
        assertNull(tab1.nextRow(), "no 4th row");

        final Table tab2 = doc.nextTable();
        assertNotNull(tab2, "2nd table");
        assertEquals("Tabelle2", tab2.getName(), "2nd table correct name");
    }

    @Test
    public void testRealDocument() throws Exception, IOException {
        final ZipFile file = new ZipFile(getClass().getResource("/test01.ods")
                .getFile());

        final Document doc = new Document(file);

        final Table table1 = doc.nextTable();
        assertNotNull(table1, "1st table exits");
        assertEquals("Tabelle1", table1.getName(), "correct name");
        final Row row1 = table1.nextRow();
        assertNotNull(row1, "1st row");
        checkRow(new String[]{"A1", "b1", "c1", "d1", "e1", "f1"}, row1);

        final Row row2 = table1.nextRow();
        assertNotNull(row2, "2nd row");
        checkRow(new String[]{"2", "b2", "", "", "e2", ""}, row2);

        final Row row3 = table1.nextRow();
        assertNotNull(row3, "3rd row");
        checkRow(new String[]{"3", "4", "c3", "", "", "f3"}, row3);

        final Row row4 = table1.nextRow();
        assertNotNull(row4, "4th row");
        checkRow(new String[]{"", "b4", "", "", "", ""}, row4);

        assertNull(table1.nextRow(), "no 5th row");

        final Table table2 = doc.nextTable();
        assertNotNull(table2, "2nd table exits");
        assertEquals("Tabelle2", table2.getName(), "correct name");
        assertNotNull(table2.nextRow(), "1st row");
        assertNull(table2.nextRow(), "no 2nd row");

        final Table table3 = doc.nextTable();
        assertNotNull(table3, "3rd table exits");
        assertEquals("Tabelle3", table3.getName(), "correct name");
        assertNotNull(table3.nextRow(), "1st row");
        assertNull(table3.nextRow(), "no 2nd row");

        assertNull(doc.nextTable(), "no 4th table");
    }

    @Test
    public void testRealDocumentInputStream() throws Exception,
            IOException {
        final Document doc = new Document(getClass()
                .getResourceAsStream("/test01.ods"));
        final Table table1 = doc.nextTable();
        assertNotNull(table1, "1st table exits");
        assertEquals("Tabelle1", table1.getName(), "correct name");
    }

    private void checkRow(final String[] content, final Row row) {
        for (String s : content) {
            final Cell cell = row.nextCell();
            assertNotNull(cell, "cell not null");
            assertEquals(s, cell.getContent(), "correct content");
        }

    }
}
