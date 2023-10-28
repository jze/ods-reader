/**
 *
 */
package de.zedlitz.opendocument;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * Wrapper for {@link XMLStreamReader} that counts the depth of elements.
 *
 * @author jesper
 */
public class XMLStreamReaderWithDepth implements XMLStreamReader {
    private final XMLStreamReader internalReader;
    private int depth = 0;

    public XMLStreamReaderWithDepth(final XMLStreamReader reader) {
        this.internalReader = reader;
    }

    public void close() throws XMLStreamException {
        internalReader.close();
    }

    public int getAttributeCount() {
        return internalReader.getAttributeCount();
    }

    public String getAttributeLocalName(final int index) {
        return internalReader.getAttributeLocalName(index);
    }

    public QName getAttributeName(final int index) {
        return internalReader.getAttributeName(index);
    }

    public String getAttributeNamespace(final int index) {
        return internalReader.getAttributeNamespace(index);
    }

    public String getAttributePrefix(final int index) {
        return internalReader.getAttributePrefix(index);
    }

    public String getAttributeType(final int index) {
        return internalReader.getAttributeType(index);
    }

    public String getAttributeValue(final int index) {
        return internalReader.getAttributeValue(index);
    }

    public String getAttributeValue(final String namespaceURI, final String localName) {
        return internalReader.getAttributeValue(namespaceURI, localName);
    }

    public String getCharacterEncodingScheme() {
        return internalReader.getCharacterEncodingScheme();
    }

    public String getElementText() throws XMLStreamException {
        return internalReader.getElementText();
    }

    public String getEncoding() {
        return internalReader.getEncoding();
    }

    public int getEventType() {
        return internalReader.getEventType();
    }

    public String getLocalName() {
        return internalReader.getLocalName();
    }

    public Location getLocation() {
        return internalReader.getLocation();
    }

    public QName getName() {
        return internalReader.getName();
    }

    public NamespaceContext getNamespaceContext() {
        return internalReader.getNamespaceContext();
    }

    public int getNamespaceCount() {
        return internalReader.getNamespaceCount();
    }

    public String getNamespacePrefix(final int index) {
        return internalReader.getNamespacePrefix(index);
    }

    public String getNamespaceURI() {
        return internalReader.getNamespaceURI();
    }

    public String getNamespaceURI(final int index) {
        return internalReader.getNamespaceURI(index);
    }

    public String getNamespaceURI(final String prefix) {
        return internalReader.getNamespaceURI(prefix);
    }

    public String getPIData() {
        return internalReader.getPIData();
    }

    public String getPITarget() {
        return internalReader.getPITarget();
    }

    public String getPrefix() {
        return internalReader.getPrefix();
    }

    public Object getProperty(final String name) throws IllegalArgumentException {
        return internalReader.getProperty(name);
    }

    public String getText() {
        return internalReader.getText();
    }

    public char[] getTextCharacters() {
        return internalReader.getTextCharacters();
    }

    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return internalReader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public int getTextLength() {
        return internalReader.getTextLength();
    }

    public int getTextStart() {
        return internalReader.getTextStart();
    }

    public String getVersion() {
        return internalReader.getVersion();
    }

    public boolean hasName() {
        return internalReader.hasName();
    }

    public boolean hasNext() throws XMLStreamException {
        return internalReader.hasNext();
    }

    public boolean hasText() {
        return internalReader.hasText();
    }

    public boolean isAttributeSpecified(final int index) {
        return internalReader.isAttributeSpecified(index);
    }

    public boolean isCharacters() {
        return internalReader.isCharacters();
    }

    public boolean isEndElement() {
        return internalReader.isEndElement();
    }

    public boolean isStandalone() {
        return internalReader.isStandalone();
    }

    public boolean isStartElement() {
        return internalReader.isStartElement();
    }

    public boolean isWhiteSpace() {
        return internalReader.isWhiteSpace();
    }

    public int next() throws XMLStreamException {
        int eventType = internalReader.next();

        if (eventType == XMLStreamConstants.START_ELEMENT) {
            this.depth++;
        } else if (eventType == XMLStreamConstants.END_ELEMENT) {
            this.depth--;
        } else if (eventType == XMLStreamConstants.START_DOCUMENT) {
            this.depth++;
        } else if (eventType == XMLStreamConstants.END_DOCUMENT) {
            this.depth--;
        }

        return eventType;
    }

    public int nextTag() throws XMLStreamException {
        int eventType = internalReader.next();

        if (eventType == XMLStreamConstants.START_ELEMENT) {
            this.depth++;
        } else if (eventType == XMLStreamConstants.END_ELEMENT) {
            this.depth--;
        } else if (eventType == XMLStreamConstants.START_DOCUMENT) {
            this.depth++;
        } else if (eventType == XMLStreamConstants.END_DOCUMENT) {
            this.depth--;
        }

        return eventType;
    }

    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        internalReader.require(type, namespaceURI, localName);
    }

    public boolean standaloneSet() {
        return internalReader.standaloneSet();
    }

    public int getDepth() {
        return this.depth;
    }
}
