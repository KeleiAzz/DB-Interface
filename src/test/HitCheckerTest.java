/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import model.HitChecker;

/**
 *
 * @author lance
 */
public class HitCheckerTest 
{
    public static void main(String[] args)
    {
        String content = read("D:\\user\\lance\\Desktop\\in.txt");
        //System.out.println(content);
        
        //HitChecker.splitSentence(content);
        
        //HitChecker.printSentences();
        
        //HitChecker.preProcess();
        
        //HitChecker.printSentences();
        
        ArrayList<String> keyWords = new ArrayList();
        keyWords.add("dog"); 
        //keyWords.add("jjaa");
        //keyWords.add("beaR"); 
        ArrayList<String> orKeywords = new ArrayList();
        orKeywords.add("pandAA");
        orKeywords.add("dog");
        ArrayList<String> excludeWords = new ArrayList();
        excludeWords.add("cat");
        excludeWords.add("kitten");
        String flag="NUMSEN";
        int numSentence = 4;
        String result = HitChecker.check(content, keyWords, orKeywords, excludeWords, flag, numSentence);
        System.out.println("++++++++++++++++++++++++++++++++");
        System.out.println(result);
    }
    
    public static String read(String fileName) 
    {
        File f = new File(fileName);
        if (!f.exists()) 
        {
            return "File not found!";
        }
        FileInputStream fs;
        String result = null;
        try {
            fs = new FileInputStream(f);
            byte[] b = new byte[fs.available()];
            fs.read(b);
            fs.close();
            result = new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
