/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lance
 */
public class unicodeTest 
{
    public static void main(String args[])
    {
        String content = "Throughout the 70’s and 80’s, Business-Software com/ERPSoftware © 2011,   Business-Software com. .";
        
        System.out.println(content.replaceAll("\\pP", ""));
        System.out.println(content.replaceAll("\\pM", "").replaceAll("\\pS", ""));
        
        String subString = content.substring(17, 18);
        System.out.println(subString);
        
       String s = "我喜欢，￥。。。..&@,Java";
		char[] chars = s.toCharArray();
		for(int i = 0; i < chars.length; i ++) {
			if((chars[i] >= 97 && chars[i] <= 122) || (chars[i] >= 65 && chars[i] <= 90)) {
				System.out.print(chars[i]);
			}
		}
        
        System.out.println("\n");        
       for(int i=97;i<=122;i++)
       {
           System.out.println((char)i);
       }
       for(int i=65;i<=90;i++)
       {
           System.out.println((char)i);
       }
       System.out.println((int)'’');
       System.out.println((int)'©');
       for(int i=65;i<=90;i++)
       {
           System.out.println((char)i);
       }
       for(int i=90;i<=255;i++)
       {
           System.out.println((char)i+ " "+i);
       }
       System.out.println(eliminateSpecialCharByUnicode("Throughout the 70’s and 80’s, Business-Software com/ERPSoftware © 2011,   Business-Software com. ."));
       
      //readFileByChars("D:\\user\\lance\\Desktop\\result\\UI-all\\American Axle & Manufacturing Holdings Inc..txt");
    }
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
           
        return result;
       
      }
}
