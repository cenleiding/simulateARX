package IO.CSV;

import Data.DataType;
import IO.ImportAdapter;
import IO.ImportColumn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 该类用于CSV文件的读入
 * @Author CLD
 * @Date 2018/3/16 17:04
 **/
public class ImportAdapterCSV extends ImportAdapter {

    private ImportConfigurationCSV config;

    /**size*/
    private long bytesTotal;

    private CSVDataInput in;

    private Iterator<String[]> it;

    private String[] row;

    /**用于指示第一行(列名)是否已经返回*/
    private boolean headerReturned = false;

    public ImportAdapterCSV(ImportConfigurationCSV config) throws IOException {

        super(config);
        this.config = config;
        this.bytesTotal = new File(config.getFileLocation()).length();
        FileInputStream cin =new FileInputStream(new File(config.getFileLocation()));

        /* Get CSV iterator */
        in = new CSVDataInput(cin, config.getCharset(), config.getDelimiter(), config.getQuote(), config.getEscape(), config.getLinebreak());
        it = in.iterator();

        /* Check whether there is actual data within the CSV file */
        if (it.hasNext()) {
            row = it.next();
            if (config.getContainsHeader()) {
                if (!it.hasNext()) {
                    throw new IOException("CSV contains nothing but header");
                }
            }
        } else {
            throw new IOException("CSV file contains no data");
        }

        // Create header
        header = createHeader();
    }

    @Override
    public boolean hasNext() {
        return row != null;
    }

    /**
     * 返回下一行中的指定列，并根据情况清洗数据。
     * @return
     */
    @Override
    public String[] next() {

        /* Check whether header was already returned */
        if (!headerReturned) {
            headerReturned = true;
            return header;
        }

        String[] result;
        try {
            result = new String[indexes.length];
            for (int i = 0; i < indexes.length; i++) {
                result[i] = row[indexes[i]];
                if (!dataTypes[i].isValid(result[i])) {
                    if (config.columns.get(i).isCleansing()) {
                        result[i] = DataType.NULL_VALUE;
                    } else {
                        throw new IllegalArgumentException("Data value does not match data type");
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Inconsistent length of header and records");
        }

        /* Fetches the next row, which will be used in next iteration */
        if (it.hasNext()) {
            row = it.next();
        } else {
            row = null;
        }

        /* Return resulting row */
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * 返回将要使用的列的名称
     * @return
     */
    private String[] createHeader() {

        /* Preparation work */
        if (config.getContainsHeader()) this.config.prepare(row);
        this.indexes = getIndexesToImport();
        this.dataTypes = getColumnDatatypes();

        /* Initialization */
        String[] header = new String[config.getColumns().size()];
        List<ImportColumn> columns = config.getColumns();

        /* Create header */
        for (int i = 0, len = columns.size(); i < len; i++) {

            ImportColumn column = columns.get(i);

            /* Check whether there is a header, which is not empty */
            if (config.getContainsHeader() &&
                    !row[((ImportColumnCSV) column).getIndex()].equals("")) {

                /* Assign name of CSV file itself */
                header[i] = row[((ImportColumnCSV) column).getIndex()];
            } else {
                /* Nothing defined in header (or empty), build name manually */
                header[i] = "Column #" + ((ImportColumnCSV) column).getIndex();
            }

            if (column.getAliasName() != null) {
                /* Name has been assigned explicitly */
                header[i] = column.getAliasName();
            }
            column.setAliasName(header[i]);
        }

        /* Fetch next row in preparation for next iteration */
        if (config.getContainsHeader()) {
            if (it.hasNext()) {
                row = it.next();
            } else {
                row = null;
            }
        }

        /* Return header */
        return header;
    }

    /**
     * 返回应导入的列的索引
     * @return
     */
    protected int[] getIndexesToImport() {

        /* Get indexes to import from */
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (ImportColumn column : config.getColumns()) {
            indexes.add(((ImportColumnCSV) column).getIndex());
        }

        int[] result = new int[indexes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = indexes.get(i);
        }

        return result;
    }


}
