package de.zedlitz.opendocument;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * @author jzedlitz
 *
 */
public abstract class AbstractBaseTest extends TestCase {
    protected XMLStreamReaderWithDepth createParser(final String content)
        throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader xpp =
            factory.createXMLStreamReader(new StringReader(content));

        return new XMLStreamReaderWithDepth(xpp);
    }

    /**
     * Move the parser to the first start tag.
     *
     * @throws XmlPullParserException
     * @throws IOException
     *
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
