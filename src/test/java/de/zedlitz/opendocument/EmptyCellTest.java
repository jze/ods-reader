package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmptyCellTest {

    private final Cell cell = new EmptyCell(new DummyRow(), 0);

    @Test
    void getContent() {
        assertEquals("", cell.getContent());
    }

    @Test
    void getValueType() {
        assertEquals("undefined", cell.getValueType());
    }

    @Test
    void getNumberColumnsRepeated() {
        assertEquals(0, cell.getNumberColumnsRepeated());
    }
}