package dao;

import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileNotFoundException; 
import java.io.FileReader; 
import java.io.IOException; 
import java.util.StringTokenizer;

public class CSVReader 
{

    public static String Read(String filePath) 
    { 
        String result="";
        try 
        { 
            //File csv = new File("C://writers.csv"); // CSV文件
            
            File csv = new File(filePath); // CSV文件

            BufferedReader br = new BufferedReader(new FileReader(csv));

            // 读取直到最后一行 
            String line = ""; 
            while ((line = br.readLine()) != null) 
            { 
                result = result+"\r\n"+line; 
            } 
            br.close();
            
        }
        catch (FileNotFoundException e) 
        { 
            // 捕获File对象生成时的异常 
            e.printStackTrace(); 
        }
        catch (IOException e) 
        { 
            // 捕获BufferedReader对象关闭时的异常 
            e.printStackTrace(); 
        } 
        return result;
    } 
}