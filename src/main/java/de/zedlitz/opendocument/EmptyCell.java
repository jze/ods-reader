package de.zedlitz.opendocument;

import javax.xml.stream.XMLStreamReader;

/**
 * @author jzedlitz
 */
public class EmptyCell extends Cell {

    EmptyCell(Row row, int columnIndex) {
        super((XMLStreamReader) null, row, columnIndex);
    }

    public String getContent() {
        return "";
    }

    public String getValueType() {
        return Cell.TYPE_UNDEFINED;
    }

    @Override
    public int getNumberColumnsRepeated() {
        return 0;
    }
}
