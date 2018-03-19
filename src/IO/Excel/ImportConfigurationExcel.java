package IO.Excel;

import Data.DataType;
import IO.CSV.ImportColumnCSV;
import IO.ImportColumn;
import IO.ImportConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


/**
 * @Author CLD
 * @Date 2018/3/19 18:56
 **/
public class ImportConfigurationExcel extends ImportConfiguration {

    public enum ExcelFileTypes {

        /**  TODO */
        XLS,

        /**  TODO */
        XLSX
    };

    /**当前所用文件类型*/
    private ExcelFileTypes excelFileType;

    /**文件地址*/
    private String fileLocation;

    private int sheetIndex;

    /**指示第一行是否包含标题（列的名称）。*/
    private boolean containsHeader;

    /**
     * 初始化一个config实例
     * @param fileLocation
     * @param excelFileType
     * @param sheetIndex
     * @param containsHeader
     */
    public ImportConfigurationExcel(String fileLocation,
                                    ExcelFileTypes excelFileType,
                                    int sheetIndex,
                                    boolean containsHeader) {

        setFileLocation(fileLocation);
        setExcelFileType(excelFileType);
        setSheetIndex(sheetIndex);
        setContainsHeader(containsHeader);
        super.setAllColumn(true);
    }

    /**
     * 初始化一个config实例
     * @param fileLocation
     * @param sheetIndex
     * @param containsHeader
     */
    public ImportConfigurationExcel(String fileLocation,
                                    int sheetIndex,
                                    boolean containsHeader) {

        ExcelFileTypes excelFileType;
        String ext = FilenameUtils.getExtension(fileLocation);

        switch (ext) {
            case "xls":
                excelFileType = ExcelFileTypes.XLS;
                break;

            default:
                excelFileType = ExcelFileTypes.XLSX;
                break;
        }

        setFileLocation(fileLocation);
        setSheetIndex(sheetIndex);
        setContainsHeader(containsHeader);
        setExcelFileType(excelFileType);
        super.setAllColumn(true);
    }

    /**
     * 添加行
     * @param column
     */
    @Override
    public void addColumn(ImportColumn column) {

        if (!(column instanceof ImportColumnExcel)) {
            throw new IllegalArgumentException("Column needs to be of type ExcelColumn");
        }

        if (!((ImportColumnExcel) column).isIndexSpecified() &&
                !this.getContainsHeader()){
            final String ERROR = "Adressing columns by name is only possible if the source contains a header";
            throw new IllegalArgumentException(ERROR);
        }

        for (ImportColumn c : columns) {
            if (((ImportColumnExcel) column).isIndexSpecified() &&
                    ((ImportColumnExcel) column).getIndex() == ((ImportColumnExcel) c).getIndex()) {
                throw new IllegalArgumentException("Column for this index already assigned");
            }

            if (!((ImportColumnExcel) column).isIndexSpecified() &&
                    ((ImportColumnExcel) column).getName().equals(((ImportColumnExcel) c).getName())) {
                throw new IllegalArgumentException("Column for this name already assigned");
            }

            if (column.getAliasName() != null && c.getAliasName() != null &&
                    c.getAliasName().equals(column.getAliasName())) {
                throw new IllegalArgumentException("Column names need to be unique");
            }
        }
        this.columns.add(column);
    }

    public boolean getContainsHeader() {
        return containsHeader;
    }

    public ExcelFileTypes getExcelFileType() {
        return excelFileType;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setContainsHeader(boolean containsHeader) {
        this.containsHeader = containsHeader;
    }

    public void setExcelFileType(ExcelFileTypes excelFileType) {
        this.excelFileType = excelFileType;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * 根据标题行确定索引
     * @param row
     */
    public void prepare(Row row) {
        if(isAllColumn){
            for(int i=0;i<row.getPhysicalNumberOfCells();i++){
                ImportColumnExcel column= new ImportColumnExcel(row.getCell(i).getStringCellValue(), DataType.STRING);
                column.setIndex(i);
                this.addColumn(column);
            }
            this.setAllColumn(false);
        }
        else{
            for (ImportColumn c : super.getColumns()) {
                ImportColumnExcel column = (ImportColumnExcel) c;
                if (!column.isIndexSpecified()) {
                    boolean found = false;
                    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                        row.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
                        if (row.getCell(i).getStringCellValue().equals(column.getName())) {
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

}
