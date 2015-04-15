package de.zedlitz.opendocument;


/**
 * @author jzedlitz
 *
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

    public void testEmptyTable() throws Exception {
        final Table table =
            new Table(advanceToStartTag(createParser(CONTENT_EMPTY_TABLE)));
        assertEquals("table has a name", "Tabelle1", table.getName());
        assertNull("table has no row", table.nextRow());
        assertNull("second call ok", table.nextRow());
    }

    public void testTwoEmptyRows() throws Exception {
        final Table table =
            new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));
        assertNotNull("1st row ok", table.nextRow());
        assertNotNull("2nd row ok", table.nextRow());
        assertNull("no 3rd row", table.nextRow());
    }

    public void testTwoEmptyRowsCheckCells() throws Exception {
        final Table table =
            new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));

        final Row row1 = table.nextRow();
        assertNotNull("1st row ok", row1);
        assertNull("no cell in 1st row", row1.nextCell());
        assertNull("second call ok", row1.nextCell());

        final Row row2 = table.nextRow();
        assertNotNull("2nd row ok", row2);
        assertNull("no cell in 2nd row", row2.nextCell());
        assertNull("second call ok", row2.nextCell());

        assertNull("no 3rd row", table.nextRow());
    }
}
