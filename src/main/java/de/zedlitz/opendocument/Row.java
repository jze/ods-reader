package de.zedlitz.opendocument;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * A row in an OpenDocument table.
 *
 * @author jzedlitz
 */
public class Row implements Iterable<Cell> {
    /**
     * Element name "table-row"
     */
    static final QName ELEMENT_ROW = new QName(Document.NS_TABLE, "table-row");
    private final XMLStreamReader xpp;
    private final int rowNumber;
    int columnIndex = 0;
    private List<Cell> allCells = null;
    // position of the next cell in memory mode
    private int nextCachedCell = 0;
    private int repeatedCells;

    public Row(final XMLStreamReader xpp, int rowNumber) {
        this.xpp = xpp;
        this.rowNumber = rowNumber;
    }

    /**
     * Creates a copy of a row (that has been completely read) for another row number. Used for repeated rows.
     */
    Row copy(int rowNumber) {
        Row result = new Row(null, rowNumber);
        result.allCells = new ArrayList<>();
        for (Cell cell : allCells) {
            result.allCells.add(new Cell(cell, result, cell.getColumnIndex()));
        }
        return result;
    }

    /**
     * Read all remaining cells of the row into memory.
     */
    void readAllCells() {
        if (allCells == null) {
            List<Cell> cells = new ArrayList<>();
            Cell nextCell = this.nextCell();

            while (nextCell != null) {
                cells.add(nextCell);
                nextCell = this.nextCell();
            }
            allCells = cells;
        }
    }

    /**
     * Does the row (that has been completely read) contain only empty cells?
     */
    boolean isEmpty() {
        for (Cell cell : allCells) {
            if (!Cell.TYPE_UNDEFINED.equals(cell.getValueType()) || !cell.getContent().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isCellStartElement(int eventType) {
        return eventType == XMLStreamConstants.START_ELEMENT
                && (Cell.ELEMENT_CELL.equals(xpp.getName()) || Cell.ELEMENT_COVERED_CELL.equals(xpp.getName()));
    }

    private boolean isRowEndElement(int eventType) {
        return eventType == XMLStreamConstants.END_ELEMENT && Row.ELEMENT_ROW.equals(xpp.getName());
    }

    Cell cellToRepeat;

    public Cell nextCell() {
        Cell result = null;

        if (allCells != null) {
            // memory mode
            if (nextCachedCell < allCells.size()) {
                result = allCells.get(nextCachedCell++);
            }
        } else if (this.repeatedCells > 0) {
            // while there are repeated cells return copies of the repeated cell
            result = new Cell(cellToRepeat, this, columnIndex);
            this.repeatedCells--;
        } else {
            try {
                int eventType = xpp.getEventType();

                while (!isRowEndElement(eventType)) {
                    if (isCellStartElement(eventType)) {
                        final Cell myCell = new Cell(xpp, this, columnIndex);

                        if (myCell.getNumberColumnsRepeated() > 1) {
                            this.repeatedCells = myCell.getNumberColumnsRepeated() - 1;
                            this.cellToRepeat = myCell;
                        }

                        result = myCell;
                        xpp.next();

                        break;
                    }

                    eventType = xpp.next();
                }
            } catch (final XMLStreamException e) {
                e.printStackTrace();
            }
        }

        columnIndex++;

        return result;
    }

    public Cell getAt(final int i) {
        // switch to memory mode
        readAllCells();

        return allCells.get(i);
    }

    public void eachCell(final Consumer<Cell> c) {
        Cell nextCell = this.nextCell();

        while (nextCell != null) {
            c.accept(nextCell);
            nextCell = this.nextCell();
        }
    }

    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator(this);
    }

    public Stream<Cell> openStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator(), 0),
                false
        );
    }

    public int getRowNum() {
        return this.rowNumber;
    }
}
