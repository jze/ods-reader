package de.zedlitz.opendocument;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;


/**
 * @author jzedlitz
 */
public abstract class AbstractBaseTest {
    protected XMLStreamReaderWithDepth createParser(final String content)
            throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader xpp =
                factory.createXMLStreamReader(new StringReader(content));

        return new XMLStreamReaderWithDepth(xpp);
    }

    /**
     * Move the parser to the first start tag.
     */
    protected XMLStreamReaderWithDepth advanceToStartTag(
            final XMLStreamReaderWithDepth xpp) throws XMLStreamException {
        int eventType = xpp.getEventType();

        while ((eventType != XMLStreamConstants.START_ELEMENT) &&
                (eventType != XMLStreamConstants.END_DOCUMENT)) {
            eventType = xpp.next();
        }

        return xpp;
    }
}
