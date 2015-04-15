package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;


/**
 * @author jzedlitz
 *
 */
public class EmptyCell extends Cell {
    /**
     * @param parser
     */
    EmptyCell() {
        super(null);
    }

    /**
     * @see de.freenet.jzedlitz.oo.Cell#getContent()
     */
    public String getContent() {
        return StringUtils.EMPTY;
    }

    /**
     * @see de.freenet.jzedlitz.oo.Cell#getValueType()
     */
    public String getValueType() {
        return Cell.TYPE_UNDEFINED;
    }

    /**
      * @see de.zedlitz.opendocument.Cell#getNumberColumnsRepeated()
      */
    @Override
    public int getNumberColumnsRepeated() {
        return 0;
    }
}
