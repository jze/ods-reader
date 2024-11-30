# ods-reader

![Maven Central version](https://img.shields.io/maven-central/v/de.zedlitz/ods-reader)
![Coverage](.github/badges/jacoco.svg)

You want to import very large OpenDocument tables (ODS file) and worry about memory usage? In that case the ods-reader might be interesting for you. Instead of loading the entire document, it is a pull-based reader. It therefore has a very low memory consumption, even with huge documents.

## Usage
Here is an example how to use the ods-reader:
```java
  Document doc = new Document(new File("myTable.ods"));
  Table table = doc.nextTable();
  Row row = table.nextRow();
  while( row != null ) {
    Cell a = row.nextCell();
    Cell b = row.nextCell();
    System.out.println( a.getContent + "\t" + b.getContent );
    row = table.nextRow();
  }
```

### Maven 

The library is available at Maven Central. You can add a dependency to `ods-reader` to you project like this:

```xml
<dependency>
    <groupId>de.zedlitz</groupId>
    <artifactId>ods-reader</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Cell types

ODS distinguishes between the value that is displayed for humans and the machine-readable value. The value that is displayed for humans depends on the language selected for the document. The method `getContent()` is used to get the language depending human-readable value.

The machine-reable value can be read using specialized methods that return suitable Java objects. Here is an example:

```java
if ("date".equals(cell.getValueType())) {
    if( cell.isDateTime() ) {
        LocalDateTime dateTime = cell.asDateTime();
    } else {
        LocalDate date = cell.asDate();
    }
} else if ("boolean".equals(cell.getValueType())) {
    boolean b = cell.asBoolean();
} else if ("time".equals(cell.getValueType())) {
    LocalTime time = cell.asTime();
} else if (cell.getValue() != null) {
    result = cell.getValue();
} else {
    result = cell.getContent();
}
```
