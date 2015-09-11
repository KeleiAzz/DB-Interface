/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author lance
 */

import java.io.*;
public class FileAccessor {
    
    /*
     * 删除文件
     */
    public static boolean delete(String fileName){
        boolean result = false;
        File f = new File(fileName);
        if (f.exists()){
            try{
                result = f.delete();
            }
            catch(Exception e){
                e.printStackTrace();
            }           
        }
        else{
            result = true;
        }
        return result;
    }
    
    /*
     * 读取文件
     */
    public static String read(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
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

    /*
     *写文件
     */
    public static boolean write(String fileName, String fileContent) {
        boolean result = false;
        File f = new File(fileName);
        try {
            FileOutputStream fs = new FileOutputStream(f);
            byte[] b = fileContent.getBytes();
            fs.write(b);
            fs.flush();
            fs.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * 追加内容到文件
     */
    public static boolean append(String fileName, String fileContent) {
        boolean result = false;
        File f = new File(fileName);
        try {
            if (f.exists()) {
                FileInputStream fsIn = new FileInputStream(f);
                byte[] bIn = new byte[fsIn.available()];
                fsIn.read(bIn);
                String oldFileContent = new String(bIn);
                //System.out.println("旧内容:" + oldFileContent);
                fsIn.close();
                if (!oldFileContent.equalsIgnoreCase("")) {
                    fileContent = oldFileContent + "\r\n" + fileContent;
                    //System.out.println("新内容:" + fileContent);
                }
            }

            FileOutputStream fs = new FileOutputStream(f);
            byte[] b = fileContent.getBytes();
            fs.write(b);
            fs.flush();
            fs.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}