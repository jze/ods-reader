package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Row row2 =     table.nextRow();
        Cell cellC2 = row2.getAt(2);
        Cell cellB2 = row2.getAt(1);
        Cell cellA2 = row2.getAt(0);
        Cell cellE2 = row2.getAt(4);

        assertEquals("2", cellA2.getContent());
        assertEquals("b2", cellB2.getContent());
        assertEquals("", cellC2.getContent());
        assertEquals("e2", cellE2.getContent());

    }
}
