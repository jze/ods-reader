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
    protected XMLStreamReader createParser(final String content)
            throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        return factory.createXMLStreamReader(new StringReader(content));
    }

    /**
     * Move the parser to the first start tag.
     */
    protected XMLStreamReader advanceToStartTag(
            final XMLStreamReader xpp) throws XMLStreamException {
        int eventType = xpp.getEventType();

        while ((eventType != XMLStreamConstants.START_ELEMENT) &&
                (eventType != XMLStreamConstants.END_DOCUMENT)) {
            eventType = xpp.next();
        }

        return xpp;
    }
}
