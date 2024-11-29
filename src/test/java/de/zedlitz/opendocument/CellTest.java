package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CellTest extends AbstractBaseTest {
    private static final String CONTENT_EMPTY_CELL =
            "<table:table-cell xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'></table:table-cell>";

    private static final String BROKEN_XML_CONTENT =
            "<table:table-cell xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'><WRONG></table:table-cell>";

    @Test
    public void empty() throws Exception {
        final Cell cell = new Cell(advanceToStartTag(createParser(CONTENT_EMPTY_CELL)), new DummyRow(), 0);

        assertEquals(StringUtils.EMPTY, cell.getContent());
        assertEquals(StringUtils.EMPTY, cell.toString());
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

    @Test
    public void testGetType() throws XMLStreamException, IOException {
        Row row = getRowFromDemoFile();
        assertEquals(CellType.STRING, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
        assertEquals(CellType.NUMBER, row.nextCell().getType());
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
        assertEquals("abc", row.nextCell().getText());
        assertEquals("7392", row.nextCell().getText());
        assertEquals("5.039", row.nextCell().getText());
        assertEquals("2024-07-01", row.nextCell().getText());
        assertEquals("5,63 %", row.nextCell().getText());
        assertEquals("78,34 €", row.nextCell().getText());
        assertEquals("08:35", row.nextCell().getText());
        assertEquals("54,143662", row.nextCell().getText());
    }

    @Test
    public void testAsDate() throws Exception {
        Row row = getRowFromDemoFile();
        assertEquals(LocalDate.of(2024,7,1), row.getAt(3).asDate());

    }

}
