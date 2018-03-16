package IO.CSV;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import Data.DataType;
import org.apache.poi.ss.formula.functions.T;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * 用于实现CSV文件的读入
 * @Author CLD
 * @Date 2018/3/12 9:20
 **/
public class CSVDataInput {

    private final Reader reader;

    private final CsvParserSettings settings;

    private final boolean cleansing;

    private final DataType<T>[] datatypes;

    /**
     *用于存放文件信息
     */
    private static class LazyFileReader extends Reader {

        private InputStreamReader reader =null;

        private final File file;

        private  final Charset charset;


        private LazyFileReader(File file,Charset charset) {
            this.charset=charset;
            this.file=file;
        }

        @Override
        public void close() throws IOException {
            if(reader!=null){
                reader.close();
            }
        }

        @Override
        @SuppressWarnings("resource")
        public int read(char[] cbuf,int off,int len) throws IOException{
            reader=reader!=null?reader:new InputStreamReader(new FileInputStream(file),charset);
            return  reader.read(cbuf,off,len);
        }
    }


    /**
     * 构造函数组
     * @param file
     * @param charset
     * @throws IOException
     */
    public CSVDataInput(final File file,final Charset charset) throws IOException{
        this(file,charset,CSVSyntax.DEFAULT_DELIMITER);
    }

    public CSVDataInput(final File file, final Charset charset, final char delimiter) throws IOException {
        this(file, charset, delimiter, CSVSyntax.DEFAULT_QUOTE);
    }

    public CSVDataInput(final File file, final Charset charset, final char delimiter, final char quote) throws IOException {
        this(file, charset, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE);
    }

    public CSVDataInput(final File file, final Charset charset, final char delimiter, final char quote, final char escape) throws IOException {
        this(file, charset, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK);
    }

    public CSVDataInput(final File file, final Charset charset, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this(new LazyFileReader(file, charset), delimiter, quote, escape, linebreak, null);
    }

    public CSVDataInput(final File file, final Charset charset,  final CSVSyntax config) throws IOException {
        this(file, charset, config, null);
    }

    public CSVDataInput(final File file, final Charset charset, final CSVSyntax config, final DataType<T>[] datatype) throws IOException {
        this(new LazyFileReader(file, charset), config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak(), datatype);
    }

    public CSVDataInput(final InputStream stream, final Charset charset) throws IOException {
        this(stream, charset, CSVSyntax.DEFAULT_DELIMITER);
    }

    public CSVDataInput(final InputStream stream, final Charset charset, final char delimiter) throws IOException {
        this(stream, charset, delimiter, CSVSyntax.DEFAULT_QUOTE);
    }

    public CSVDataInput(final InputStream stream, final Charset charset, final char delimiter, final char quote) throws IOException {
        this(stream, charset, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE);
    }

    public CSVDataInput(final InputStream stream, final Charset charset, final char delimiter, final char quote, final char escape) throws IOException {
        this(stream, charset, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK);
    }

    public CSVDataInput(final InputStream stream, final Charset charset, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this(new InputStreamReader(stream, charset), delimiter, quote, escape, linebreak, null);
    }

    public CSVDataInput(final InputStream stream, final Charset charset, final CSVSyntax config) throws IOException {
        this(stream, charset, config, null);
    }

    public CSVDataInput(final InputStream stream, final Charset charset, final CSVSyntax config, final DataType<T>[] datatypes) throws IOException {
        this(new InputStreamReader(stream, charset), config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak(), datatypes);
    }

    public CSVDataInput(final String filename, final Charset charset) throws IOException {
        this(filename, charset, CSVSyntax.DEFAULT_DELIMITER);
    }

    public CSVDataInput(final String filename, final Charset charset, final char delimiter) throws IOException {
        this(filename, charset, delimiter, CSVSyntax.DEFAULT_QUOTE);
    }

    public CSVDataInput(final String filename, final Charset charset, final char delimiter, final char quote) throws IOException {
        this(filename, charset, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE);
    }

    public CSVDataInput(final String filename, final Charset charset, final char delimiter, final char quote, final char escape) throws IOException {
        this(filename, charset, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK);
    }

    public CSVDataInput(final String filename, final Charset charset, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this(new File(filename), charset, delimiter, quote, escape, linebreak);
    }

    public CSVDataInput(final String filename, final Charset charset, final CSVSyntax config) throws IOException {
        this(filename, charset, config, null);
    }

    public CSVDataInput(final String filename, final Charset charset, final CSVSyntax config, final DataType<T>[] datatypes) throws IOException {
        this(new LazyFileReader(new File(filename), charset), config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak(), datatypes);
    }

    public CSVDataInput(final Reader reader, final char delimiter, final char quote, final char escape, final char[] linebreak, final DataType<T>[] datatypes) throws IOException {
        this.reader = reader;
        this.datatypes = datatypes;
        if (datatypes != null) {
            cleansing = true;
        } else {
            cleansing = false;
        }
        settings = createSettings(delimiter, quote, escape, linebreak);
    }

    /**
     * 提供迭代器。
     * @return
     */
    public Iterator<String[]> iterator(){
        return new Iterator<String[]>() {
            boolean initialized=false;
            CsvParser parser=null;
            String[] next=null;

            @Override
            public boolean hasNext() {
                initParser();
                boolean result=next!=null;
                if(!result&&parser!=null){
                    parser.stopParsing();
                    parser=null;
                }
                return result;
            }

            @Override
            public String[] next() {
                initParser();
                String[] result=next;
                next=parser.parseNext();
                //将不符合数据类型的数据用NULL代替
                if (cleansing) {
                    if (result.length != datatypes.length) {
                        throw new IllegalArgumentException("More columns available in CSV file than data types specified");
                    }

                    for (int i = 0; i < result.length; i++) {
                        if (!datatypes[i].isValid(result[i])) {
                            result[i] = DataType.NULL_VALUE;
                        }
                    }
                }
                return result;
            }

            private void initParser() {
                if (!initialized) {
                    parser = new CsvParser(settings);
                    parser.beginParsing(reader);
                    next = parser.parseNext();
                    initialized = true;
                }
            }
        };
    }

    /**
     * CSV语法配置
     * @param delimiter
     * @param quote
     * @param escape
     * @param linebreak
     * @return
     */
    private CsvParserSettings createSettings(final char delimiter, final char quote, final char escape, final char[] linebreak) {
        CsvFormat format = new CsvFormat();
        format.setDelimiter(delimiter);
        format.setQuote(quote);
        format.setQuoteEscape(escape);
        format.setLineSeparator(linebreak);
        format.setNormalizedNewline(CSVSyntax.getNormalizedLinebreak(linebreak));
        format.setComment('\0');

        CsvParserSettings settings = new CsvParserSettings();
        settings.setEmptyValue("");
        settings.setNullValue("");
        settings.setFormat(format);
        return settings;
    }
}
