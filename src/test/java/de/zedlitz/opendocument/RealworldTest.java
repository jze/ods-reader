package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RealworldTest {

    /**
     * Tests the use of the Consumers for the various classes.
     */
    @Test
    public void consumer() throws Exception {
        final Document doc = new Document(getClass().getResourceAsStream("/test01.ods"));

        AtomicInteger numberOfTables = new AtomicInteger(0);
        AtomicInteger numberOfRows = new AtomicInteger(0);
        AtomicInteger numberOfCells = new AtomicInteger(0);

        doc.eachTable(table -> {
            numberOfTables.incrementAndGet();

            table.eachRow(row -> {
                numberOfRows.incrementAndGet();

                row.eachCell(cell -> numberOfCells.incrementAndGet());

            });
        });

        assertEquals(3, numberOfTables.get());
        assertEquals(6, numberOfRows.get());
        assertEquals(26, numberOfCells.get());
    }

    /**
     * Test the access to a cell by its number
     */
    @Test
    public void directCellAccess() throws XMLStreamException, IOException {
        final Document doc = new Document(getClass().getResourceAsStream("/test01.ods"));
        Table table = doc.nextTable();
        Row row1 = table.nextRow();

        Cell cellC1 = row1.getAt(2);
        Cell cellB1 = row1.getAt(1);
        Cell cellA1 = row1.getAt(0);
        Cell cellE1 = row1.getAt(4);

        assertEquals("A1", cellA1.getContent());
        assertEquals("b1", cellB1.getContent());
        assertEquals("c1", cellC1.getContent());
        assertEquals("e1", cellE1.getContent());

        // The second line is only sparsely filled.
        Row row2 = table.nextRow();
        Cell cellC2 = row2.getAt(2);
        Cell cellB2 = row2.getAt(1);
        Cell cellA2 = row2.getAt(0);
        Cell cellE2 = row2.getAt(4);

        assertEquals("2", cellA2.getContent());
        assertEquals("b2", cellB2.getContent());
        assertEquals("", cellC2.getContent());
        assertEquals("e2", cellE2.getContent());
    }

    @Test
    public void noOdsFile() throws XMLStreamException, IOException {
        final Document doc = new Document(getClass().getResourceAsStream("/no-ods.zip"));
        assertNull(doc.nextTable());
    }

    @Test
    public void iterators() throws XMLStreamException, IOException {
        AtomicInteger numberOfRows = new AtomicInteger(0);
        AtomicInteger numberOfCells = new AtomicInteger(0);

        final Document doc = new Document(getClass().getResourceAsStream("/test01.ods"));
        Table table = doc.nextTable();
        for (Row row : table) {
            numberOfRows.incrementAndGet();
            for (Cell cell : row) {
                numberOfCells.incrementAndGet();
            }
        }

        assertEquals(4, numberOfRows.get());
        assertEquals(19, numberOfCells.get());
    }

    @Test
    public void differentFormats() throws Exception {
        List<String> expectedRow1 = Arrays.asList("text", "number", "thousand point", "date", "percentage", "currency", "time", "floating point number");
        List<String> expectedRow2 = Arrays.asList("abc", "7392", "5.039", "2024-07-01", "5,63 %", "78,34 €", "08:35", "54,143662");

        final Document doc = new Document(getClass().getResourceAsStream("/formats.ods"));
        Table table = doc.nextTable();

        List<String> row1 = table.nextRow().openStream().map(Cell::getRawValue).collect(Collectors.toList());
        List<String> row2 = table.nextRow().openStream().map(Cell::getRawValue).collect(Collectors.toList());
        assertEquals(expectedRow1, row1);
        assertEquals(expectedRow2, row2);

        assertNull(table.nextRow());
        assertNull(doc.nextTable());
    }

    @Test
    public void moreFormats() throws Exception {

        final Document doc = new Document(getClass().getResourceAsStream("/formats2.ods"));
        Table table = doc.nextTable();

        Row row = table.nextRow();
        while( row != null) {
            Cell cell =        row.nextCell();
            row = table.nextRow();
        }


    }
}
