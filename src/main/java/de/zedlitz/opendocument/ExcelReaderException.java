package de.zedlitz.opendocument;

/**
 * Base class of {@link OdsReaderException} class to be a drop-in replacement for fastexcel.
 */
public class ExcelReaderException extends RuntimeException{
    public ExcelReaderException(String message) {
        super(message);
    }
}
