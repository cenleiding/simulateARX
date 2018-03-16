package IO;

import Data.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * 该抽象类为所有数据源的适配器
 * 定义了数据源导入适配器的共有方法和属性。
 * 数据源由{@link ImportConfiguration}描述
 * @Author CLD
 * @Date 2018/3/16 15:14
 **/
public abstract class ImportAdapter implements Iterable<String[]> {

    protected String[] header;

    protected DataType<?>[] dataTypes;

    /**将要导入的列的索引*/
    protected int[] indexes;

    private ImportConfiguration config = null;

    protected ImportAdapter(ImportConfiguration config) {
        this.config = config;
        if (config.getColumns().isEmpty()) {
            throw new IllegalArgumentException("No columns specified");
        }
    }

    public ImportConfiguration getConfig() {
        return config;
    }

    public String[] getHeader() {
        return header;
    }

    public abstract int getProgress();

    protected DataType<?>[] getColumnDatatypes() {

        List<DataType<?>> result = new ArrayList<DataType<?>>();
        for (ImportColumn column : config.getColumns()) {
            result.add(column.getDataType());
        }
        return result.toArray(new DataType[result.size()]);

    }

    /**
     * 根据配置生成相应的适配器
     * @param config
     * @return
     */
    public static ImportAdapter create(ImportConfiguration config){
//        if (config instanceof ImportConfigurationCSV) {
//            return new ImportAdapterCSV((ImportConfigurationCSV) config);
//        } else if (config instanceof ImportConfigurationExcel) {
//            return new ImportAdapterExcel((ImportConfigurationExcel) config);
//        } else if (config instanceof ImportConfigurationJDBC) {
//            return new ImportAdapterJDBC((ImportConfigurationJDBC) config);
//        } else {
            throw new IllegalArgumentException("No adapter defined for this type of configuration");
//        }
    }



}
