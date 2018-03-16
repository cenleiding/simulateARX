package IO.CSV;

import Data.DataType;
import IO.ImportColumn;

/**
 * 该类用于表示一列CSV数据
 * @Author CLD
 * @Date 2018/3/16 16:19
 **/
public class ImportColumnCSV extends ImportColumn {

    private int index;

    private String name;

    /** 构造函数组*/
    public ImportColumnCSV(int index, DataType<?> datatype) {
        this(index, null, datatype, false);
    }

    public ImportColumnCSV(int index, DataType<?> datatype, boolean cleansing) {
        this(index, null, datatype, cleansing);
    }

    public ImportColumnCSV(int index, String aliasName, DataType<?> datatype) {
        this(index, aliasName, datatype, false);
    }

    public ImportColumnCSV(int index, String aliasName, DataType<?> datatype, boolean cleansing) {
        super(aliasName, datatype, cleansing);
        setIndex(index);
    }

    public ImportColumnCSV(String name, DataType<?> datatype) {
        this(name, null, datatype, false);
    }

    public ImportColumnCSV(String name, DataType<?> datatype, boolean cleansing) {
        this(name, null, datatype, cleansing);
    }

    public ImportColumnCSV(String name, String alias, DataType<?> datatype) {
        this(name, alias, datatype, false);
    }

    public ImportColumnCSV(String name, String alias, DataType<?> datatype, boolean cleansing) {
        super(alias, datatype, cleansing);
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
