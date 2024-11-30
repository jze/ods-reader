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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 */
public class Cell {
    protected final static String TYPE_UNDEFINED = "undefined";
    final static QName ELEMENT_CELL = new QName(Document.NS_TABLE, "table-cell");
    private final static QName ELEMENT_ANNOTATION = new QName(Document.NS_OFFICE, "annotation");
    private final static String ATTRIBUTE_VALUE_TYPE = "value-type";
    private final static String ATTRIBUTE_NUMBER_COLUMNS_REPEATED = "number-columns-repeated";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_BOOLEAN_VALUE = "boolean-value";
    private static final String ATTRIBUTE_DATE_VALUE = "date-value";
    private static final String ATTRIBUTE_TIME_VALUE = "time-value";
    private static final String ATTRIBUTE_CURRENCY = "currency";

    private final StringBuffer content = new StringBuffer();
    private final int columnIndex;
    private final Row row;
    private String currency;
    private String timeValue;
    private String booleanValue;
    private String value;
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
        this.value = parser.getAttributeValue(Document.NS_OFFICE, Cell.ATTRIBUTE_VALUE);
        this.booleanValue = parser.getAttributeValue(Document.NS_OFFICE, Cell.ATTRIBUTE_BOOLEAN_VALUE);
        this.timeValue = parser.getAttributeValue(Document.NS_OFFICE, Cell.ATTRIBUTE_TIME_VALUE);
        this.currency = parser.getAttributeValue(Document.NS_OFFICE, Cell.ATTRIBUTE_CURRENCY);

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

    public String getCurrency() {
        return currency;
    }

    public String getTimeValue() {
        return timeValue;
    }

    public String getBooleanValue() {
        return booleanValue;
    }

    public String getDateValue() {
        return dateValue;
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
     * Returns the content of the cell formatted for the locale of the file. For example in a German ods file the
     * boolean value <code>true</code> will be returned as <code>WAHR</code>. In a French ods file it will be
     * <code>VRAI</code>. And in an English ods file it will be <code>TRUE</code>.
     * <p/>
     * If you are looking for a language independent value you can use the getValue method.
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
        return String.format("[%s \"%s\"]", getValueType(), getContent());
    }

    public Boolean asBoolean() {
        if ("boolean".equals(valueType) && StringUtils.isNotEmpty(booleanValue)) {
            return Boolean.valueOf(booleanValue);
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for boolean value");
    }

    public LocalDate asDate() {
        if ("date".equals(valueType) && StringUtils.isNotEmpty(dateValue)) {
            return LocalDate.parse(dateValue);
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for date value");
    }

    public LocalDateTime asDateTime() {
        if ("date".equals(valueType) && StringUtils.isNotEmpty(dateValue)) {
            if (dateValue.contains("T")) {
                // date and time
                return LocalDateTime.parse(dateValue, DateTimeFormatter.ISO_DATE_TIME);
            } else {
                // only date
                return LocalDateTime.parse(dateValue+"T00:00:00", DateTimeFormatter.ISO_DATE_TIME);
            }
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for date value");
    }

    public LocalTime asTime() {
        if ("time".equals(valueType) && StringUtils.isNotEmpty(timeValue)) {
            Duration duration = Duration.parse(timeValue);
            return LocalTime.ofSecondOfDay(duration.getSeconds());
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for time value");
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getAddress() {
        return getColumnName(columnIndex) + row.getRowNum();
    }

    /**
     * Returns the language independent value of a cell.
     */
    public String getValue() {
        return value;
    }


}
