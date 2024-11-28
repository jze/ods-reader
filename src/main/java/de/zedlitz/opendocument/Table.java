package de.zedlitz.opendocument;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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
    private final XMLStreamReader xpp;
    private String name;

    Table(final XMLStreamReader parser) {
        this.xpp = parser;
        this.setName(parser.getAttributeValue(Document.NS_TABLE, Table.ATTRIBUTE_NAME));
    }

    private boolean isTableEndElement(int eventType) {

        return eventType == XMLStreamConstants.END_ELEMENT && Table.ELEMENT_TABLE.equals(xpp.getName());
    }

    private boolean isRowStartElement(int eventType) {
        return eventType == XMLStreamConstants.START_ELEMENT
                && Row.ELEMENT_ROW.equals(xpp.getName())
                && Document.NS_TABLE.equals(xpp.getNamespaceURI());
    }


    public final Row nextRow() {
        Row result = null;

        try {
            /*
             * look for a table:table element until the end of the document has
             * been reached
             */
            int eventType = xpp.getEventType();

            while (!isTableEndElement(eventType)) {

                if (isRowStartElement(eventType)) {
                    // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by jzedlitz on 12.04.06 15:30
                    result = new Row(xpp);
                    xpp.next();

                    break;
                }

                eventType = xpp.next();
            }
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }

        return result;
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
