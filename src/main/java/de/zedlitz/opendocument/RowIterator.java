package de.zedlitz.opendocument;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RowIterator implements Iterator<Row> {
    private final Table table;
    private Row next;
    // The next row must not be fetched before the cells of the current row have been read,
    // because fetching it moves the parser past the current row.
    private boolean fetched = false;

    RowIterator(Table table) {
        this.table = table;
    }

    @Override
    public boolean hasNext() {
        if (!fetched) {
            next = table.nextRow();
            fetched = true;
        }
        return next != null;
    }

    @Override
    public Row next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more rows available.");
        }
        fetched = false;
        return next;
    }
}
