package IO;

import Data.DataType;

/**
 * 该抽象类用于表示一列数据
 * 每一列数据至少包含{@link #aliasName}和{@link #dataType}.
 * @Author CLD
 * @Date 2018/3/16 14:48
 **/
public abstract class ImportColumn {

    /** 列的别名*/
    private String aliasName;

    /** 列的数据类型*/
    private DataType<?> dataType;

    /** 是否将非匹配值替换为NULL值*/
    private boolean cleansing;

    /** 构造函数*/
    public ImportColumn(String aliasName, DataType<?> dataType) {
       this.aliasName=aliasName;
       this.dataType=dataType;
       cleansing = false;
    }

    /**构造函数*/
    public ImportColumn(String aliasName, DataType<?> dataType, boolean cleansing) {
        this.aliasName=aliasName;
        this.dataType=dataType;
        this.cleansing = cleansing;
    }

    public String getAliasName() {
        return aliasName;
    }
    public DataType<?> getDataType() {
        return dataType;
    }
    public boolean isCleansing() {
        return cleansing;
    }
    public void setAliasName(String aliasName) { this.aliasName = aliasName;}
    public void setCleansing(boolean cleansing) {this.cleansing = cleansing;}
    public void setDataType(DataType<?> dataType) {
        this.dataType = dataType;
    }

}
