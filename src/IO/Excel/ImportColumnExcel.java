package IO.Excel;

import Data.DataType;
import IO.ImportColumn;

/**
 * 该类用于表示一列Excel数据
 * @Author CLD
 * @Date 2018/3/19 18:44
 **/
public class ImportColumnExcel extends ImportColumn {

    private int index;

    private String name;

    public ImportColumnExcel(int index, DataType<?> datatype) {
        this(index, null, datatype, false);
    }

    public ImportColumnExcel(int index, DataType<?> datatype, boolean cleansing) {
        this(index, null, datatype, cleansing);
    }

    public ImportColumnExcel(int index, String aliasName, DataType<?> datatype) {
        this(index, aliasName, datatype, false);
    }

    public ImportColumnExcel(int index, String aliasName, DataType<?> datatype, boolean cleansing) {
        super(aliasName, datatype, cleansing);
        setIndex(index);
    }

    public ImportColumnExcel(String name, DataType<?> datatype) {
        this(name, null, datatype, false);
    }

    public ImportColumnExcel(String name, DataType<?> datatype, boolean cleansing) {
        this(name, null, datatype, cleansing);
    }

    public ImportColumnExcel(String name, String aliasName, DataType<?> datatype) {
        this(name, aliasName, datatype, false);
    }

    public ImportColumnExcel(String name, String aliasName, DataType<?> datatype, boolean cleansing) {
        super(aliasName, datatype, cleansing);
        setIndex(Integer.MIN_VALUE);
        setName(name);
    }

    public int getIndex() {
        return index;
    }
    public String getName() {
        return name;
    }
    public boolean isIndexSpecified() {
        return index != Integer.MIN_VALUE;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public void setName(String name) {
        this.name = name;
    }

}
