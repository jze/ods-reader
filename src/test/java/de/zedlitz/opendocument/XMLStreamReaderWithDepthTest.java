package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.mockito.Mockito.verify;

/**
 * Make sure that the method invocations are forwarded to the internal XmlStreamReader.
 */
class XMLStreamReaderWithDepthTest {

    XMLStreamReader originalReader = Mockito.mock(XMLStreamReader.class);

    XMLStreamReaderWithDepth reader = new XMLStreamReaderWithDepth(originalReader);

    @Test
    void close() throws XMLStreamException {
        reader.close();
        verify(originalReader).close();
    }

    @Test
    void getAttributeCount() {
        reader.getAttributeCount();
        verify(originalReader).getAttributeCount();
    }

    @Test
    void getAttributeLocalName() {
        reader.getAttributeLocalName(0);
        verify(originalReader).getAttributeLocalName(0);
    }

    @Test
    void getAttributeName() {
        reader.getAttributeName(0);
        verify(originalReader).getAttributeName(0);
    }

    @Test
    void getAttributeNamespace() {
        reader.getAttributeNamespace(0);
        verify(originalReader).getAttributeNamespace(0);
    }

    @Test
    void getAttributePrefix() {
        reader.getAttributePrefix(0);
        verify(originalReader).getAttributePrefix(0);
    }

    @Test
    void getAttributeType() {
        reader.getAttributeType(0);
        verify(originalReader).getAttributeType(0);
    }

    @Test
    void getCharacterEncodingScheme() {
        reader.getCharacterEncodingScheme();
        verify(originalReader).getCharacterEncodingScheme();
    }

    @Test
    void getElementText() throws XMLStreamException {
        reader.getElementText();
        verify(originalReader).getElementText();
    }

    @Test
    void getEncoding() {
        reader.getEncoding();
        verify(originalReader).getEncoding();
    }

    @Test
    void getEventType() {
        reader.getEventType();
        verify(originalReader).getEventType();
    }

    @Test
    void getLocalName() {
        reader.getLocalName();
        verify(originalReader).getLocalName();
    }

    @Test
    void getLocation() {
        reader.getLocation();
        verify(originalReader).getLocation();
    }

    @Test
    void getName() {
        reader.getName();
        verify(originalReader).getName();
    }

    @Test
    void getNamespaceContext() {
        reader.getNamespaceContext();
        verify(originalReader).getNamespaceContext();
    }

    @Test
    void getNamespaceCount() {
        reader.getNamespaceCount();
        verify(originalReader).getNamespaceCount();
    }

    @Test
    void getNamespacePrefix() {
        reader.getNamespacePrefix(34);
        verify(originalReader).getNamespacePrefix(34);
    }

    @Test
    void getNamespaceURI() {
        reader.getNamespaceURI();
        verify(originalReader).getNamespaceURI();
    }

    @Test
    void getPIData() {
        reader.getPIData();
        verify(originalReader).getPIData();
    }

    @Test
    void getPITarget() {
        reader.getPITarget();
        verify(originalReader).getPITarget();
    }

    @Test
    void getPrefix() {
        reader.getPrefix();
        verify(originalReader).getPrefix();
    }

    @Test
    void getProperty() {
        reader.getProperty("A");
        verify(originalReader).getProperty("A");
    }

    @Test
    void getText() {
        reader.getText();
        verify(originalReader).getText();
    }

    @Test
    void getTextCharacters() {
        reader.getTextCharacters();
        verify(originalReader).getTextCharacters();
    }

    @Test
    void getTextLength() {
        reader.getTextLength();
        verify(originalReader).getTextLength();
    }

    @Test
    void getTextStart() {
        reader.getTextStart();
        verify(originalReader).getTextStart();
    }

    @Test
    void getVersion() {
        reader.getVersion();
        verify(originalReader).getVersion();
    }

    @Test
    void hasName() {
        reader.hasName();
        verify(originalReader).hasName();
    }

    @Test
    void hasNext() throws XMLStreamException {
        reader.hasNext();
        verify(originalReader).hasNext();
    }

    @Test
    void hasText() {
        reader.hasText();
        verify(originalReader).hasText();
    }

    @Test
    void isAttributeSpecified() {
        reader.isAttributeSpecified(5);
        verify(originalReader).isAttributeSpecified(5);
    }

    @Test
    void isCharacters() {
        reader.isCharacters();
        verify(originalReader).isCharacters();
    }

    @Test
    void isEndElement() {
        reader.isEndElement();
        verify(originalReader).isEndElement();
    }

    @Test
    void isStandalone() {
        reader.isStandalone();
        verify(originalReader).isStandalone();
    }

    @Test
    void isStartElement() {
        reader.isStartElement();
        verify(originalReader).isStartElement();
    }

    @Test
    void isWhiteSpace() {
        reader.isWhiteSpace();
        verify(originalReader).isWhiteSpace();
    }


    @Test
    void require() throws XMLStreamException {
        reader.require(3, "http://example.org", "test");
        verify(originalReader).require(3, "http://example.org", "test");
    }

    @Test
    void standaloneSet() {
        reader.standaloneSet();
        verify(originalReader).standaloneSet();
    }

    @Test
    void getAttributeValue() {
        reader.getAttributeValue(3);
        verify(originalReader).getAttributeValue(3);
    }

    @Test
    void getNamespaceURIWithInt() {
        reader.getNamespaceURI(3);
        verify(originalReader).getNamespaceURI(3);
    }

    @Test
    void getNamespaceURIWithString() {
        reader.getNamespaceURI("foo");
        verify(originalReader).getNamespaceURI("foo");
    }

    @Test
    void getTextCharactersWithParameters() throws XMLStreamException {
        reader.getTextCharacters(5, "abc".toCharArray(), 0, 3);
        verify(originalReader).getTextCharacters(5, "abc".toCharArray(), 0, 3);
    }

}