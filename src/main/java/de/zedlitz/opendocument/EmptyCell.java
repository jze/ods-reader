package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;


/**
 * @author jzedlitz
 */
public class EmptyCell extends Cell {

    EmptyCell(Row row, int columnIndex) {
        super(null, row, columnIndex);
    }

    public String getContent() {
        return StringUtils.EMPTY;
    }

    public String getValueType() {
        return Cell.TYPE_UNDEFINED;
    }

    @Override
    public int getNumberColumnsRepeated() {
        return 0;
    }
}
