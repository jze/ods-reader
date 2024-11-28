package de.zedlitz.opendocument;

import java.util.Iterator;

public class RowIterator implements Iterator<Row> {
    private final Table table;
    private Row next;

    RowIterator(Table table) {
        this.table = table;
        this.next = table.nextRow();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Row next() {
        if (next == null) {
            throw new IllegalStateException("No more rows available.");
        }
        Row current = next;
        next = table.nextRow();
        return current;
    }
}
