package Data;

import IO.CSV.CSVDataOutput;
import IO.CSV.ImportConfigurationCSV;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @Author CLD
 * @Date 2018/3/15 15:38
 **/
public class test {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello test!");
//        Data data=Data.create("C:\\Users\\CLD\\Desktop\\1.csv", StandardCharsets.UTF_8,',');
//        Iterator<String[]> iterator = data.iterator();
//        CSVDataOutput csvoutput=new CSVDataOutput("C:\\Users\\CLD\\Desktop\\LALALA.csv",',');
//        csvoutput.write(iterator);
//        while (iterator.hasNext()){
//            for(String s:iterator.next())
//            System.out.print(s);
//            System.out.println();
//        }

        DataSource source = DataSource.createExcelSource("C:\\Users\\CLD\\Desktop\\1.xls",0,true);
//        source.addColumn(2, DataType.STRING);
//        source.addColumn(2, DataType.STRING);
//        source.addColumn(3, DataType.STRING);
//        source.addColumn(4, DataType.STRING);
        Data data = Data.create(source);
        Iterator<String[]> iterator = data.iterator();
//        CSVDataOutput csvoutput=new CSVDataOutput("C:\\Users\\CLD\\Desktop\\LALALA.csv",',');
//        csvoutput.write(iterator);
        while (iterator.hasNext()){
            for(String s:iterator.next())
                System.out.print(s);
            System.out.println();
        }

    }

}
