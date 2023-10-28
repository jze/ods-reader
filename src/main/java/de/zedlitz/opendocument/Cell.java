/**
 *
 */
package de.zedlitz.opendocument;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 */
public class Cell {
    protected final static String TYPE_UNDEFINED = "undefined";
    final static QName ELEMENT_CELL =
            new QName(Document.NS_TABLE, "table-cell");
    private final static QName ELEMENT_ANNOTATION =
            new QName(Document.NS_OFFICE, "annotation");
    private final static String ATTRIBUTE_VALUE_TYPE = "value-type";
    private final static String ATTRIBUTE_NUMBER_COLUMNS_REPEATED =
            "number-columns-repeated";
    private final StringBuffer content = new StringBuffer();
    private String valueType;
    private int numberColumnsRepeated;

    Cell(final XMLStreamReader parser) {
        if (parser == null) {
            return;
        }

        this.valueType = StringUtils.defaultIfEmpty(parser.getAttributeValue(
                        Document.NS_OFFICE, Cell.ATTRIBUTE_VALUE_TYPE),
                Cell.TYPE_UNDEFINED);
        this.numberColumnsRepeated = NumberUtils.toInt(parser.getAttributeValue(
                Document.NS_TABLE, Cell.ATTRIBUTE_NUMBER_COLUMNS_REPEATED));

        /*
         * extract content
         */
        try {
            int eventType = parser.getEventType();

            while (!((eventType == XMLStreamConstants.END_ELEMENT) &&
                    Cell.ELEMENT_CELL.equals(parser.getName()))) {
                if ((eventType == XMLStreamConstants.START_ELEMENT) &&
                        Cell.ELEMENT_ANNOTATION.equals(parser.getName())) {
                    // skip note
                    skipNote(parser);
                } else if (eventType == XMLStreamConstants.CHARACTERS) {
                    this.content.append(parser.getText());
                }

                eventType = parser.next();
            }
        } catch (final XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void skipNote(final XMLStreamReader parser)
            throws XMLStreamException {
        int eventType = parser.getEventType();

        while (!((eventType == XMLStreamConstants.END_ELEMENT) &&
                Cell.ELEMENT_ANNOTATION.equals(parser.getName()))) {
            eventType = parser.next();
        }
    }

    /**
     * @return Returns the valueType.
     */
    public String getValueType() {
        return this.valueType;
    }

    /**
     * @see de.zedlitz.opendocument.Cell#getContent()
     */
    public String getContent() {
        return this.content.toString();
    }

    public int getNumberColumnsRepeated() {
        return this.numberColumnsRepeated;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getContent();
    }
}
