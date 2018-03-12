package IO.CSV;

import java.io.Serializable;

/**
 * @Author CLD
 * @Date 2018/3/9 15:51
 * 使用uniVocity-parsers解析器处理CSV文件
 * 该类用于保存CSV文件的语法
 **/
public class CSVSyntax implements Serializable{


    /**UID*/
    private static final long serialVersionUID=1L;

    /** The delimiter. */
    private char delimiter;

    /** The quote. */
    private char quote;

    /** The escape. */
    private char escape;

    /** The linebreak. */
    private char[] linebreak;

    /** Default values. */
    public static final char   DEFAULT_DELIMITER = ';';

    /** Default values. */
    public static final char   DEFAULT_QUOTE     = '\"';

    /** Default values. */
    public static final char   DEFAULT_ESCAPE    = '\"';

    /** Default values. */
    public static final char[] DEFAULT_LINEBREAK = { '\n' };

    /** Supported line breaks. */
    private static final char[][] linebreaks      = { { '\n' }, { '\r', '\n' }, { '\r' } };

    /** Labels for supported line breaks. */
    private static final String[] linebreaklabels = { "Unix", "Windows", "Mac OS"};

    /**
     * 根据系统选择相应的换行符
     * @param label
     * @return
     */
    public static char[] getLinebreakForLabel(String label){
        int i=0;
        for(String l:linebreaklabels){
            if (l.equals(label)) return linebreaks[i];
            else i++;
        }
        return linebreaks[0];
    }

    /**
     * 初始化CSV的语法类
     */
    public CSVSyntax() {
        this(DEFAULT_DELIMITER);
    }
    public CSVSyntax(final char delimiter) {
        this(delimiter, DEFAULT_QUOTE);
    }
    public CSVSyntax(final char delimiter, final char quote) {
        this(delimiter, quote, DEFAULT_ESCAPE);
    }
    public CSVSyntax(final char delimiter, final char quote, final char escape) { this(delimiter, quote, escape, DEFAULT_LINEBREAK); }
    public CSVSyntax(final char delimiter, final char quote, final char escape, final char[] linebreak) {
        this.delimiter = delimiter;
        this.quote = quote;
        this.escape = escape;
        this.linebreak = linebreak;
    }

    /**
     * get/set 语法变量
     */
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
    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }
    public void setEscape(char escape) {
        this.escape = escape;
    }
    public void setLinebreak(char[] linebreak) {
        this.linebreak = linebreak;
    }
    public void setQuote(char quote) {
        this.quote = quote;
    }
}
