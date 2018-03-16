package Data;

import IO.CSV.CSVDataInput;
import IO.CSV.CSVSyntax;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *用来存放输入数据
 * @Author CLD
 * @Date 2018/3/15 10:08
 **/
public abstract class Data {

    protected abstract Iterator<String[]> iterator();

//    /** The data handle. */
//    private DataHandleInput handle;
//
//    /** The data definition. */
//    private DataDefinition  definition = new DataDefinition();

    /**
     * 用于直接内部生成数据
     */
    public static class DefaultData extends Data{

      private final List<String[]> data=new ArrayList<String[]>();

      public void add(String... row){
         data.add(row);
      }

      @Override
      protected Iterator<String[]> iterator(){return data.iterator();}
  }

    /**
     * array 格式数据
     */
    public static class ArrayData extends Data{

      private final String[][] array;

      private ArrayData(String[][] array) {
          this.array = array;
      }

      @Override
      protected Iterator<String[]> iterator() {
          return new Iterator<String[]>() {

              private int pos = 0;

              @Override
              public boolean hasNext() {
                  return pos<array.length;
              }

              @Override
              public String[] next() {
                  if (hasNext()) {
                      return array[pos++];
                  } else {
                      throw new NoSuchElementException();
                  }
              }

              @Override
              public void remove() {
                  throw new UnsupportedOperationException();
              }
          };
      }
  }

    /**
     * Iterator 格式数据
     */
    public static class IterableData extends  Data{

      private Iterator<String[]> iterator=null;

      private IterableData(final Iterator<String[]> iterator){this.iterator=iterator;}

      @Override
      protected Iterator<String[]> iterator(){
          return iterator;
      }
  }

    public static DefaultData create() {
        return new DefaultData();
    }

    /** 通过CSV，生成数据 */

    /**
     * Data from CSV
     * @param file the file
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset) throws IOException {
        return new IterableData(new CSVDataInput(file, charset).iterator());
    }

    /**
     * Data from CSV
     * @param file A file
     * @param delimiter The utilized separator character
     * @return A Data object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset, final char delimiter) throws IOException {
        return new IterableData(new CSVDataInput(file, charset, delimiter).iterator());
    }

    /**
     * Data from CSV
     * @param file A file
     * @param delimiter The utilized separator character
     * @param quote The delimiter for strings
     * @return A Data object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset, final char delimiter, final char quote) throws IOException {
        return new IterableData(new CSVDataInput(file, charset, delimiter, quote).iterator());
    }

    /**
     * Data from CSV
     * @param file the file
     * @param delimiter the delimiter
     * @param quote the quote
     * @param escape the escape
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset, final char delimiter, final char quote, final char escape) throws IOException {
        return new IterableData(new CSVDataInput(file, charset, delimiter, quote, escape).iterator());
    }

    /**
     * Data from CSV
     * @param file the file
     * @param delimiter the delimiter
     * @param quote the quote
     * @param escape the escape
     * @param linebreak the linebreak
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        return new IterableData(new CSVDataInput(file, charset, delimiter, quote, escape, linebreak).iterator());
    }

    /**
     * Data from CSV
     * @param file the file
     * @param config the config
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset, final CSVSyntax config) throws IOException {
        return new IterableData(new CSVDataInput(file, charset, config).iterator());
    }

    /**
     * Data from CSV
     * @param file the file
     * @param config the config
     * @param datatypes the datatypes
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final File file, final Charset charset, final CSVSyntax config, final DataType<T>[] datatypes) throws IOException {
        return new IterableData(new CSVDataInput(file, charset, config, datatypes).iterator());
    }

    /**
     * Data from CSV
     * @param stream the stream
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset).iterator());
    }

    /**
     * Data from CSV
     * @param stream An input stream
     * @param delimiter The utilized separator character
     * @return A Data object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset, final char delimiter) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset, delimiter).iterator());
    }

    /**
     * Data from CSV
     * @param stream An input stream
     * @param delimiter The utilized separator character
     * @param quote The delimiter for strings
     * @return A Data object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset, final char delimiter, final char quote) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset, delimiter, quote).iterator());
    }

    /**
     * Data from CSV
     * @param stream the stream
     * @param delimiter the delimiter
     * @param quote the quote
     * @param escape the escape
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset, final char delimiter, final char quote, final char escape) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset, delimiter, quote, escape).iterator());
    }

    /**
     * Data from CSV
     * @param stream the stream
     * @param delimiter the delimiter
     * @param quote the quote
     * @param escape the escape
     * @param linebreak the linebreak
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset, delimiter, quote, escape, linebreak).iterator());
    }

    /**
     * Data from CSV
     * @param stream the stream
     * @param config the config
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset, final CSVSyntax config) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset, config).iterator());
    }

    /**
     *Data from CSV
     * @param stream the stream
     * @param config the config
     * @param datatypes the datatypes
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final InputStream stream, final Charset charset, final CSVSyntax config, final DataType<T>[] datatypes) throws IOException {
        return new IterableData(new CSVDataInput(stream, charset, config, datatypes).iterator());
    }

    /**
     * Data from CSV
     * @param path the path
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset) throws IOException {
        return new IterableData(new CSVDataInput(path, charset).iterator());
    }

    /**
     * Data from CSV
     * @param path A path to the file
     * @param delimiter The utilized separator character
     * @return A Data object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset, final char delimiter) throws IOException {
        return new IterableData(new CSVDataInput(path, charset, delimiter).iterator());
    }

    /**
     * Data from CSV
     * @param path A path to the file
     * @param delimiter The utilized separator character
     * @param quote The delimiter for strings
     * @return A Data object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset, final char delimiter, final char quote) throws IOException {
        return new IterableData(new CSVDataInput(path, charset, delimiter, quote).iterator());
    }

    /**
     * Data from CSV
     * @param path the path
     * @param delimiter the delimiter
     * @param quote the quote
     * @param escape the escape
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset, final char delimiter, final char quote, final char escape) throws IOException {
        return new IterableData(new CSVDataInput(path, charset, delimiter, quote, escape).iterator());
    }

    /**
     * Data from CSV
     * @param path the path
     * @param delimiter the delimiter
     * @param quote the quote
     * @param escape the escape
     * @param linebreak the linebreak
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        return new IterableData(new CSVDataInput(path, charset, delimiter, quote, escape, linebreak).iterator());
    }

    /**
     * Data from CSV
     * @param path the path
     * @param config the config
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset, final CSVSyntax config) throws IOException {
        return new IterableData(new CSVDataInput(path, charset, config).iterator());
    }

    /**
     * Data from CSV
     * @param path the path
     * @param config the config
     * @param datatypes the datatypes
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Data create(final String path, final Charset charset, final CSVSyntax config, final DataType<T>[] datatypes) throws IOException {
        return new IterableData(new CSVDataInput(path, charset, config, datatypes).iterator());
    }

    /**
     * Data from List
     * @param list
     * @return
     */
    public static Data create(final List<String[]> list) {
        return new IterableData(list.iterator());
    }

    /**
     *Data from Array
     * @param array The array
     * @return A Data object
     */
    public static Data create(final String[][] array) {
        return new ArrayData(array);
    }

//    /**
//     * Creates a new data object from the given data source specification.
//     *
//     * @param source The source that should be used to import data
//     * @return Data object as described by the data source
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    public static Data create(final DataSource source) throws IOException {
//
//        ImportConfiguration config = source.getConfiguration();
//        ImportAdapter adapter = ImportAdapter.create(config);
//        return create(adapter);
//    }

//    /**
//     * Creates a new data object from an iterator over tuples.
//     *
//     * @param iterator An iterator
//     * @return A Data object
//     */
//    public static Data create(final Iterator<String[]> iterator) {
//
//        // Obtain data
//        IterableData result = new IterableData(iterator);
//
//        // Update definition, if needed
//        if (iterator instanceof ImportAdapter) {
//            result.getDefinition().parse((ImportAdapter) iterator);
//        }
//
//        // Return
//        return result;
//    }

//    public DataDefinition getDefinition() {
//        return definition;
//    }
//
//    public DataHandle getHandle() {
//        if (handle == null) {
//            handle = new DataHandleInput(this);
//        } else {
//            handle.update(this);
//        }
//        return handle;
//    }

}
