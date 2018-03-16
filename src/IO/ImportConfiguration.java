package IO;

import java.util.ArrayList;
import java.util.List;

/**
 * 该抽象类定义了数据读入共有的配置属性
 * @Author CLD
 * @Date 2018/3/16 14:30
 **/
public abstract class ImportConfiguration {

    /**
     * 此列表表示要导入的列
     * 每个元素表示一个要导入的单列
     * @note 只有在列表中的列才会被导入到{处理表单}，其他列会被忽略
     * {@link ImportColumn}
     */
    protected List<ImportColumn> columns = new ArrayList<ImportColumn>();

    /**
     * 添加一列到{处理表单}
     * @param column
     */
     public abstract void addColumn(ImportColumn column);

    /**
     * 获得列表
     * @return
     */
    public List<ImportColumn> getColumns() {
        return columns;
    }

}
