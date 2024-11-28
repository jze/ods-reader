package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CellTest extends AbstractBaseTest {
    private static final String CONTENT_EMPTY_CELL =
            "<table:table-cell xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'></table:table-cell>";

    private static final String BROKEN_XML_CONTENT =
            "<table:table-cell xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'><WRONG></table:table-cell>";

    @Test
    public void empty() throws Exception {
        final Cell cell = new Cell(advanceToStartTag(createParser(CONTENT_EMPTY_CELL)));

        assertEquals(StringUtils.EMPTY, cell.getContent());
        assertEquals(StringUtils.EMPTY, cell.toString());
    }

    @Test
    public void brokenXmlContent() {
        assertThrows(RuntimeException.class, () -> new Cell(advanceToStartTag(createParser(BROKEN_XML_CONTENT))));
    }
}
