package de.zedlitz.opendocument;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * An OpenDocument table.
 *
 * @author jzedlitz
 */
public class Table implements Sheet, Iterable<Row> {
    static final QName ELEMENT_TABLE = new QName(Document.NS_TABLE, "table");

    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_NUMBER_ROWS_REPEATED = "number-rows-repeated";
    private final XMLStreamReader xpp;
    private String name;
    private int rowNumber = 1;
    // copies of a repeated row that still have to be returned
    private final Deque<Row> repeatedRows = new ArrayDeque<>();

    Table(final XMLStreamReader parser) {
        this.xpp = parser;
        this.setName(parser.getAttributeValue(Document.NS_TABLE, Table.ATTRIBUTE_NAME));
    }

    private boolean isTableEndElement(int eventType) {

        return eventType == XMLStreamConstants.END_ELEMENT && Table.ELEMENT_TABLE.equals(xpp.getName());
    }

    private boolean isRowStartElement(int eventType) {
        return eventType == XMLStreamConstants.START_ELEMENT
                && Row.ELEMENT_ROW.equals(xpp.getName());
    }

    public final Row nextRow() {
        if (!repeatedRows.isEmpty()) {
            return repeatedRows.poll();
        }

        Row result = null;

        try {
            /*
             * look for a table:table-row element until the end of the table has
             * been reached
             */
            int eventType = xpp.getEventType();

            while (!isTableEndElement(eventType)) {

                if (isRowStartElement(eventType)) {
                    final int numberRowsRepeated = getNumberRowsRepeated();
                    // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by jzedlitz on 12.04.06 15:30
                    result = new Row(xpp, rowNumber++);
                    xpp.next();

                    if (numberRowsRepeated > 1) {
                        expandRepeatedRow(result, numberRowsRepeated);
                    }

                    break;
                }

                eventType = xpp.next();
            }
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }

        return result;
    }

    private int getNumberRowsRepeated() {
        try {
            return Integer.parseInt(xpp.getAttributeValue(Document.NS_TABLE, ATTRIBUTE_NUMBER_ROWS_REPEATED));
        } catch (final RuntimeException e) {
            return 0;
        }
    }

    /**
     * Read the repeated row into memory and queue copies of it. Empty rows at the end of a table
     * (LibreOffice writes them for formatted rows, often with ~1 million repetitions) are not expanded.
     */
    private void expandRepeatedRow(final Row row, final int numberRowsRepeated) throws XMLStreamException {
        row.readAllCells();

        if (row.isEmpty()) {
            // look ahead whether another row follows - leave the parser at the row start or table end
            int eventType = xpp.getEventType();
            while (!isTableEndElement(eventType) && !isRowStartElement(eventType)) {
                eventType = xpp.next();
            }
            if (isTableEndElement(eventType)) {
                return;
            }
        }

        for (int i = 1; i < numberRowsRepeated; i++) {
            repeatedRows.add(row.copy(rowNumber++));
        }
    }


    /**
     * @see de.zedlitz.opendocument.Table#getName()
     */
    public final String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    public void eachRow(final Consumer<Row> c) {
        Row nextRow = this.nextRow();

        while (nextRow != null) {
            c.accept(nextRow);

            nextRow = this.nextRow();
        }
    }

    public Stream<Row> openStream() {
        Iterator<Row> iterator = new RowIterator(this);

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, 0),
                false
        );
    }

    @Override
    public Iterator<Row> iterator() {
        return new RowIterator(this);
    }


}
