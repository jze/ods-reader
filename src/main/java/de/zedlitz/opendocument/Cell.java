/**
 *
 */
package de.zedlitz.opendocument;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.time.LocalDate;


/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 */
public class Cell {
    protected final static String TYPE_UNDEFINED = "undefined";
    final static QName ELEMENT_CELL = new QName(Document.NS_TABLE, "table-cell");
    private final static QName ELEMENT_ANNOTATION = new QName(Document.NS_OFFICE, "annotation");
    private final static String ATTRIBUTE_VALUE_TYPE = "value-type";
    private final static String ATTRIBUTE_NUMBER_COLUMNS_REPEATED = "number-columns-repeated";
    private static final String ATTRIBUTE_DATE_VALUE = "date-value";
    private final StringBuffer content = new StringBuffer();
    private final int columnIndex;
    private final Row row;
    private String valueType;
    private int numberColumnsRepeated;
    private String dateValue;

    Cell(final XMLStreamReader parser, Row row, int columnIndex) {
        this.row = row;
        this.columnIndex = columnIndex;
        if (parser == null) {
            return;
        }

        this.valueType = StringUtils.defaultIfEmpty(parser.getAttributeValue(
                        Document.NS_OFFICE, Cell.ATTRIBUTE_VALUE_TYPE),
                Cell.TYPE_UNDEFINED);
        this.dateValue = parser.getAttributeValue(Document.NS_OFFICE, Cell.ATTRIBUTE_DATE_VALUE);
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
            throw new RuntimeException(e);
        }
    }

    private static String getColumnName(int columnIndex) {
        StringBuilder columnName = new StringBuilder();
        columnIndex++; // Adjust to 1-based index
        while (columnIndex > 0) {
            int remainder = (columnIndex - 1) % 26;
            columnName.insert(0, (char) ('A' + remainder));
            columnIndex = (columnIndex - 1) / 26;
        }
        return columnName.toString();
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

    public CellType getType() {
        if ("string".equals(valueType)) {
            return CellType.STRING;
        }
        if ("float".equals(valueType) || "currency".equals(valueType) || "date".equals(valueType)
                || "percentage".equals(valueType) || "time".equals(valueType)) {
            return CellType.NUMBER;
        }
        return CellType.EMPTY;
    }

    public LocalDate asDate() {
        if ("date".equals(valueType) && StringUtils.isNotEmpty(dateValue)) {
            return LocalDate.parse(dateValue);
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for date value");
    }

    public String getDataFormatString() {
        throw new NotImplementedException();
    }

    public Object getValue() {
        throw new NotImplementedException();
    }

    public String getText() {
        return getContent();
    }

    public String getRawValue() {
        return getContent();
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getAddress() {
        return getColumnName(columnIndex) + row.getRowNum();
    }
}
