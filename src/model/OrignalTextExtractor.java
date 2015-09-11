/*
 * Extract content which satisfy specific requirement (keywords, year, specific sentence/ sentences/ paragraph)
 */
package model;

import dao.CSVWriter;
import dao.DBConnector;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.MainFrame;

/**
 *
 * @author lance
 */
public class OrignalTextExtractor 
{
    public static ArrayList<String> ExtractContentFromCompanyProfile(int yearFrom, int yearTo, ArrayList<String> keyWords, ArrayList<String> orKeywords, ArrayList<String> excludeWords, String flag,int numSentence,MainFrame parentMainFrame,String targetPath)
    {
        ArrayList<String> hits=new ArrayList();
        DBConnector dbConnector = new DBConnector();
        // link='http://www.aosmith.com/News/Default.aspx?coll_id=35&coll_name=2012%20News%20Archive'// A. O. Smith
        // link='http://www.meritsolutions.com/resources/whitepapers/2011-Top-20-ERP-Vendors.pdf'//A&M
        // link='http://www.zrp.com.cn/en_news.asp?id=131' // P&G
        //company = 'GNC Holdings'
        //company = 'Power Integrations Inc'
//        String sql = "SELECT CONVERT(content USING utf8) AS content,link,company_name FROM link_content_2014 where year>="+yearFrom+" and year <="+yearTo+";";
        String sql = "SELECT CONVERT(content USING utf8) AS content,link,company_name FROM auto_test_link_content;";
        ResultSet resultSet = dbConnector.executeQuerry(sql);
        try 
        {
            //if resultset contains nothing, return a empty arrayList
            int resultSetSize=0;
            while(resultSet.next())
            {
                resultSetSize++;
            }
            if(resultSetSize==0)
            {
                return hits;
            }
            //after count, put the cursor back before 1st
            resultSet.beforeFirst();
            
            //get the 1st company name and record it as currentCampany
            resultSet.next();
            String currentCompany = resultSet.getString("company_name").replace(",", " ");
            String extractedContentCurrentCompany=new String("==========\r\n");
            resultSet.beforeFirst();
            while(resultSet.next()) 
            {
                String content = resultSet.getString("content");
                String campany = resultSet.getString("company_name").replace(",", " ");
                String link = resultSet.getString("link");
                
                System.out.println("current link:"+link);
                
                String result = HitChecker.check(content, keyWords, orKeywords, excludeWords, flag, numSentence);
                result=eliminateSpecialCharByUnicode(result);
                if(result!=null&&result.length()!=0)//paragraph hits, return a string
                {
                    System.out.println("++++++++++++++++++hit result+++++++++++++++++++++");
                    System.out.println(result);
                    parentMainFrame.insertTextPane("hit found in Campany:"+campany+"\r\n", Color.black); 
                    //write to hits 
                    hits.add(campany+","+link+","+result.replace(",", " ")+",\n");
                    //record the extracted content to extractedContentCurrentCompany, out put if it comes to next company
                    if(campany.equals(currentCompany))//compare company names
                    {
                        extractedContentCurrentCompany+=link+"\r\n"+result+"\r\n"+"==========\r\n";
                    }
                    else 
                    {
                        if(extractedContentCurrentCompany.length()>"==========\r\n".length())
                        {
                            outputExtractedContent4CurrentCompany(currentCompany,extractedContentCurrentCompany,targetPath);
                            extractedContentCurrentCompany=new String("==========\r\n");
                        }
                        extractedContentCurrentCompany+=link+"\r\n"+result+"\r\n"+"==========\r\n";
                        currentCompany=campany;
                    }
                }
                else//paragraph not hits, return null
                {
                    //currently do nothing
                }
            }
            //after exit while, if there is some content in extractedContentCurrentCompany, it should also be output
            if(extractedContentCurrentCompany.length()>"==========\r\n".length())
            {
                outputExtractedContent4CurrentCompany(currentCompany,extractedContentCurrentCompany,targetPath);
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(OrignalTextExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hits;
            
    }

    private static void outputExtractedContent4CurrentCompany(String currentCompany, String extractedContentCurrentCompany,String targetPath) 
    {
        CSVWriter.Write(targetPath+"/"+currentCompany+".txt", extractedContentCurrentCompany);
    }

    // this method is almost the same as ExtractContentFromCompanyProfile except it accept a dictionary parameter and pass into hitchecker
    public static ArrayList<String> ExtractContentFromCompanyProfile(int yearFrom, int yearTo, ArrayList<ArrayList<String>> keywordDic, ArrayList<String> excludeWords, String flag, int numSentence, MainFrame parentMainFrame, String targetPath) 
    {
        ArrayList<String> hits=new ArrayList();
        DBConnector dbConnector = new DBConnector();
        String sql = "SELECT CONVERT(content USING utf8) AS content,link,company_name FROM auto_test_link_content";
        ResultSet resultSet = dbConnector.executeQuerry(sql);
        try 
        {
            //if resultset contains nothing, return a empty arrayList
            int resultSetSize=0;
            while(resultSet.next())
            {
                resultSetSize++;
            }
            if(resultSetSize==0)
            {
                return hits;
            }
            //after count, put the cursor back before 1st
            resultSet.beforeFirst();
            
            //get the 1st company name and record it as currentCampany
            resultSet.next();
            String currentCompany = resultSet.getString("company_name").replace(",", " ");
            String extractedContentCurrentCompany=new String("==========\r\n");
            resultSet.beforeFirst();
            while(resultSet.next()) 
            {
                String content = resultSet.getString("content");
                String campany = resultSet.getString("company_name").replace(",", " ");
                String link = resultSet.getString("link");
                
                String result = HitChecker.check(content, keywordDic, excludeWords, flag, numSentence);
                if(result!=null&&result.length()!=0)//paragraph hits, return a string
                {
                    System.out.println("++++++++++++++++++hit result+++++++++++++++++++++");
                    System.out.println(result);
                    parentMainFrame.insertTextPane("hit found in Campany:"+campany+"\r\n", Color.black); 
                    //write to hits 
                    hits.add(campany+","+link+","+result.replace(",", " ")+",\n");
                    //record the extracted content to extractedContentCurrentCompany, out put if it comes to next company
                    if(campany.equals(currentCompany))//compare company names
                    {
                        extractedContentCurrentCompany+=link+"\r\n"+result+"\r\n"+"==========\r\n";
                    }
                    else 
                    {
                        if(extractedContentCurrentCompany.length()>"==========\r\n".length())
                        {
                            outputExtractedContent4CurrentCompany(currentCompany,extractedContentCurrentCompany,targetPath);
                            extractedContentCurrentCompany=new String("==========\r\n");
                        }
                        extractedContentCurrentCompany+=link+"\r\n"+result+"\r\n"+"==========\r\n";
                        currentCompany=campany;
                    }
                }
                else//paragraph not hits, return null
                {
                    //currently do nothing
                }
            }
            //after exit while, if there is some content in extractedContentCurrentCompany, it should also be output
            if(extractedContentCurrentCompany.length()>"==========\r\n".length())
            {
                outputExtractedContent4CurrentCompany(currentCompany,extractedContentCurrentCompany,targetPath);
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(OrignalTextExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hits;
        
    }
    // use unicode number to check the eliminate the special chars! Really good idea jajaja
    public static String eliminateSpecialCharByUnicode(String input) 
    {
        String result=new String();
 
        // 一次读一个字符
        int tempchar;
        for(int i=0;i<input.length();i++)
        {
            tempchar=input.charAt(i);
            if ((char) tempchar<=127) 
            {
                result+=(char)tempchar;
            }
            else
            {
                result+=" ";
            }
         }
           
        return result.replace("..", ".");
       
      }
    
}
