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
    private int emptyCells;

    public Row(final XMLStreamReader xpp, int rowNumber) {
        this.xpp = xpp;
        this.rowNumber = rowNumber;
    }

    private boolean isCellStartElement(int eventType) {
        return eventType == XMLStreamConstants.START_ELEMENT && Cell.ELEMENT_CELL.equals(xpp.getName());
    }

    private boolean isRowEndElement(int eventType) {
        return eventType == XMLStreamConstants.END_ELEMENT && Row.ELEMENT_ROW.equals(xpp.getName());
    }

    public Cell nextCell() {
        Cell result = null;

        // while there are empty (faked) cells return one those
        if (this.emptyCells > 0) {
            result = new EmptyCell(this, columnIndex);
            this.emptyCells--;
        } else {
            try {
                int eventType = xpp.getEventType();

                while (!isRowEndElement(eventType)) {
                    if (isCellStartElement(eventType)) {
                        final Cell myCell = new Cell(xpp, this, columnIndex);

                        if (myCell.getNumberColumnsRepeated() > 1) {
                            this.emptyCells = myCell.getNumberColumnsRepeated() - 1;
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
        if (allCells == null) {
            allCells = new ArrayList<>();
            Cell nextCell = this.nextCell();

            while (nextCell != null) {
                allCells.add(nextCell);
                nextCell = this.nextCell();
            }
        }

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
