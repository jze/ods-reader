package de.zedlitz.opendocument;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * An OpenDocument spreadsheet document.
 * <p>
 * Close the document after use, e.g. with try-with-resources. A {@link ZipFile} or {@link InputStream} passed to
 * the constructor is not closed by {@link #close()} - this remains the responsibility of the caller.
 *
 * @author jzedlitz
 */
public class Document implements Closeable {
    /**
     * Namespace urn:oasis:names:tc:opendocument:xmlns:table:1.0
     */
    final static String NS_TABLE = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";

    /**
     * Namespace urn:oasis:names:tc:opendocument:xmlns:office:1.0
     */
    final static String NS_OFFICE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

    final static String NS_TEXT = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";

    private final XMLStreamReader xpp;

    // the ZipFile opened by this document, null if the caller is responsible for the source
    final ZipFile ownedZipFile;

    public Document(final String filename)
            throws XMLStreamException, IOException {
        this(new ZipFile(filename), true);
    }

    public Document(final File file) throws XMLStreamException, IOException {
        this(new ZipFile(file), true);
    }

    public Document(final Path path) throws XMLStreamException, IOException {
        this(new ZipFile(path.toFile()), true);
    }

    public Document(final ZipFile file) throws XMLStreamException, IOException {
        this(file, false);
    }

    private Document(final ZipFile file, final boolean ownsZipFile) throws XMLStreamException, IOException {
        try {
            final XMLInputFactory factory = createInputFactory();
            final ZipEntry content = file.getEntry("content.xml");
            this.xpp = factory.createXMLStreamReader(file.getInputStream(content));
        } catch (XMLStreamException | IOException | RuntimeException e) {
            if (ownsZipFile) {
                try {
                    file.close();
                } catch (IOException closeException) {
                    e.addSuppressed(closeException);
                }
            }
            throw e;
        }
        this.ownedZipFile = ownsZipFile ? file : null;
    }

    public Document(final InputStream inputStream)
            throws XMLStreamException, IOException {
        final XMLInputFactory factory = createInputFactory();

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
        this.ownedZipFile = null;
    }

    public Document(final XMLStreamReader parser) {
        this.xpp = parser;
        this.ownedZipFile = null;
    }

    /**
     * Closes the parser and the file if it has been opened by this document.
     */
    @Override
    public void close() throws IOException {
        try {
            xpp.close();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        } finally {
            if (ownedZipFile != null) {
                ownedZipFile.close();
            }
        }
    }

    /**
     * Creates a factory that does not process DTDs and external entities to prevent XXE attacks.
     */
    private static XMLInputFactory createInputFactory() {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory;
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

    public Optional<Table> getTable(int i) {
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

    public Optional<Sheet> getSheet(int i) {
        return getTable(i).map(it -> it);
    }
}
