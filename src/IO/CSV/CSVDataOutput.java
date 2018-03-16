package IO.CSV;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import java.io.*;
import java.util.Iterator;

/**
 * 用于CSV数据的输出
 * @Author CLD
 * @Date 2018/3/15 16:04
 **/
public class CSVDataOutput {

    private final Writer writer;

    private final CsvWriterSettings settings;

    private boolean close;

    /**构造函数组*/

    public CSVDataOutput(final File file) throws IOException {
        this(file, CSVSyntax.DEFAULT_DELIMITER);
    }

    public CSVDataOutput(final File file, final char delimiter) throws IOException {
        this(file, delimiter, CSVSyntax.DEFAULT_QUOTE);
    }

    public CSVDataOutput(final File file, final char delimiter, final char quote) throws IOException {
        this(file, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE);
    }

    public CSVDataOutput(final File file, final char delimiter, final char quote, final char escape) throws IOException {
        this(file, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK);
    }

    public CSVDataOutput(final File file, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this(new FileWriter(file), delimiter, quote, escape, linebreak);
    }

    public CSVDataOutput(final File file, final CSVSyntax config) throws IOException {
        this(file, config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak());
    }

    public CSVDataOutput(final OutputStream stream) throws IOException {
        this(stream, CSVSyntax.DEFAULT_DELIMITER);
    }

    public CSVDataOutput(final OutputStream stream, final char delimiter) throws IOException {
        this(stream, delimiter, CSVSyntax.DEFAULT_QUOTE);
    }

    public CSVDataOutput(final OutputStream stream, final char delimiter, final char quote) throws IOException {
        this(stream, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE);
    }

    public CSVDataOutput(final OutputStream stream, final char delimiter, final char quote, final char escape) throws IOException {
        this(stream, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK);
    }

    public CSVDataOutput(final OutputStream stream, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this(new OutputStreamWriter(stream), delimiter, quote, escape, linebreak);
        close = false;
    }

    public CSVDataOutput(final OutputStream stream, final CSVSyntax config) throws IOException {
        this(stream, config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak());
    }

    public CSVDataOutput(final String filename) throws IOException {
        this(filename, CSVSyntax.DEFAULT_DELIMITER);
    }

    public CSVDataOutput(final String filename, final char delimiter) throws IOException {
        this(filename, delimiter, CSVSyntax.DEFAULT_QUOTE);
    }

    public CSVDataOutput(final String filename, final char delimiter, final char quote) throws IOException {
        this(filename, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE);
    }

    public CSVDataOutput(final String filename, final char delimiter, final char quote, final char escape) throws IOException {
        this(filename, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK);
    }

    public CSVDataOutput(final String filename, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this(new File(filename), delimiter, quote, escape, linebreak);
    }

    public CSVDataOutput(String filename, final CSVSyntax config) throws IOException {
        this(filename, config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak());
    }

    public CSVDataOutput(final Writer writer, final char delimiter, final char quote, final char escape, final char[] linebreak) throws IOException {
        this.writer = writer;
        close = true;
        settings = createSettings(delimiter, quote, escape, linebreak);
    }


    /**
     * 写Iterator数据
     * @param iterator
     * @throws IOException
     */
    public void write(final Iterator<String[]> iterator) throws IOException {

        CsvWriter csvwriter = new CsvWriter(writer, settings);
        while (iterator.hasNext()) {
            csvwriter.writeRow((Object[]) iterator.next());
        }
        if (close) {
            csvwriter.close();
        } else {
            csvwriter.flush();
        }
    }

    /**
     * 写数组类型数据
     * @param hierarchy
     * @throws IOException
     */
    public void write(final String[][] hierarchy) throws IOException {

        CsvWriter csvwriter = new CsvWriter(writer, settings);
        for (int i = 0; i < hierarchy.length; i++) {
            csvwriter.writeRow((Object[]) hierarchy[i]);
        }
        if (close) {
            csvwriter.close();
        } else {
            csvwriter.flush();
        }
    }

    /**
     * CSV格式设置
     * @param delimiter
     * @param quote
     * @param escape
     * @param linebreak
     * @return
     */
    private CsvWriterSettings createSettings(final char delimiter, final char quote, final char escape, final char[] linebreak) {
        CsvFormat format = new CsvFormat();
        format.setDelimiter(delimiter);
        format.setQuote(quote);
        format.setQuoteEscape(escape);
        format.setLineSeparator(linebreak);
        format.setNormalizedNewline(CSVSyntax.getNormalizedLinebreak(linebreak));

        CsvWriterSettings settings = new CsvWriterSettings();
        settings.setEmptyValue("");
        settings.setNullValue("");
        settings.setFormat(format);
        return settings;
    }

}
