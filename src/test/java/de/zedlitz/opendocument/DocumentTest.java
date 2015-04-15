package de.zedlitz.opendocument;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * @author jzedlitz
 * 
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

    public void testEmptyTable() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_EMPTY_TABLE));
        assertNotNull("one table", doc.nextTable());
        assertNull("no second table", doc.nextTable());
        assertNull("no second table", doc.nextTable());
    }

    public void testEmptyTableCheckRows() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_EMPTY_TABLE));
        final Table tab = doc.nextTable();
        assertNotNull("one table", tab);
        assertNull("no row", tab.nextRow());
        assertNull("no second table", doc.nextTable());
    }

    public void testSkipRows() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_TWO_ROWS));

        final Table tab1 = doc.nextTable();
        assertNotNull("1st table", tab1);
        assertEquals("1st table correct name", "Tabelle1", tab1.getName());

        final Table tab2 = doc.nextTable();
        assertNotNull("2nd table", tab2);
        assertEquals("2nd table correct name", "Tabelle2", tab2.getName());
    }

    public void testReadRows() throws Exception {
        final Document doc = new Document(this
                .createParser(CONTENT_ONE_TWO_ROWS));

        final Table tab1 = doc.nextTable();
        assertNotNull("1st table", tab1);
        assertEquals("1st table correct name", "Tabelle1", tab1.getName());

        assertNotNull("1st row ok", tab1.nextRow());
        assertNotNull("2nd row", tab1.nextRow());
        assertNull("no 3rd row", tab1.nextRow());
        assertNull("no 4th row", tab1.nextRow());

        final Table tab2 = doc.nextTable();
        assertNotNull("2nd table", tab2);
        assertEquals("2nd table correct name", "Tabelle2", tab2.getName());
    }

    public void testRealDocument() throws Exception, IOException {
        final ZipFile file = new ZipFile(getClass().getResource("/test01.ods")
                .getFile());

        final Document doc = new Document(file);

        final Table table1 = doc.nextTable();
        assertNotNull("1st table exits", table1);
        assertEquals("correct name", "Tabelle1", table1.getName());
        final Row row1 = table1.nextRow();
        assertNotNull("1st row", row1);
        checkRow(new String[] { "A1", "b1", "c1", "d1", "e1", "f1" }, row1);

        final Row row2 = table1.nextRow();
        assertNotNull("2nd row", row2);
        checkRow(new String[] { "2", "b2", "", "", "e2", "" }, row2);

        final Row row3 = table1.nextRow();
        assertNotNull("3rd row", row3);
        checkRow(new String[] { "3", "4", "c3", "", "", "f3" }, row3);

        final Row row4 = table1.nextRow();
        assertNotNull("4th row", row4);
        checkRow(new String[] { "", "b4", "", "", "", "" }, row4);

        assertNull("no 5th row", table1.nextRow());

        final Table table2 = doc.nextTable();
        assertNotNull("2nd table exits", table2);
        assertEquals("correct name", "Tabelle2", table2.getName());
        assertNotNull("1st row", table2.nextRow());
        assertNull("no 2nd row", table2.nextRow());

        final Table table3 = doc.nextTable();
        assertNotNull("3rd table exits", table3);
        assertEquals("correct name", "Tabelle3", table3.getName());
        assertNotNull("1st row", table3.nextRow());
        assertNull("no 2nd row", table3.nextRow());

        assertNull("no 4th table", doc.nextTable());
    }

    public void testRealDocumentInputStream() throws Exception,
            IOException {
        final Document doc = new Document(getClass()
                .getResourceAsStream("/test01.ods"));
        final Table table1 = doc.nextTable();
        assertNotNull("1st table exits", table1);
        assertEquals("correct name", "Tabelle1", table1.getName());
    }

    /**
     * @param content
     * @param row1
     */
    private void checkRow(final String[] content, final Row row) {
        for (int i = 0; i < content.length; i++) {
            final Cell cell = row.nextCell();
            assertNotNull("cell not null", cell);
            assertEquals("correct content", content[i], cell.getContent());
        }

    }
}
