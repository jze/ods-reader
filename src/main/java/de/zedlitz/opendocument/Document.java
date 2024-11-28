package de.zedlitz.opendocument;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * An OpenDocument spreadsheet document.
 *
 * @author jzedlitz
 */
public class Document {
    /**
     * Namespace urn:oasis:names:tc:opendocument:xmlns:table:1.0
     */
    static String NS_TABLE = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";

    /**
     * Namespace urn:oasis:names:tc:opendocument:xmlns:office:1.0
     */
    static String NS_OFFICE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

    private final XMLStreamReader xpp;

    public Document(final String filename)
            throws XMLStreamException, IOException {
        this(new ZipFile(filename));
    }

    public Document(final File file) throws XMLStreamException, IOException {
        this(new ZipFile(file));
    }

    public Document(final ZipFile file) throws XMLStreamException, IOException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final ZipEntry content = file.getEntry("content.xml");
        this.xpp = factory.createXMLStreamReader(file.getInputStream(content));
    }

    public Document(final InputStream inputStream)
            throws XMLStreamException, IOException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();

        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ZipEntry content = null;
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while ((zipEntry != null) && (content == null)) {
            if ("content.xml".equals(zipEntry.getName())) {
                content = zipEntry;
            } else {
                zipEntry = zipInputStream.getNextEntry();
            }
        }

        this.xpp = factory.createXMLStreamReader(zipInputStream);
    }

    public Document(final XMLStreamReader parser) {
        this.xpp = parser;
    }

    public final Table nextTable() {
        Table result = null;

        try {
            /*
             * look for a table:table element until the end of the document has
             * been reached
             */
            int eventType = xpp.getEventType();

            while ((eventType != XMLStreamConstants.END_DOCUMENT)) {
                if ((eventType == XMLStreamConstants.START_ELEMENT) &&
                        Table.ELEMENT_TABLE.equals(xpp.getName())) {
                    result = new Table(xpp);
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

    public void eachTable(final Consumer<Table> c) {
        Table nextTable = this.nextTable();

        while (nextTable != null) {
            c.accept(nextTable);
            nextTable = this.nextTable();
        }
    }

    public Optional<Sheet> getSheet(int i) {
        int count = 0;
        Table nextTable = this.nextTable();
        while (nextTable != null) {
            if (count == i) {
                return Optional.of(nextTable);
            }
            count++;
            nextTable = this.nextTable();
        }
        return Optional.empty();
    }
}
