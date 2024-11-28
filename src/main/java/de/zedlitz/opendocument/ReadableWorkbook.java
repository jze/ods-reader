package de.zedlitz.opendocument;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * Just a wrapper for the {@link Document} class to be a drop-in replacement for fastexcel.
 */
public class ReadableWorkbook extends Document {

    public ReadableWorkbook(File odsFile, ReadingOptions readingOptions) throws XMLStreamException, IOException {
        super(odsFile);
    }
}
