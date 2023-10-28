package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jzedlitz
 */
public class TableTest extends AbstractBaseTest {
    private static final String CONTENT_EMPTY_TABLE =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">" +
                    "</table:table>";
    private static final String CONTENT_TWO_EMPTY_ROWS =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">" +
                    "<table:table-row/><table:table-row/>" + "</table:table>";

    @Test
    public void testEmptyTable() throws Exception {
        final Table table =
                new Table(advanceToStartTag(createParser(CONTENT_EMPTY_TABLE)));
        assertEquals("Tabelle1", table.getName(), "table has a name");
        assertNull(table.nextRow(), "table has no row");
        assertNull(table.nextRow(), "second call ok");
    }

    @Test
    public void testTwoEmptyRows() throws Exception {
        final Table table =
                new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));
        assertNotNull(table.nextRow(), "1st row ok");
        assertNotNull(table.nextRow(), "2nd row ok");
        assertNull(table.nextRow(), "no 3rd row");
    }

    @Test
    public void testTwoEmptyRowsCheckCells() throws Exception {
        final Table table =
                new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));

        final Row row1 = table.nextRow();
        assertNotNull(row1, "1st row ok");
        assertNull(row1.nextCell(), "no cell in 1st row");
        assertNull(row1.nextCell(), "second call ok");

        final Row row2 = table.nextRow();
        assertNotNull(row2, "2nd row ok");
        assertNull(row2.nextCell(), "no cell in 2nd row");
        assertNull(row2.nextCell(), "second call ok");

        assertNull(table.nextRow(), "no 3rd row");
    }
}
