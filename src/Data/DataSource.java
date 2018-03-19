/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2018 Fabian Prasser and contributors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package Data;

import IO.CSV.CSVSyntax;
import IO.CSV.ImportColumnCSV;
import IO.CSV.ImportConfigurationCSV;
import IO.Excel.ImportColumnExcel;
import IO.Excel.ImportConfigurationExcel;
import IO.ImportConfiguration;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * 该类提供了用于从CSV文件，Excel文件或通过JDBC连接导入数据的配置选项。
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class DataSource { // NO_UCD

    /** The config. */
    private final ImportConfiguration config;

    /**
     * Creates a CSV source.
     */
    private DataSource(File file, Charset charset, char separator, boolean containsHeader) {
        config = new ImportConfigurationCSV(file.getAbsolutePath(), charset, separator, containsHeader);
    }

    public static DataSource createCSVSource(File file, Charset charset, char separator, boolean containsHeader) {
        return new DataSource(file, charset, separator, containsHeader);
    }

    public static DataSource createCSVSource(String file, Charset charset, char separator, boolean containsHeader) {
        return createCSVSource(new File(file), charset, separator, containsHeader);
    }

    public static DataSource createCSVSource(String file){
        return createCSVSource(new File(file), StandardCharsets.UTF_8, CSVSyntax.DEFAULT_DELIMITER,true);
    }
    
    /**
     * Creates an Excel data source.
     */
    public static DataSource createExcelSource(File file, int sheetIndex, boolean containsHeader) {
        return new DataSource(file, sheetIndex, containsHeader);
    }

    public static DataSource createExcelSource(String file, int sheetIndex, boolean containsHeader) {
        return createExcelSource(new File(file), sheetIndex, containsHeader);
    }

    private DataSource(File file, int sheetIndex, boolean containsHeader) {
        config = new ImportConfigurationExcel(file.getAbsolutePath(), sheetIndex, containsHeader);
    }
//
//    /**
//     * Creates a JDBC data source.
//     *
//     * @param url
//     * @param table
//     * @return
//     * @throws SQLException
//     */
//    public static DataSource createJDBCSource(String url, String table) throws SQLException {
//        return new DataSource(url, table);
//    }
//
//    /**
//     * Creates a JDBC data source.
//     *
//     * @param url
//     * @param user
//     * @param password
//     * @param table
//     * @return
//     * @throws SQLException
//     */
//    public static DataSource createJDBCSource(String url, String user, String password, String table) throws SQLException {
//        return new DataSource(url, user, password, table);
//    }
//
//
//    /**
//     * Creates a JDBC data source.
//     *
//     * @param url
//     * @param table
//     * @throws SQLException
//     */
//    private DataSource(String url, String table) throws SQLException {
//        config = new ImportConfigurationJDBC(url, table);
//    }
//
//    /**
//     * Creates a JDBC data source.
//     *
//     * @param url
//     * @param user
//     * @param password
//     * @param table
//     * @throws SQLException
//     */
//    private DataSource(String url, String user, String password, String table) throws SQLException {
//        config = new ImportConfigurationJDBC(url, user, password, table);
//    }
    
    /**
     * Adds a new column.
     *
     * @param index
     */
    public void addColumn(int index) {
        addColumn(index, DataType.STRING);
    }
    
    /**
     * Adds a new column.
     *
     * @param index
     * @param datatype
     */
    public void addColumn(int index, DataType<?> datatype) {
        if (config instanceof ImportConfigurationCSV){
            config.addColumn(new ImportColumnCSV(index, datatype));
        } else if (config instanceof ImportConfigurationExcel) {
            config.addColumn(new ImportColumnExcel(index, datatype));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(index, datatype));
        }
    }
    
    /**
     * Adds a new column.
     *
     * @param index
     * @param datatype
     */
    public void addColumn(int index, DataType<?> datatype, boolean cleansing) {
        if (config instanceof ImportConfigurationCSV){
            config.addColumn(new ImportColumnCSV(index, datatype, cleansing));
        } else if (config instanceof ImportConfigurationExcel) {
            config.addColumn(new ImportColumnExcel(index, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(index, datatype, cleansing));
        }
    }
    
    /**
     * Adds a new column.
     *
     * @param index
     * @param alias
     */
    public void addColumn(int index, String alias) {
        addColumn(index, alias, DataType.STRING);
    }
    
    /**
     * Adds a new column.
     *
     * @param index
     * @param alias
     * @param datatype
     */
    public void addColumn(int index, String alias, DataType<?> datatype) {
        if (config instanceof ImportConfigurationCSV)
            config.addColumn(new ImportColumnCSV(index, alias, datatype));
//        } else if (config instanceof ImportConfigurationExcel) {
//            config.addColumn(new ImportColumnExcel(index, alias, datatype));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(index, alias, datatype));
//        }
    }
    
    /**
     * Adds a new column.
     * 
     * @param index
     * @param alias
     * @param datatype
     * @param cleansing
     */
    public void addColumn(int index, String alias, DataType<?> datatype, boolean cleansing) {
        if (config instanceof ImportConfigurationCSV)
            config.addColumn(new ImportColumnCSV(index, alias, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationExcel) {
//            config.addColumn(new ImportColumnExcel(index, alias, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(index, alias, datatype, cleansing));
//        }
    }
    
    /**
     * Adds a new column.
     *
     * @param name
     */
    public void addColumn(String name) {
        addColumn(name, DataType.STRING);
    }
    
    /**
     * Adds a new column.
     *
     * @param name
     * @param datatype
     */
    public void addColumn(String name, DataType<?> datatype) {
        if (config instanceof ImportConfigurationCSV)
            config.addColumn(new ImportColumnCSV(name, datatype));
//        } else if (config instanceof ImportConfigurationExcel) {
//            config.addColumn(new ImportColumnExcel(name, datatype));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(name, datatype));
//        }
    }
    
    /**
     * Adds a new column.
     *
     * @param name
     * @param datatype
     * @param cleansing
     */
    public void addColumn(String name, DataType<?> datatype, boolean cleansing) {
        if (config instanceof ImportConfigurationCSV)
            config.addColumn(new ImportColumnCSV(name, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationExcel) {
//            config.addColumn(new ImportColumnExcel(name, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(name, datatype, cleansing));
//        }
    }
    
    /**
     * Adds a new column.
     *
     * @param name
     * @param alias
     */
    public void addColumn(String name, String alias) {
        addColumn(name, alias, DataType.STRING);
    }
    
    /**
     * Adds a new column.
     *
     * @param name
     * @param alias
     * @param datatype
     */
    public void addColumn(String name, String alias, DataType<?> datatype) {
        if (config instanceof ImportConfigurationCSV)
            config.addColumn(new ImportColumnCSV(name, alias, datatype));
//        } else if (config instanceof ImportConfigurationExcel) {
//            config.addColumn(new ImportColumnExcel(name, alias, datatype));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(name, alias, datatype));
//        }
    }
    
    /**
     * Adds a new column.
     * @param name
     * @param alias
     * @param datatype
     * @param cleansing
     */
    public void addColumn(String name, String alias, DataType<?> datatype, boolean cleansing) {
        if (config instanceof ImportConfigurationCSV)
            config.addColumn(new ImportColumnCSV(name, alias, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationExcel) {
//            config.addColumn(new ImportColumnExcel(name, alias, datatype, cleansing));
//        } else if (config instanceof ImportConfigurationJDBC) {
//            config.addColumn(new ImportColumnJDBC(name, alias, datatype, cleansing));
//        }
    }
    
    /**
     * Returns the configuration.
     *
     * @return
     */
    protected ImportConfiguration getConfiguration() {
        return config;
    }
}
