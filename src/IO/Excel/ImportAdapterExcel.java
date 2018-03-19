package IO.Excel;

import Data.DataType;
import IO.ImportAdapter;
import IO.ImportColumn;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sun.nio.ch.IOUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @Author CLD
 * @Date 2018/3/19 19:17
 **/
public class ImportAdapterExcel extends ImportAdapter {

    private ImportConfigurationExcel config;

    private Iterator<Row> iterator;

    private Row row;

    /**第一行是否已经返回过*/
    private boolean headerReturned = false;

    /**总行数*/
    private int totalRows;

    /**当前行*/
    private int currentRow = 0;

    private FileInputStream input;

    /**
     * 根据config创建Excel实例
     * @param config
     * @throws IOException
     */
    public ImportAdapterExcel(ImportConfigurationExcel config) throws IOException {

        super(config);
        this.config = config;

        /* Get row iterator */
        input = new FileInputStream(config.getFileLocation());
        Workbook workbook = null;

        if (config.getExcelFileType() == ImportConfigurationExcel.ExcelFileTypes.XLS) {
            workbook = new HSSFWorkbook(input);
        } else if (config.getExcelFileType() == ImportConfigurationExcel.ExcelFileTypes.XLSX) {
            workbook = new XSSFWorkbook(input);
        } else {
            input.close();
            throw new IllegalArgumentException("File type not supported");
        }

        workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);
        Sheet sheet = workbook.getSheetAt(config.getSheetIndex());
        iterator = sheet.iterator();

        /* Get total number of rows */
        totalRows = sheet.getPhysicalNumberOfRows();

        /* Check whether there is actual data within the file */
        if (iterator.hasNext()) {
            row = iterator.next();
            if (config.getContainsHeader()) {
                if (!iterator.hasNext()) {
                    throw new IOException("File contains nothing but header");
                }
            }
        } else {
            throw new IOException("File contains no data");
        }

        // Create header
        header = createHeader();
    }

    @Override
    public boolean hasNext() {
        return row != null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] next() {

        /* Check whether header was already returned */
        if (!headerReturned) {
            headerReturned = true;
            return header;
        }

        /* Create regular row */
        String[] result = new String[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            row.getCell(indexes[i]).setCellType(Cell.CELL_TYPE_STRING);
            result[i] = trim(row.getCell(indexes[i]).getStringCellValue());

            if (!dataTypes[i].isValid(result[i])) {
                if (config.columns.get(i).isCleansing()) {
                    result[i] = DataType.NULL_VALUE;
                } else {
                    throw new IllegalArgumentException("Data value does not match data type");
                }
            }
        }

        /* Fetches the next row, which will be used in next iteration */
        if (iterator.hasNext()) {
            row = iterator.next();
            currentRow++;
        } else {
            row = null;
            try {
                input.close();
            } catch (Exception e) {
                /* Die silently */
            }
        }

        /* Return resulting row */
        return result;
    }

    private String[] createHeader() {

        /* Preparation work */
        if (config.getContainsHeader()) {
            config.prepare(row);
        }
        indexes = getIndexesToImport();
        dataTypes = getColumnDatatypes();

        /* Initialization */
        String[] header = new String[config.getColumns().size()];
        List<ImportColumn> columns = config.getColumns();

        /* Create header */
        for (int i = 0, len = columns.size(); i < len; i++) {

            ImportColumn column = columns.get(i);

            row.getCell(((ImportColumnExcel) column).getIndex()).setCellType(Cell.CELL_TYPE_STRING);
            String name = trim(row.getCell(((ImportColumnExcel) column).getIndex()).getStringCellValue());

            if (config.getContainsHeader() && !name.equals("")) {
                /* Assign name of file itself */
                header[i] = name;
            } else {
                /* Nothing defined in header (or empty), build name manually */
                header[i] = "Column #" + ((ImportColumnExcel) column).getIndex();
            }

            if (column.getAliasName() != null) {
                /* Name has been assigned explicitly */
                header[i] = column.getAliasName();
            }

            column.setAliasName(header[i]);
        }

        /* Fetch next row in preparation for next iteration */
        if (config.getContainsHeader()) {

            if (iterator.hasNext()) {
                row = iterator.next();
                currentRow++;
            } else {
                row = null;
            }
        }

        /* Return header */
        return header;
    }

    protected int[] getIndexesToImport() {

        /* Get indexes to import from */
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (ImportColumn column : config.getColumns()) {
            indexes.add(((ImportColumnExcel) column).getIndex());
        }

        int[] result = new int[indexes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = indexes.get(i);
        }
        return result;
    }

    public static String trim(String input) {
        return input == null ? null : input.trim();
    }

}
