package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;


/**
 * @author jzedlitz
 */
public class EmptyCell extends Cell {

    EmptyCell() {
        super(null);
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
