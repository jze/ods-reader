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

    /**
     * Get the raw value of the <code>currency</code> attribute. It is only present for cells with the type "currency".
     * @return  the raw currency value or <code>null</code> if not present.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Get the raw value of the <code>time-value</code> attribute. It is only present for cells with the type "time".
     * @return  the raw time value or <code>null</code> if not present.
     */
    public String getTimeValue() {
        return timeValue;
    }

    /**
     * Get the raw value of the <code>boolean-value</code> attribute. It is only present for cells with the type "boolean".
     * @return  the raw boolean value or <code>null</code> if not present.
     */
    public String getBooleanValue() {
        return booleanValue;
    }

    /**
     * Get the raw value of the <code>date-value</code> attribute. It is only present for cells with the type "date".
     * @return  the raw date value or <code>null</code> if not present.
     */
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
     * Get the raw value of the <code>value-type</code> attribute. It should be present for every cell.
     * @return Returns the valueType or <code>null</code> if not present.
     */
    public String getValueType() {
        return this.valueType;
    }

    /**
     * Returns the content of the cell formatted for the locale of the file. For example in a German ods file the
     * boolean value <code>true</code> will be returned as <code>WAHR</code>. In a French ods file it will be
     * <code>VRAI</code>. And in an English ods file it will be <code>TRUE</code>.
     * <p>
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

    /**
     * Return the boolean value of the cell. This works only for cells with the type "boolean".
     * Check for <code>"boolean".equals(cell.getValueType())</code> before invoking this method.
     *
     * @return a {@link LocalDate} object with the value of the cell.
     * @throws OdsReaderException is the cell is not a boolean cell
     */
    public boolean asBoolean() {
        if ("boolean".equals(valueType) && StringUtils.isNotEmpty(booleanValue)) {
            return Boolean.valueOf(booleanValue);
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for boolean value");
    }

    /**
     * Return a {@link LocalDate} object with the value of the cell. This works only for cells with the type "date" and
     * do not have a time component.
     * Check for <code>"date".equals(cell.getValueType())</code> before invoking this method.
     *
     * @return a {@link LocalDate} object with the value of the cell.
     * @throws OdsReaderException is the cell is not a date cell
     */
    public LocalDate asDate() {
        if ("date".equals(valueType) && StringUtils.isNotEmpty(dateValue)) {
            return LocalDate.parse(dateValue);
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for date value");
    }

    /**
     * Return a {@link LocalDateTime} object with the value of the cell. This works only for cells with the type "date".
     * Check for <code>"date".equals(cell.getValueType())</code> before invoking this method.
     * If the cell contains only a date and no time the time <code>00:00:00</code> will be used as the time component.
     *
     * @return a {@link LocalDateTime} object with the value of the cell.
     * @throws OdsReaderException is the cell is not a date cell
     */
    public LocalDateTime asDateTime() {
        if ("date".equals(valueType) && StringUtils.isNotEmpty(dateValue)) {
            if (dateValue.contains("T")) {
                // date and time
                return LocalDateTime.parse(dateValue, DateTimeFormatter.ISO_DATE_TIME);
            } else {
                // only date
                return LocalDateTime.parse(dateValue + "T00:00:00", DateTimeFormatter.ISO_DATE_TIME);
            }
        }

        throw new OdsReaderException("Wrong cell type " + valueType + " for date value");
    }

    /**
     * Does the cell contain a value that consists of a date and a time?
     */
    public boolean isDateTime() {
        return "date".equals(valueType) && StringUtils.contains(dateValue, "T");
    }

    /**
     * Return a {@link LocalTime} object with the value of the cell. This works only for cells with the type "time".
     * Check for <code>"time".equals(cell.getValueType())</code> before invoking this method.
     *
     * @return a {@link LocalTime} object with the value of the cell.
     * @throws OdsReaderException is the cell is not a time cell
     */
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
     * Returns the language independent value of a cell. This is only present for cell with the type float, currency,
     * and percentage. It is stored in the <code>value</code> attribute of the cell element.
     * <p>
     * If getValue() is <code>null</code> you can use getContent() to get the text value of the cell.
     *
     * @return the language independent value of a cell or <code>null</code> if not value is present.
     */
    public String getValue() {
        return value;
    }


}
