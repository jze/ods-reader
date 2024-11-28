package de.zedlitz.opendocument;

public class ReadingOptions {
    public static final ReadingOptions DEFAULT_READING_OPTIONS = new ReadingOptions(false, false);
    private final boolean withCellFormat;
    private final boolean cellInErrorIfParseError;

    public ReadingOptions(boolean withCellFormat, boolean cellInErrorIfParseError) {
        this.withCellFormat = withCellFormat;
        this.cellInErrorIfParseError = cellInErrorIfParseError;
    }

    public boolean isWithCellFormat() {
        return this.withCellFormat;
    }

    public boolean isCellInErrorIfParseError() {
        return this.cellInErrorIfParseError;
    }
}
