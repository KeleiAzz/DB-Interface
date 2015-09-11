/*
 * in current version the content in DB are plain string with\ paragraph (\r\n), in uper and smaller cases, with punctuations.
 * This code provide hit sentence extraction, sentence set extraction or who paragraph extraction.
 * Paragraph extraction is provided in this version.
 * In Preprocess I turn whatever not letter (upper or smaller) or number into space(" ") for both keyword and original content.
 * Consider content "hit" if a sentence contains keywords or OrKeywords, but not Exclude keywords.
 * 
 * Algorithm: check sentence by sentence. Record start position.
 */
package model;

import dao.CSVWriter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import ui.MainFrame;

/**
 *
 * @author lance
 */
public class HitChecker 
{
    private static ArrayList<Integer> index = new ArrayList();
    private static ArrayList<ArrayList<String>> keywordDic = new ArrayList();
    private static ArrayList<String> keyWords = new ArrayList();
    private static ArrayList<String> orKeywords= new ArrayList();
    private static ArrayList<String> excludeKeywords = new ArrayList();
    private static ArrayList<String> originalSentenses = new ArrayList();
    private static ArrayList<String> processedSentenses = new ArrayList();
    private static String processedContent=new String("");
    // PLZ do not put space in the following list.
    private static String[][] protectedWordsMappingList = {
        {"Inc.","PROTECTEDINC"},
        {"Corp.", "PROTECTEDCORP"},
        {"Dec.","PROTECTEDDEC"},
        {"A. O. Smith","PROTECTEDAOSMITH"},
        {"Nov.","PROTECTEDNOV"},
        {"Oct.","PROTECTEDOCT"},
        {"Sept.","PROTECTEDSEPT"},
        {"Sep.","PROTECTEDSEP"},
        {"Aug.","PROTECTEDAUG"},
        {"Jan.","PROTECTEDJAN"},
        {"Feb.","PROTECTEDFEB"},
        {"Ltd.","PROTECTEDLTD"},
        {"Co.","PROTECTEDCO"},
        {"St. Vincent Health","PROTECTEDSTVINCENTHEALTH"},
        {"vs.","PROTECTEDVS"},
        {"Mr.","PROTECTEDMR"},
        {"Ms.","PROTECTEDMS"},
        {"U. S.","PROTECTEDUSPACES"},
        {"U.S.","PROTECTEDUS"},
        {"St. Simons Island", "PROTECTEDSTSIMONSISLAND"}
    };
    
    public static String check(String content, ArrayList<String> keyWords, ArrayList<String> orKeywords, ArrayList<String> excludeKeywords, String flag, int numSentence) 
    {
        HitChecker.index.clear();
        HitChecker.keyWords.clear();//processed
        HitChecker.orKeywords.clear();//processed
        HitChecker.excludeKeywords.clear();//processed
        HitChecker.originalSentenses.clear();
        HitChecker.processedSentenses.clear();
        HitChecker.processedContent=new String("");
           
        String result;
        // remove the lines which are too short or too long. This will help remove the header and footer
        content = removeShortAndLongLines(content);
        
        //split content into a set of sentence (this method only fills originalSentenses Array) This is split-merge approach.
        //splitSentence(content);
        // a new apptoach for splitting sentence: merge-split
        splitSentenceMerge_Split(content);
        //out put all the split sentences for a single document into specified folder, only used in step-by-step debugging.
        //outputSplitSentence();
        getQuottedContent();
        removeShortAndLongSentencs();
        //copy the keywords, NOT assigning
        HitChecker.keyWords=new ArrayList<String>(keyWords);
        HitChecker.orKeywords=new ArrayList<String>(orKeywords);
        HitChecker.excludeKeywords=new ArrayList<String>(excludeKeywords);
        //flip upper letters, keep only letters and numbers
        preProcess();
        //find sentences that hit the keyword
        findHits(flag, numSentence);
        result = combineHitSentences().replaceAll("\\pC", "");
        return result;
    }

    //remove all the character with are not letter or number into space (total length wont change) for both keywords and splitted sentence set
    //Then convert all the upper cases into small cases for keywords and sentence set
    public static void preProcess() 
    {
        for(int i=0;i<originalSentenses.size();i++)//the # of originalSentenses equals the # of processedSentenses
        {
            processedSentenses.add(" "+flipUpperLetterAndReplaceSpecials(originalSentenses.get(i)));//add a space to the begining of the sentence to faciliate searching. A space at the end is not necessary since "." is replaced to space
        }
        for(int i=0;i<keyWords.size();i++)
        {
            keyWords.set(i," "+flipUpperLetterAndReplaceSpecials(keyWords.get(i)).trim()+" ");
        }
        for(int i=0;i<orKeywords.size();i++)
        {
            orKeywords.set(i," "+flipUpperLetterAndReplaceSpecials(orKeywords.get(i)).trim()+" ");
        }
        for(int i=0;i<excludeKeywords.size();i++)
        {
            excludeKeywords.set(i," "+flipUpperLetterAndReplaceSpecials(excludeKeywords.get(i)).trim()+" ");
        }
        
    }

    //find hits and put the hit sentences into results;
    private static void findHits(String flag, int numSentence) 
    {
        //combine processedSentenses to make processedContent
        for(int i=0;i<processedSentenses.size();i++)
        {
            HitChecker.processedContent=HitChecker.processedContent+HitChecker.processedSentenses.get(i);
        }
        
        //check if this document (whole content) hit the keywords/orkeywords
        //the document must hit all the terms in keyword or orkeywords to be considered as "hit"
        boolean hitKeywordsFlag=true;
        boolean hitOrKeywordsFlag=true;
        if(HitChecker.keyWords==null||HitChecker.keyWords.size()==0)
        {
            hitKeywordsFlag=false;
        }
        if(HitChecker.orKeywords==null||HitChecker.orKeywords.size()==0)
        {
            hitOrKeywordsFlag=false;
        }
            
        //check if the preprocessed content contains the keywords/orkeywords
        for(int i=0;i<HitChecker.keyWords.size();i++)
        {
            if(!HitChecker.processedContent.contains(HitChecker.keyWords.get(i)))
            {
                hitKeywordsFlag=false;
                break;
            }
        }
        for(int i=0;i<HitChecker.orKeywords.size();i++)
        {
            if(!HitChecker.processedContent.contains(HitChecker.orKeywords.get(i)))
            {
                hitOrKeywordsFlag=false;
                break;
            }
        }
        //if NO, index should still be empty, and return.
        if(hitOrKeywordsFlag==false&&hitKeywordsFlag==false)//both keywords and orkeyword does not hit the content. leave the index array empty, return.
        {
            return;
        }

        //if YES, find hit sentences from processedSentenses
        if(hitKeywordsFlag==true)//find if any sentence contain ANY keywords, record sentence index
        {
            fillIndexByHitSentences(HitChecker.keyWords);
        }
        if(hitOrKeywordsFlag==true)//find if any sentence contain ANY OrWords, record sentence index
        {
            fillIndexByHitSentences(HitChecker.orKeywords);
        }
        
        //remove all the sentence in index which contains ANY excludeKeyWords,
        deleteIndexByExcludeSentences();
        
        //4th round, add additional sentence based on the radio button that the user select (WHOLE:all sentence, NUNSEN: specified # of sentence, SINGLE: the hit sentences)
        if(flag.equals("WHOLE"))//add all sentence # into index
        {
            HitChecker.index.clear();
            for(int i=0;i<HitChecker.processedSentenses.size();i++)
            {
                HitChecker.index.add(i);
            }
        }
        else if(flag.equals("NUNSEN"))// if one sentence hits the keyword list, also add its neighbour sentences into index (numSentence)
        {
            int tempIndexSize = HitChecker.index.size();
            for(int i=0;i<tempIndexSize;i++)
            {
                for(int j=index.get(i)-numSentence;j<=index.get(i)+numSentence;j++)
                {
                    if(j>=0&&j<HitChecker.processedSentenses.size()&&!HitChecker.index.contains(j))
                    {
                        HitChecker.index.add(j);
                    }
                }
            }
            Collections.sort(HitChecker.index);//this works in sorting integers！
        }
        else//SINGLE, do nothing because the sentence id are in index now
        {
            Collections.sort(HitChecker.index);
        }
        
        for(int i=0;i<index.size();i++)
        {
            System.out.println(index.get(i));
        }
              
    }

    public static void splitSentenceMerge_Split(String content)
    {
        if(content==null||content.trim().length()==0)
        {
            return;
        }
        //replace all \r \n with space, no line break anymore
        content = content.replace("\r", " ");
        content = content.replace("\n", " ");
        
        //replace the protected words and phrases.
        content = protectSpecialWords(content);
        //split the content again
        String[] tempSentence = content.split("\\. |\\. |\r\n|\r|\n|! |\\? ");
        //add a "." on each string's end
        for(int i=0;i<tempSentence.length;i++)
        {   
            tempSentence[i]=tempSentence[i].trim()+".";
        }
        
        // for each sentence in tempSentencs, replace the protected words and phrases back
        for(int i=0;i<tempSentence.length;i++)
        {   
            tempSentence[i] = releaseProtection(tempSentence[i]);
        }
        // this approach saves the trouble for merging the lines and finding the correct end of a sentence. Thanks to Yun's suggestion.
        //add to HitChecker.originalSentenses
        for(int i=0;i<tempSentence.length;i++)
        {
            HitChecker.originalSentenses.add(tempSentence[i]);
        }
    }
    
    //split the processed original 
    public static void splitSentence(String content) 
    {
        if(content==null||content.trim().length()==0)
        {
            return;
        }
        String[] tempSentence = content.split("\\. |\\. |\r\n|\r|\n|! |\\? ");
        //add a "." on each string's end
        for(int i=0;i<tempSentence.length;i++)
        {   
            tempSentence[i]=tempSentence[i].trim()+".";
        }
        //combine the false sentences together
        for(int i=0;i<tempSentence.length;i++)
        {
            if(i==tempSentence.length-1||(isEndOfSentense(tempSentence[i])==true && isStartOfSentense(tempSentence[i+1])==true))//current line in tempSentence[] is a full sentence
            {
                HitChecker.originalSentenses.add(tempSentence[i]);
            }
            else//current line is not a full sentence, find the end of the correct sentence.
            {
                int formerIndex = i;
                for(i++;i<tempSentence.length;i++)
                {
                    tempSentence[formerIndex]=tempSentence[formerIndex].substring(0,tempSentence[formerIndex].length()-1)+" "+tempSentence[i];
                    if(i==tempSentence.length-1|| (isEndOfSentense(tempSentence[i+1])==true && isStartOfSentense(tempSentence[i+1]) )    )
                    {
                        break;
                    }
                }
                HitChecker.originalSentenses.add(tempSentence[formerIndex]);
            }
            
        }
    }
    
    private static boolean isStartOfSentense(String input)
    {
        int firstLetter = 0;
        boolean containLetter = false;
        for(;firstLetter<input.length();firstLetter++)
        {
            if(input.charAt(firstLetter)>='a'&&input.charAt(firstLetter)<='z')
            {
                containLetter=true;
                break;
            }
            if(input.charAt(firstLetter)>='A'&&input.charAt(firstLetter)<='Z')
            {
                containLetter=true;
                break;
            }
        }
        if(containLetter==false)//If current sentense does not EVEN contains a letter, return false
        {
            return false;
        }

        if(firstLetter+1<input.length())
        {
            if(Character.isUpperCase(input.charAt(firstLetter))==true && Character.isUpperCase(input.charAt(firstLetter+1))==false)//
            {
                return true;
            }     
        }
        return false;
    }
    
    private static boolean isEndOfSentense(String input)
    {
        String trimmedInput = input.trim();
        if(trimmedInput.charAt(trimmedInput.length()-1)==',')//if the sentense ends with a comma, it is not an end of the sentense
        {return false;}
        
        //true by default
        return true;
    }

    //combine all the sentence into result string (for return)
    private static String combineHitSentences() 
    {
        String result = new String("");
         for(int i=0;i<HitChecker.index.size();i++)
         {
             result += HitChecker.originalSentenses.get(HitChecker.index.get(i))+"!@#$%^";
         }
         return result;
    }

    private static String flipUpperLetterAndReplaceSpecials(String input)
    {
       StringBuilder sb = new StringBuilder();
       for (int i = 0; i < input.length(); i++) 
       {
           if (Character.isLetterOrDigit(input.charAt(i)))
           {
              sb.append(input.charAt(i));
           }
           else
           {
               sb.append(" ");
           }
       }
       String result = sb.toString();
       return result.toLowerCase();
    }
    
    private static void fillIndexByHitSentences(ArrayList<String> termList) 
    {
        boolean sentenceHitFlag=true;
        for(int i=0;i<HitChecker.processedSentenses.size();i++)
        {
            sentenceHitFlag=sentenceHitWordList(HitChecker.processedSentenses.get(i),termList);
            if(sentenceHitFlag==true)//if hits, record the sentence # in index
            {
                if(!HitChecker.index.contains(new Integer(i)))
                {
                    HitChecker.index.add(i);
                }
            }
        }
    }

    private static void deleteIndexByExcludeSentences() 
    {
        for(int i=0;i<HitChecker.excludeKeywords.size();i++)
        {
            for(int j=0;j<HitChecker.index.size();j++)
            {
                if(HitChecker.processedSentenses.get(HitChecker.index.get(j)).contains(HitChecker.excludeKeywords.get(i)))
                {
                    HitChecker.index.remove(j);
                    j--;
                }
            }
        }
    }

    //a different check method which support keywordDic
    static String check(String content, ArrayList<ArrayList<String>> keywordDic, ArrayList<String> excludeWords, String flag, int numSentence) 
    {
        HitChecker.index.clear();
        HitChecker.keyWords.clear();//processed
        HitChecker.orKeywords.clear();//processed
        HitChecker.excludeKeywords.clear();//processed
        HitChecker.originalSentenses.clear();
        HitChecker.processedSentenses.clear();
        HitChecker.processedContent=new String("");
           
        String result;
        
        //split content into a set of sentences
        splitSentence(content);
        getQuottedContent();
        removeShortAndLongSentencs();
        //copy the keywords dictionary, NOT assigning
        HitChecker.keywordDic=new ArrayList<ArrayList<String>>(keywordDic);
        //flip upper letters, keep only letters and numbers
        preProcess4Dic();
        //find sentences that hit the keyword
        findHits4Dic(flag, numSentence);
        result = combineHitSentences();
        return result;        
    }
    //remove all the character with are not letter or number into space (total length wont change) for both keywords dictionary and splitted sentence set
    //Then convert all the upper cases into small cases for keywords dictionary and sentence set
    public static void preProcess4Dic() 
    {
        for(int i=0;i<originalSentenses.size();i++)//the # of originalSentenses equals the # of processedSentenses
        {
            processedSentenses.add(" "+flipUpperLetterAndReplaceSpecials(originalSentenses.get(i)));//add a space to the begining of the sentence to faciliate searching. A space at the end is not necessary since "." is replaced to space
        }
        //create new dictionary item, do the preprocess and 
        for(int i=0;i<keywordDic.size();i++)
        {
            ArrayList tempDicItem = new ArrayList<String>();
            for(int j=0;j<keywordDic.get(i).size();j++)
            {
                tempDicItem.add(" "+flipUpperLetterAndReplaceSpecials(keywordDic.get(i).get(j)).trim()+" ");
            }
            keywordDic.set(i,tempDicItem);
        }
        for(int i=0;i<excludeKeywords.size();i++)
        {
            excludeKeywords.set(i," "+flipUpperLetterAndReplaceSpecials(excludeKeywords.get(i)).trim()+" ");
        }
        
    }
    
    //find hits and put the hit sentences into results;
    private static void findHits4Dic(String flag, int numSentence) 
    {
        //combine processedSentenses to make processedContent
        for(int i=0;i<processedSentenses.size();i++)
        {
            HitChecker.processedContent=HitChecker.processedContent+HitChecker.processedSentenses.get(i);
        }
        
        //check if this document (whole content) hit the keywords set in dictionary. A keyword set may concsist of several keywords.
        //the document must hit all the terms in keyword set to be considered as "hit"
        ArrayList<Boolean> hitKeywordsDicFlag=new ArrayList<Boolean>();
        //check if the preprocessed content contains the keywords/orkeywords
        for(int i=0;i<keywordDic.size();i++)
        {
            boolean tempFlag=true;
            for(int j=0;j<keywordDic.get(i).size();j++)
            {
                if(!HitChecker.processedContent.contains(HitChecker.keywordDic.get(i).get(j)))
                {
                    tempFlag=false;
                    break;
                }
            }
            hitKeywordsDicFlag.add(tempFlag);
        }
        
        // check in hitKeywordsDicFlag if there is any true?
        boolean containsTrue = false;
        for(int i=0;i<hitKeywordsDicFlag.size();i++)
        {
            if(hitKeywordsDicFlag.get(i)==true)
            {
                containsTrue=true;
                break;
            }
        }
        
        //if NO, index should still be empty, and return.
        if(containsTrue==false)
        {
            return;
        }

        //if YES, find hit sentences from processedSentenses
        if(containsTrue==true)//find if any sentence contain ANY keywords, record sentence index
        {
            fillIndexByHitSentences4Dic();
        }
       
        
        //remove all the sentence in index which contains ANY excludeKeyWords,
        deleteIndexByExcludeSentences();
        
        //4th round, add additional sentence based on the radio button that the user select (WHOLE:all sentence, NUNSEN: specified # of sentence, SINGLE: the hit sentences)
        if(flag.equals("WHOLE"))//add all sentence # into index
        {
            HitChecker.index.clear();
            for(int i=0;i<HitChecker.processedSentenses.size();i++)
            {
                HitChecker.index.add(i);
            }
        }
        else if(flag.equals("NUNSEN"))// if one sentence hits the keyword list, also add its neighbour sentences into index (numSentence)
        {
            int tempIndexSize = HitChecker.index.size();
            for(int i=0;i<tempIndexSize;i++)
            {
                for(int j=index.get(i)-numSentence;j<=index.get(i)+numSentence;j++)
                {
                    if(j>=0&&j<HitChecker.processedSentenses.size()&&!HitChecker.index.contains(j))
                    {
                        HitChecker.index.add(j);
                    }
                }
            }
            Collections.sort(HitChecker.index);//this works in sorting integers！
        }
        else//SINGLE, do nothing because the sentence id are in index now
        {
            Collections.sort(HitChecker.index);
        } 
        
        for(int i=0;i<index.size();i++)
        {
            System.out.println(index.get(i));
        }
    }    
    
    //fill index with hitted sentences #
    private static void fillIndexByHitSentences4Dic() 
    {
        for(int i=0;i<HitChecker.processedSentenses.size();i++)
        {
            boolean sentenceHitFlag=false;
            for(int j=0;j<HitChecker.keywordDic.size();j++)
            {
                if(sentenceHitWordList(processedSentenses.get(i),keywordDic.get(j))==true)
                {
                    sentenceHitFlag=true;//if current sentence contain any word- hit!
                    break;
                }
            }
            if(sentenceHitFlag==true)//if hits, record the sentence # in index
            {
                if(!HitChecker.index.contains(new Integer(i)))
                {
                    HitChecker.index.add(i);
                }
            }
        }
    }    

    private static boolean sentenceHitWordList(String sentence, ArrayList<String> keywordSet) 
    {
        boolean hitKeywordsFlag=true;
         for(int i=0;i<keywordSet.size();i++)
        {
            if(!sentence.contains(keywordSet.get(i)))
            {
                hitKeywordsFlag=false;
                break;
            }
        }
         return hitKeywordsFlag;
    }

    private static void getQuottedContent() 
    {
        ArrayList indexOfQuotes= new ArrayList<Integer>();
        for(int i=0;i<originalSentenses.size();i++)
        {
            if(!(originalSentenses.get(i).indexOf("\"")==-1&&originalSentenses.get(i).indexOf("“")==-1&&originalSentenses.get(i).indexOf("”")==-1))//if the current string contains at least 1 double quote
            {
                for(int j=0;j<originalSentenses.get(i).length();j++)
                {
                    if(originalSentenses.get(i).charAt(j)=='\"'||originalSentenses.get(i).charAt(j)=='”'||originalSentenses.get(i).charAt(j)=='“')//record all the quotes
                    {
                        indexOfQuotes.add(j);
                    }
                }
            
                //remove the sentense if it contains odd number of quotes
                if(indexOfQuotes.size()%2==1)
                {
                    originalSentenses.set(i, "");
                    indexOfQuotes.clear();
                    continue;
                }
                //go through the index and figure our the longest quoted string
                int largestLength=0;
                int recorderOfStartQuoteIndex=-1;
                for(int j =0;j<indexOfQuotes.size()-1;j++)
                {
                    if(((Integer)indexOfQuotes.get(j+1)-(Integer)indexOfQuotes.get(j))>=largestLength)
                    {
                        largestLength=((Integer)indexOfQuotes.get(j+1)-(Integer)indexOfQuotes.get(j));
                        recorderOfStartQuoteIndex=j;
                    }
                }
                //put the longgest quoted sentense into the originalSentenses arraylist instead of the original sentense
                int start = (int) indexOfQuotes.get(recorderOfStartQuoteIndex);
                int end = (int) indexOfQuotes.get(recorderOfStartQuoteIndex+1);
                String tempString;
                if(start+1<end-1)
                {
                    tempString = originalSentenses.get(i).substring(start+1, end-1);
                }
                else
                {
                    tempString="";
                }
                if(tempString.length()!=0&&tempString.charAt(tempString.length()-1)!='.')
                {
                    if(tempString.charAt(tempString.length()-1)>='a'&&tempString.charAt(tempString.length()-1)<='z')//end-1 is a char
                    {
                        tempString +=".";
                    }
                    else if(tempString.charAt(tempString.length()-1)>='A'&&tempString.charAt(tempString.length()-1)<='Z')
                    {
                        tempString +=".";
                    }
                    else
                    {
                        tempString=tempString.substring(0, tempString.length()-1)+".";
                    }
                }
                originalSentenses.set(i, tempString);
                //clear the memory
                indexOfQuotes.clear();
            }
        }
        
    }
    
    //before splitting sentence, remove the lines on original aritile who are too long (>=43) or too short (<=4)
    private static String removeShortAndLongLines(String content) 
    {
        if (content==null||content.length()==0)
        {
            return "";
        }

        String[] tempSentence = content.split("\r\n|\r|\n");

        String merged = new String();
        String temp = new String();
        int tempSize=0;
        for(int i=0;i<tempSentence.length;i++)
        {
            temp = tempSentence[i];
            tempSize = temp.length();
            for(;;)//remove any 2 continuous spases.
            {
                temp = temp.replace("  "," ");
                if(temp.length()==tempSize)
                {break;}
                else
                {tempSize=temp.length();}
            }
             String[] splitedWords = temp.split(" ");
            //if(splitedWords.length<=4||splitedWords.length>=43)//if a sentense is too short or too long, remove this sentense.
             if(splitedWords.length<=4)
            {
                //throw current line away
            }
            else
            {
                merged = merged+temp+" ";
            }
        }
        return merged;
    }

    //after splitting sentence, remove the ones who are too long (>=43) or too short (<=4)
    private static void removeShortAndLongSentencs() 
    {
        String temp = new String();
        int tempSize=0;
        for(int i=0;i<originalSentenses.size();i++)
        {
            temp=originalSentenses.get(i);
            tempSize = temp.length();
            for(;;)//remove any 2 continuous spases.
            {
                temp = temp.replace("  "," ");
                if(temp.length()==tempSize)
                {break;}
                else
                {tempSize=temp.length();}
            }
            String[] splitedWords = temp.split(" ");
            if(splitedWords.length<=4||splitedWords.length>=43)//if a sentense is too short or too long, remove this sentense.
            {
                originalSentenses.set(i, "");
            }
        }
    }

    //out put all the split sentences for a single document into specified folder, only used in step-by-step debugging.
    private static void outputSplitSentence() 
    {
        String output = new String();
        for(int i=0;i<HitChecker.originalSentenses.size();i++)
        {
            output += HitChecker.originalSentenses.get(i)+"|||\r\n";
        }
        CSVWriter.Write("C:\\Users\\SCRC\\Desktop\\Sept 19 test lenovo_Yang\\splitsentence.txt", output);
    }

    // input is a long sting without \r\n,
    //replace all the protected words with their subsitutes.
    private static String protectSpecialWords(String content) 
    {
        String result = content;
        for(int i=0;i< HitChecker.protectedWordsMappingList.length;i++)
        {
            result=result.replace(" "+protectedWordsMappingList[i][0], " "+protectedWordsMappingList[i][1]);
            result=result.replace(protectedWordsMappingList[i][0]+" ", protectedWordsMappingList[i][1]+" ");
        }
        return result;
    }

    // input is a string (a splitted sentence)
    private static String releaseProtection(String content) 
    {
        String result = content;
        for(int i=0;i< HitChecker.protectedWordsMappingList.length;i++)
        {
            result=result.replace(" "+protectedWordsMappingList[i][1], " "+protectedWordsMappingList[i][0]);
            result=result.replace(protectedWordsMappingList[i][1]+" ", protectedWordsMappingList[i][0]+" ");
        }
        return result;        
    }
}
