package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;


/**
 * @author jzedlitz
 *
 */
public class RowTest extends AbstractBaseTest {
    /**
     * A row without any cells.
     */
    private static final String CONTENT_EMPTY_ROW =
        "<table:table-row table:style-name=\"ro1\"" +
        " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'>" +
        "</table:table-row>";

    /**
     * Row consists of three empty cells.
     */
    private static final String CONTENT_3_EMPTY_CELLS =
        "<table:table-row table:style-name=\"ro1\"" +
        " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'>" +
        "<table:table-cell/><table:table-cell></table:table-cell><table:table-cell/>" +
        "</table:table-row>";

    /**
     * Three empty cell that are summed up in one element.
     */
    private static final String CONTENT_CELLS_REPEATED =
        "<table:table-row table:style-name=\"ro1\"" +
        " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'>" +
        "<table:table-cell table:number-columns-repeated=\"3\"/>" +
        "</table:table-row>";

    /**
     * Two empty cells, one string, one number, one formula
     */
    private static final String CONTENT_MIXED =
        "<table:table-row table:style-name=\"ro1\"" +
        " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
        " xmlns:text='urn:oasis:names:tc:opendocument:xmlns:text:1.0'" +
        " xmlns:office='urn:oasis:names:tc:opendocument:xmlns:office:1.0'>" +
        "<table:table-cell table:number-columns-repeated=\"2\"/>" +
        "<table:table-cell office:value-type=\"string\"><text:p>s</text:p>" +
        "</table:table-cell><table:table-cell office:value-type=\"float\"" +
        " office:value=\"2\"><text:p>2</text:p></table:table-cell>" +
        "<table:table-cell table:formula=\"oooc:=1+2\" " +
        "office:value-type=\"float\" office:value=\"3\"><text:p>3</text:p>" +
        "</table:table-cell></table:table-row>";

    /**
     * One cell of the row contains a note.
     */
    private static final String CONTENT_NOTE =
        "<table:table-row table:style-name='ro1'" +
        " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
        " xmlns:text='urn:oasis:names:tc:opendocument:xmlns:text:1.0'" +
        " xmlns:office='urn:oasis:names:tc:opendocument:xmlns:office:1.0'" +
        " xmlns:svg='urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0'" +
        " xmlns:draw='urn:oasis:names:tc:opendocument:xmlns:drawing:1.0'" +
        " xmlns:dc='http://purl.org/dc/elements/1.1/'>" +
        "<table:table-cell office:value-type='string'><text:p>A1</text:p>" +
        "</table:table-cell><table:table-cell office:value-type='string'>" +
        "<office:annotation draw:style-name='gr1' draw:text-style-name='P1' " +
        "svg:width='2.899cm' svg:height='0.599cm' svg:x='5.116cm' svg:y='0.001cm' " +
        "draw:caption-point-x='-5.116cm' draw:caption-point-y='-0.001cm'>" +
        "<dc:creator>jze</dc:creator><dc:date>2006-11-08T00:00:00</dc:date>" +
        "<text:p text:style-name='P1'>NoteB1</text:p></office:annotation>" +
        "<text:p>B1</text:p></table:table-cell>" +
        "<table:table-cell office:value-type='string'><text:p>C1</text:p>" +
        "</table:table-cell></table:table-row>";

    public void testEmptyRow() throws Exception {
        final Row row =
            new Row(advanceToStartTag(createParser(CONTENT_EMPTY_ROW)));

        assertNull("row has no cells", row.nextCell());
        assertNull("second call ok", row.nextCell());
    }

    public void testEmptyCells() throws Exception {
        final Row row =
            new Row(advanceToStartTag(createParser(CONTENT_3_EMPTY_CELLS)));

        assertNotNull("1st cell ok", row.nextCell());
        assertNotNull("2nd cell ok", row.nextCell());
        assertNotNull("3rd cell ok", row.nextCell());
        assertNull("no 4th cell", row.nextCell());
    }

    public void testEmptyCellsRepeated() throws Exception {
        final Row row =
            new Row(advanceToStartTag(createParser(CONTENT_CELLS_REPEATED)));

        assertNotNull("1st cell ok", row.nextCell());
        assertNotNull("2nd cell ok", row.nextCell());
        assertNotNull("3rd cell ok", row.nextCell());
        assertNull("no 4th cell", row.nextCell());
    }

    public void testMixedContent() throws Exception {
        final Row row =
            new Row(advanceToStartTag(createParser(CONTENT_MIXED)));

        final Cell cell1 = row.nextCell();
        assertNotNull("1st cell ok", cell1);
        assertEquals("1st cell empty", StringUtils.EMPTY, cell1.getContent());
        assertEquals("1st cell correct type", Cell.TYPE_UNDEFINED,
            cell1.getValueType());

        final Cell cell2 = row.nextCell();
        assertNotNull("2nd cell ok", cell2);
        assertEquals("2nd cell empty", StringUtils.EMPTY, cell2.getContent());
        assertEquals("2nd cell correct type", Cell.TYPE_UNDEFINED,
            cell2.getValueType());

        final Cell cell3 = row.nextCell();
        assertNotNull("3rd cell ok", cell3);
        assertEquals("3rd cell correct type", "string", cell3.getValueType());
        assertEquals("3rd cell correct value", "s", cell3.getContent());

        final Cell cell4 = row.nextCell();
        assertNotNull("4th cell ok", cell4);
        assertEquals("4th cell correct type", "float", cell4.getValueType());
        assertEquals("4th cell correct value", "2", cell4.getContent());

        final Cell cell5 = row.nextCell();
        assertNotNull("5th cell ok", cell5);
        assertEquals("5th cell correct type", "float", cell5.getValueType());
        assertEquals("5th cell correct value", "3", cell5.getContent());
    }

    /**
     * If you are not interesed in a note it will not be added to the cell's
     * content.
     * @throws IOException
     * @throws Exception
     *
     */
    public void testIgnoreNote() throws Exception {
        final Row row =
            new Row(advanceToStartTag(createParser(CONTENT_NOTE)));

        final Cell cell1 = row.nextCell();
        assertNotNull("1st cell ok", cell1);
        assertEquals("1st cell correct value", "A1", cell1.getContent());

        final Cell cell2 = row.nextCell();
        assertNotNull("2nd cell ok", cell2);
        assertEquals("2nd cell correct value", "B1", cell2.getContent());

        final Cell cell3 = row.nextCell();
        assertNotNull("3rd cell ok", cell3);
        assertEquals("3rd cell correct value", "C1", cell3.getContent());
    }
}
