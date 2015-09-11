package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

public class FileFolderIterator 
{
        public FileFolderIterator() 
        {
        }
        /**
         * 读取某个文件夹下的所有文件
         */
        public static ArrayList<String> readCompanyNames(String filepath) throws FileNotFoundException, IOException 
        {
            ArrayList<String> fileNameArrayList = new ArrayList();
            try 
            {
                    File file = new File(filepath);
                    if (!file.isDirectory()) 
                    {
                        fileNameArrayList.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                    } 
                    else if (file.isDirectory()) 
                    {
                            //System.out.println("文件夹");
                            String[] filelist = file.list();
                            for (int i = 0; i < filelist.length; i++) 
                            {
                                    File readfile = new File(filepath + "\\" + filelist[i]);
                                    if (!readfile.isDirectory()) 
                                    {
                                        if(readfile.getName().lastIndexOf(".")!=-1)
                                            fileNameArrayList.add(readfile.getName().substring(0, readfile.getName().lastIndexOf(".")));
                                    } 
                                    else if (readfile.isDirectory()) 
                                    {
                                            fileNameArrayList.addAll(readCompanyNames(filepath + "\\" + filelist[i])); 
                                    }
                            }
                    }
            } 
            catch (FileNotFoundException e) 
            {
                    System.out.println("readfile Exception:" + e.getMessage());
            }
            return fileNameArrayList;
        }

        /**
         * 删除某个文件夹下的所有文件夹和文件
         */
        
        
        /*public static boolean deletefile(String delpath)
                        throws FileNotFoundException, IOException {
                try {

                        File file = new File(delpath);
                        if (!file.isDirectory()) {
                                System.out.println("1");
                                file.delete();
                        } else if (file.isDirectory()) {
                                System.out.println("2");
                                String[] filelist = file.list();
                                for (int i = 0; i < filelist.length; i++) {
                                        File delfile = new File(delpath + "\\" + filelist[i]);
                                        if (!delfile.isDirectory()) {
                                                System.out.println("path=" + delfile.getPath());
                                                System.out.println("absolutepath="
                                                                + delfile.getAbsolutePath());
                                                System.out.println("name=" + delfile.getName());
                                                delfile.delete();
                                                System.out.println("删除文件成功");
                                        } else if (delfile.isDirectory()) {
                                                deletefile(delpath + "\\" + filelist[i]);
                                        }
                                }
                                file.delete();

                        }

                } catch (FileNotFoundException e) {
                        System.out.println("deletefile()   Exception:" + e.getMessage());
                }
                return true;
        }*/
        
        public static void main(String[] args) 
        {
                try 
                {
                        readCompanyNames("d:/company list");
                        // deletefile("D:/file");
                } 
                catch (FileNotFoundException ex) 
                {
                    ex.printStackTrace(); 
                }
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
                System.out.println("ok");
        }
}
