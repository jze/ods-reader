package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest extends AbstractBaseTest {
    private static final String CONTENT_EMPTY_CELL =
            "<table:table-cell xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'></table:table-cell>";

    /**
     * A date cell without a date-value attribute. I don't think this situation can happen in real live.
     */
    private static final String CONTENT_MISSING_DATE_VALUE =
            "<table:table-cell  office:value-type=\"date\" xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'></table:table-cell>";

    /**
     * A time cell without a time-value attribute. I don't think this situation can happen in real live.
     */
    private static final String CONTENT_MISSING_TIME_VALUE =
            "<table:table-cell  office:value-type=\"time\" xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'></table:table-cell>";

    /**
     * A boolean cell without a time-value attribute. I don't think this situation can happen in real live.
     */
    private static final String CONTENT_MISSING_BOOLEAN_VALUE =
            "<table:table-cell  office:value-type=\"boolean\" xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'></table:table-cell>";

    private static final String BROKEN_XML_CONTENT =
            "<table:table-cell xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'><WRONG></table:table-cell>";

    @Test
    public void empty() throws Exception {
        final Cell cell = new Cell(advanceToStartTag(createParser(CONTENT_EMPTY_CELL)), new DummyRow(), 0);

        assertEquals(StringUtils.EMPTY, cell.getContent());
        assertEquals("[undefined \"\"]", cell.toString());
    }

    @Test
    public void brokenXmlContent() {
        assertThrows(RuntimeException.class, () -> new Cell(advanceToStartTag(createParser(BROKEN_XML_CONTENT)), new DummyRow(), 0));
    }

    private Row getRowFromDemoFile() throws XMLStreamException, IOException {
        final Document doc = new Document(getClass().getResourceAsStream("/formats.ods"));
        Table table = doc.nextTable();
        table.nextRow(); // skip header line
        return table.nextRow();
    }

    /**
     * In this demo file all the interesting cells are beneath each other in the first column.
     */
    private List<Cell> getCellsFromDemoFile(String resourceName) throws XMLStreamException, IOException {
        List<Cell> cells = new ArrayList<>();

        final Document doc = new Document(getClass().getResourceAsStream(resourceName));
        Table table = doc.nextTable();
        Row row = table.nextRow();

        while (row != null) {
            cells.add(row.nextCell());
            row = table.nextRow();
        }
        return cells;
    }

    @Test
    public void testGetValueType() throws Exception {
        Row row = getRowFromDemoFile();
        assertEquals("string", row.nextCell().getValueType());
        assertEquals("float", row.nextCell().getValueType());
        assertEquals("float", row.nextCell().getValueType());
        assertEquals("date", row.nextCell().getValueType());
        assertEquals("percentage", row.nextCell().getValueType());
        assertEquals("currency", row.nextCell().getValueType());
        assertEquals("time", row.nextCell().getValueType());
        assertEquals("float", row.nextCell().getValueType());
    }

    @Test
    public void testGetColumnIndex() throws Exception {
        Row row = getRowFromDemoFile();
        for (int i = 0; i <= 7; i++) {
            assertEquals(i, row.nextCell().getColumnIndex());
        }
    }

    @Test
    public void testGetAddress() throws Exception {
        Row row = getRowFromDemoFile();
        assertEquals("A2", row.nextCell().getAddress());
        assertEquals("B2", row.nextCell().getAddress());
        assertEquals("C2", row.nextCell().getAddress());
        assertEquals("D2", row.nextCell().getAddress());
        assertEquals("E2", row.nextCell().getAddress());
        assertEquals("F2", row.nextCell().getAddress());
        assertEquals("G2", row.nextCell().getAddress());
        assertEquals("H2", row.nextCell().getAddress());
    }

    @Test
    public void testGetText() throws Exception {
        Row row = getRowFromDemoFile();
        assertEquals("abc", row.nextCell().getContent());
        assertEquals("7392", row.nextCell().getContent());
        assertEquals("5.039", row.nextCell().getContent());
        assertEquals("2024-07-01", row.nextCell().getContent());
        assertEquals("5,63 %", row.nextCell().getContent());
        assertEquals("78,34 €", row.nextCell().getContent());
        assertEquals("08:35", row.nextCell().getContent());
        assertEquals("54,143662", row.nextCell().getContent());
    }

    @Test
    public void testAsDate() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertThrows(OdsReaderException.class, () -> cells.get(0).asDate());
        assertEquals(LocalDate.of(1999, 12, 31), cells.get(11).asDate());
    }

    @Test
    public void testAsDateTime() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertThrows(OdsReaderException.class, () -> cells.get(0).asDateTime());
        // 1999-12-31
        assertEquals(LocalDateTime.of(1999, 12, 31, 0, 0, 0), cells.get(11).asDateTime());
        // 1999-12-31T07:35:02
        assertEquals(LocalDateTime.of(1999, 12, 31, 7, 35, 2), cells.get(22).asDateTime());
        // 1899-12-30T13:37:46
        assertEquals(LocalDateTime.of(1899, 12, 30, 13, 37, 46), cells.get(28).asDateTime());
    }

    @Test
    public void testGetDateValue() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertNull(cells.get(0).getDateValue());
        assertEquals("1999-12-31", cells.get(11).getDateValue());
        assertEquals("1999-12-31T07:35:02", cells.get(22).getDateValue());
        assertEquals("1899-12-30T13:37:46", cells.get(28).getDateValue());
    }

    @Test
    public void testIsDateTime() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertFalse(cells.get(0).isDateTime());
        assertFalse(cells.get(11).isDateTime());
        assertTrue(cells.get(22).isDateTime());
        assertTrue(cells.get(28).isDateTime());
    }

    @Test
    public void testAsBoolean() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_french.ods");
        assertThrows(OdsReaderException.class, () -> cells.get(0).asBoolean());
        assertTrue(cells.get(33).asBoolean());
        assertFalse(cells.get(34).asBoolean());
    }

    @Test
    public void testGetBoolean() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_french.ods");
        assertNull(cells.get(0).getBooleanValue());
        assertEquals("true", cells.get(33).getBooleanValue());
        assertEquals("false", cells.get(34).getBooleanValue());
    }

    @Test
    public void testGetCurrency() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertNull(cells.get(0).getCurrency());
        assertEquals("EUR", cells.get(8).getCurrency());
        assertEquals("DEM", cells.get(9).getCurrency());
    }

    @Test
    public void testAsTime() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertThrows(OdsReaderException.class, () -> cells.get(0).asTime());
        assertEquals(LocalTime.of(13, 37, 46), cells.get(26).asTime());
    }

    @Test
    public void testGetTimeValue() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_german.ods");
        assertNull(cells.get(0).getTimeValue());
        assertEquals("PT13H37M46S", cells.get(26).getTimeValue());
    }

    @Test
    public void testGetValue() throws Exception {
        List<Cell> cells = getCellsFromDemoFile("/formats_french.ods");
        assertEquals("50000", cells.get(0).getValue());
        assertEquals("0.1295", cells.get(7).getValue());
        assertEquals("120.5", cells.get(8).getValue());
    }

    @Test
    public void invalidCells() throws XMLStreamException {
        Cell cell = new Cell(advanceToStartTag(createParser(CONTENT_MISSING_DATE_VALUE)), new DummyRow(), 0);
        assertThrows(OdsReaderException.class, cell::asDate);
        assertThrows(OdsReaderException.class, cell::asDateTime);

        cell = new Cell(advanceToStartTag(createParser(CONTENT_MISSING_TIME_VALUE)), new DummyRow(), 0);
        assertThrows(OdsReaderException.class, cell::asTime);

        cell = new Cell(advanceToStartTag(createParser(CONTENT_MISSING_BOOLEAN_VALUE)), new DummyRow(), 0);
        assertThrows(OdsReaderException.class, cell::asBoolean);

    }
}
