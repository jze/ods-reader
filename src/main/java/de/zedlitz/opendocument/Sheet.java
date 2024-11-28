package de.zedlitz.opendocument;

import java.util.stream.Stream;

/**
 * The {@link Table} class implements this interface to be a drop-in replacement for fastexcel.
 */
public interface Sheet {

    String getName();

    Stream<Row> openStream();
}
