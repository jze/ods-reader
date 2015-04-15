# opendocument-reader
You want to import very large OpenDocument tables and worry about memory usage? In that case the opendocument-reader might be interesting for you. Instead of loading the entire document, it is a pull-based reader. 

## Usage
Here is an example how to use the opendocument-reader:
```java
  Document doc = new Document(new ZipFile("myTable.ods"));
  Table table = doc.nextTable();
  Row row = table.nextRow();
  while( row != null ) {
    Cell a = row.nextCell();
    Cell b = row.nextCell();
    System.out.println( a.getContent + "\t" + b.getContent );
    row = table.nextRow();
  }
  
```
