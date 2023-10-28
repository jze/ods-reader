package de.zedlitz.opendocument;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * A row in an OpenDocument table.
 *
 * @author jzedlitz
 */
public class Row {
    /**
     * Element name "table-row"
     */
    static final QName ELEMENT_ROW = new QName(Document.NS_TABLE, "table-row");
    private final XMLStreamReaderWithDepth xpp;
    private final int depth;
    List<Cell> allCells = null;
    private int emptyCells;

    public Row(final XMLStreamReaderWithDepth xpp) {
        this.xpp = xpp;
        this.depth = xpp.getDepth();
    }

    public Cell nextCell() {
        Cell result = null;

        // while there are empty (faked) cells return one those
        if (this.emptyCells > 0) {
            result = new EmptyCell();
            this.emptyCells--;
        } else {
            try {
                int eventType = xpp.getEventType();

                while (!((eventType == XMLStreamConstants.END_ELEMENT) && Row.ELEMENT_ROW.equals(xpp.getName())) && (eventType != XMLStreamConstants.END_DOCUMENT) && (xpp.getDepth() >= depth)) {
                    if ((eventType == XMLStreamConstants.START_ELEMENT) && Cell.ELEMENT_CELL.equals(xpp.getName())) {
                        final Cell myCell = new Cell(xpp);

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    Cell getAt(final int i) {
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
}
