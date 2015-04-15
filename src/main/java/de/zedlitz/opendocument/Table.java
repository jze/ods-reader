package de.zedlitz.opendocument;

import groovy.lang.Closure;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;


/**
 * An OpenDocument table.
 *
 * @author jzedlitz
 *
 */
public class Table {
    static final QName ELEMENT_TABLE = new QName(Document.NS_TABLE, "table");
    private static final String ATTRIBUTE_NAME = "name";
    private final XMLStreamReaderWithDepth xpp;
    private String name;
    private final int depth;

    Table(final XMLStreamReaderWithDepth parser) {
        this.xpp = parser;
        this.depth = parser.getDepth();
        this.setName(parser.getAttributeValue(Document.NS_TABLE,
                Table.ATTRIBUTE_NAME));
    }

    /**
     * @see de.freenet.jzedlitz.oo.Table#nextRow()
     */
    public final Row nextRow() {
        Row result = null;

        try {
            /*
             * look for a table:table element until the end of the document has
             * been reached
             */
            int eventType = xpp.getEventType();

            while (!((eventType == XMLStreamConstants.END_ELEMENT) &&
                    Table.ELEMENT_TABLE.equals(xpp.getName()) &&
                    Document.NS_TABLE.equals(xpp.getNamespaceURI())) &&
                    (eventType != XMLStreamConstants.END_DOCUMENT) &&
                    (xpp.getDepth() >= depth)) {
                if ((eventType == XMLStreamConstants.START_ELEMENT) &&
                        Row.ELEMENT_ROW.equals(xpp.getName()) &&
                        Document.NS_TABLE.equals(xpp.getNamespaceURI())) {
                    // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by jzedlitz on 12.04.06 15:30
                    result = new Row(xpp);
                    xpp.next();

                    break;
                }

                eventType = xpp.next();
            }
        } catch (final XMLStreamException e) {
            // TODO Auto-generated catch block
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
     * @param name
     *            The name to set.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
      * @see de.zedlitz.opendocument.Table#eachRow(groovy.lang.Closure)
      */
    public void eachRow(final Closure c) {
        Row nextRow = this.nextRow();

        while (nextRow != null) {
            c.call(nextRow);

            nextRow = this.nextRow();
        }
    }
}
