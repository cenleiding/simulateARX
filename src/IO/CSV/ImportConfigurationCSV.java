package IO.CSV;

import IO.ImportColumn;
import IO.ImportConfiguration;

import java.nio.charset.Charset;

/**
 * @Author CLD
 * @Date 2018/3/16 15:50
 **/
public class ImportConfigurationCSV extends ImportConfiguration {

    /** 分隔符*/
    private final char delimiter;

    /** 引用*/
    private final char quote  ;

    /**换行符*/
    private final char[] linebreak;

    /**换码符*/
    private final char escape;

    /**文件编码*/
    private final Charset charset;

    /**文件路径*/
    private String fileLocation;

    /** 第一行是否包含头(列名)*/
    private boolean containsHeader;

    /** 构造函数组*/
    public ImportConfigurationCSV(String fileLocation,Charset charset,boolean containsHeader) {
        this(fileLocation, charset, CSVSyntax.DEFAULT_DELIMITER, CSVSyntax.DEFAULT_QUOTE, CSVSyntax.DEFAULT_ESCAPE, containsHeader);
    }

    public ImportConfigurationCSV(String fileLocation,Charset charset,char delimiter,boolean containsHeader) {
        this(fileLocation, charset, delimiter, CSVSyntax.DEFAULT_QUOTE, CSVSyntax.DEFAULT_ESCAPE, containsHeader);
    }
    public ImportConfigurationCSV(String fileLocation,Charset charset,char delimiter,char quote,boolean containsHeader) {
        this(fileLocation, charset, delimiter, quote, CSVSyntax.DEFAULT_ESCAPE, containsHeader);
    }

    public ImportConfigurationCSV(String fileLocation,Charset charset,char delimiter,char quote,char escape,boolean containsHeader) {
        this(fileLocation, charset, delimiter, quote, escape, CSVSyntax.DEFAULT_LINEBREAK, containsHeader);
    }

    public ImportConfigurationCSV(String fileLocation,
                                  Charset charset,
                                  char delimiter,
                                  char quote,
                                  char escape,
                                  char[] linebreak,
                                  boolean containsHeader) {

        this.fileLocation=fileLocation;
        this.quote = quote;
        this.delimiter = delimiter;
        this.escape = escape;
        this.containsHeader = containsHeader;
        this.linebreak = linebreak;
        this.charset = charset;
    }

    /**
     * 向处理表单中添加一列
     * 且保证该列是CSV格式
     * @param column
     */
    public void addColumn(ImportColumn column) {

        if (!(column instanceof ImportColumnCSV)) {
            throw new IllegalArgumentException("Column needs to be of type CSVColumn");
        }

        if (!((ImportColumnCSV) column).isIndexSpecified() &&
                !getContainsHeader()) {
            final String ERROR = "Adressing columns by name is only possible if the source contains a header";
            throw new IllegalArgumentException(ERROR);
        }

        for (ImportColumn c : columns) {
            if (((ImportColumnCSV) column).isIndexSpecified() &&
                    (((ImportColumnCSV) column).getIndex() == ((ImportColumnCSV) c).getIndex())) {
                throw new IllegalArgumentException("Column for this index already assigned");
            }

            if (!((ImportColumnCSV) column).isIndexSpecified() &&
                    ((ImportColumnCSV) column).getName().equals(((ImportColumnCSV) c).getName())) {
                throw new IllegalArgumentException("Column for this name already assigned");
            }

            if ((column.getAliasName() != null) && (c.getAliasName() != null) &&
                    c.getAliasName().equals(column.getAliasName())) {
                throw new IllegalArgumentException("Column names need to be unique");
            }
        }

        columns.add(column);
    }

    public Charset getCharset() {
        return charset;
    }


    public char getDelimiter() {
        return delimiter;
    }

    public char getEscape() {
        return escape;
    }

    public char[] getLinebreak() {
        return linebreak;
    }

    public char getQuote() {
        return quote;
    }

    public boolean getContainsHeader() {
        return containsHeader;
    }

    public void setContainsHeader(boolean containsHeader) {
        this.containsHeader = containsHeader;
    }

    /**
     * 根据列的头，设置索引
     *
     * @param row the row
     */
    public void prepare(String[] row) {

        for (ImportColumn c : super.getColumns()) {
            ImportColumnCSV column = (ImportColumnCSV) c;
            if (!column.isIndexSpecified()) {
                boolean found = false;
                for (int i = 0; i < row.length; i++) {
                    if (row[i].equals(column.getName())) {
                        found = true;
                        column.setIndex(i);
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("Index for column '" + column.getName() + "' couldn't be found");
                }
            }
        }
    }

}
