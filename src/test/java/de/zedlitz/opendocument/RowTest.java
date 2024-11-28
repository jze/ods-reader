package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jzedlitz
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

    private static final String BROKEN_XML_CONTENT=
            "<table:table-row table:style-name=\"ro1\"" +
                    " xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'>" ;
    @Test
    public void testEmptyRow() throws Exception {
        final Row row =  new Row(advanceToStartTag(createParser(CONTENT_EMPTY_ROW)));

        assertNull(row.nextCell(), "row has no cells");
        assertNull(row.nextCell(), "second call ok");
    }

    @Test
    public void testEmptyCells() throws Exception {
        final Row row = new Row(advanceToStartTag(createParser(CONTENT_3_EMPTY_CELLS)));

        assertNotNull(row.nextCell(), "1st cell ok");
        assertNotNull(row.nextCell(), "2nd cell ok");
        assertNotNull(row.nextCell(), "3rd cell ok");
        assertNull(row.nextCell(), "no 4th cell");
    }

    @Test
    public void testEmptyCellsRepeated() throws Exception {
        final Row row =   new Row(advanceToStartTag(createParser(CONTENT_CELLS_REPEATED)));

        assertNotNull(row.nextCell(), "1st cell ok");
        assertNotNull(row.nextCell(), "2nd cell ok");
        assertNotNull(row.nextCell(), "3rd cell ok");
        assertNull(row.nextCell(), "no 4th cell");
    }

    @Test
    public void testMixedContent() throws Exception {
        final Row row = new Row(advanceToStartTag(createParser(CONTENT_MIXED)));

        final Cell cell1 = row.nextCell();
        assertNotNull(cell1, "1st cell ok");
        assertEquals(StringUtils.EMPTY, cell1.getContent(), "1st cell empty");
        assertEquals(Cell.TYPE_UNDEFINED, cell1.getValueType(), "1st cell correct type");

        final Cell cell2 = row.nextCell();
        assertNotNull(cell2, "2nd cell ok");
        assertEquals(StringUtils.EMPTY, cell2.getContent(), "2nd cell empty");
        assertEquals(Cell.TYPE_UNDEFINED, cell2.getValueType(), "2nd cell correct type");

        final Cell cell3 = row.nextCell();
        assertNotNull(cell3, "3rd cell ok");
        assertEquals("string", cell3.getValueType(), "3rd cell correct type");
        assertEquals("s", cell3.getContent(), "3rd cell correct value");

        final Cell cell4 = row.nextCell();
        assertNotNull(cell4, "4th cell ok");
        assertEquals("float", cell4.getValueType(), "4th cell correct type");
        assertEquals("2", cell4.getContent(), "4th cell correct value");

        final Cell cell5 = row.nextCell();
        assertNotNull(cell5, "5th cell ok");
        assertEquals("float", cell5.getValueType(), "5th cell correct type");
        assertEquals("3", cell5.getContent(), "5th cell correct value");
    }

    /**
     * If you are not interesed in a note it will not be added to the cell's
     * content.
     */
    @Test
    public void testIgnoreNote() throws Exception {
        final Row row = new Row(advanceToStartTag(createParser(CONTENT_NOTE)));

        final Cell cell1 = row.nextCell();
        assertNotNull(cell1, "1st cell ok");
        assertEquals("A1", cell1.getContent(), "1st cell correct value");

        final Cell cell2 = row.nextCell();
        assertNotNull(cell2, "2nd cell ok");
        assertEquals("B1", cell2.getContent(), "2nd cell correct value");

        final Cell cell3 = row.nextCell();
        assertNotNull(cell3, "3rd cell ok");
        assertEquals("C1", cell3.getContent(), "3rd cell correct value");
    }

    @Test
    public void brokenXmlContent() throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        final Row row = new Row(advanceToStartTag(createParser(BROKEN_XML_CONTENT)));

        Cell cell = row.nextCell();

        System.setErr(originalErr);

        assertNull(cell);
        assertTrue(errContent.toString().contains("XMLStreamException"));
    }
}
