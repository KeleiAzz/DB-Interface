package dao;

import java.sql.*;
import java.util.ArrayList;
import model.RecordContent;

/**
 *
 * @author lance
 */
public class DBConnector {
private int counter;
private ArrayList retureResultArrayList=new ArrayList();
private int columnCount;
 
public void DBConnector()
{}

// return a arraylist of a querry, without any parsing
public ResultSet executeQuerry(String querry)
{

           // 驱动程序名
           String driver = "com.mysql.jdbc.Driver";

           // URL指向要访问的数据库名scutcs
           String url = "jdbc:mysql://localhost:3306/ml";

           // MySQL配置时的用户名
           String user = "root"; 

           // MySQL配置时的密码
           String password = "1423";

           try 
           { 
                // 加载驱动程序
                Class.forName(driver);

                // 连续数据库
                Connection conn = DriverManager.getConnection(url, user, password);

                if(!conn.isClosed()) 
                 System.out.println("Succeeded connecting to the Database!-for querry");
                // statement用来执行SQL语句
                Statement statement = conn.createStatement();
                // 要执行的SQL语句
                String sql = querry;
                System.out.println(sql);
                // 结果集
                ResultSet rs = statement.executeQuery(sql);
                return rs;
                /*下面的实现是从rs里面去读行数，并把每一个tuple变成一个字符数组输出。没有必要
                ResultSetMetaData rsmd = rs.getMetaData() ; 
                columnCount = rsmd.getColumnCount();
                retureResultArrayList.add(columnCount);
                rs.beforeFirst();
                
                while(rs.next())
                {
                    String[] tempStringArray = new String[]{"","","","","","","","","","","","","","","",""};
                    for(counter = 1;counter<=columnCount;counter++)
                    {
                        tempStringArray[counter-1] = rs.getString(counter);
                    }
                    retureResultArrayList.add(tempStringArray);
                }

                rs.close();
                conn.close();
                return retureResultArrayList;
                * */

               } 
           catch(ClassNotFoundException e)
           {
                System.out.println("Sorry,can`t find the Driver!"); 
                e.printStackTrace();
                return null;
            }           
           catch(SQLException e) 
           {
                e.printStackTrace();
                return null;
           } 
           catch(Exception e) 
           {
                e.printStackTrace();
                return null;
           } 
}

// execute some querry which do not need and result returned. 
public void executeUpdate(String querry)
{
  // 驱动程序名
           String driver = "com.mysql.jdbc.Driver";

           // URL指向要访问的数据库名scutcs
           String url = "jdbc:mysql://localhost:3306/ml";

           // MySQL配置时的用户名
           String user = "root"; 

           // MySQL配置时的密码
           String password = "root";

           try 
           { 
                // 加载驱动程序
                Class.forName(driver);

                // 连续数据库
                Connection conn = DriverManager.getConnection(url, user, password);

                if(!conn.isClosed()) 
                 System.out.println("Succeeded connecting to the Database!－for insert");
                // statement用来执行SQL语句
                Statement statement = conn.createStatement();
                // 要执行的SQL语句
                String sql = querry;
                System.out.println(sql);
                // 结果集
                statement.execute(sql);
               
                conn.close();

               } 
           catch(ClassNotFoundException e)
           {
                System.out.println("Sorry,can`t find the Driver!"); 
                e.printStackTrace();
            }           
           catch(SQLException e) 
           {
                e.printStackTrace();
           } 
           catch(Exception e) 
           {
                e.printStackTrace();
           } 
}

    public static void InsertRecord(RecordContent record)
    {

           // 驱动程序名
           String driver = "com.mysql.jdbc.Driver";

           // URL指向要访问的数据库名scutcs
           String url = "jdbc:mysql://127.0.0.1:3306/ml";

           // MySQL配置时的用户名
           String user = "root"; 

           // MySQL配置时的密码
           String password = "";

           try 
           { 
                // 加载驱动程序
                Class.forName(driver);

                // 连续数据库
                Connection conn = DriverManager.getConnection(url, user, password);

                if(conn.isClosed()) 
                 System.out.println("Failed in connecting to the Database!");

                // statement用来执行SQL语句
                Statement statement = conn.createStatement();

                String sql = new String();
                if(record.getToken().equals(""))//entire document into merged_link_content
                {
                    sql = "insert into merged_link_content values ('"+record.getCompanyName().replace("'","")+"','"+record.getLink().replace("'","")+"',"+record.getYear()+",'"+record.getContent().replace("'","").replace("’","").replace("‟","")+"','"+record.getGroup()+"')";
                }
                else
                {
                    // 要执行的SQL语句
                    sql = "insert into extracted_link_content values ('"+record.getCompanyName().replace("'","")+"','"+record.getLink().replace("'","")+"','"+record.getContent().replace("'","").replace("’","").replace("‟","")+"','"+record.getToken()+"')";
                }
                //System.out.println(sql);
                statement.executeUpdate(sql);
            
                conn.close();

           } 
           catch(ClassNotFoundException e) 
           {
            e.printStackTrace();
           } 
           catch(SQLException e) 
           {
            e.printStackTrace();
           } 
           catch(Exception e) 
           {
            e.printStackTrace();
           } 
    } 
    
}
