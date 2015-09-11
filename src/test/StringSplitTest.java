/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author lance
 */
public class StringSplitTest 
{
    public static void main(String args[])
    {
        String content = "Blue Horseshoe will complete this project using its proven methodology! and project experience? that has delivered successful upgrade after successful upgrade for Lawson clients worldwide.  Blue Horseshoe is a Certified Infor-Lawson Service Partner, supporting all of the Lawson Suites (Financial, Procurement, HR/Payroll), Lawson Business Intelligence, Budgeting and Planning, Absence Management, Grant Management, Process Flow and Mobile Supply Chain Management.";
        String[] tempSentence = content.split("\\. |\\. |\r\n|\r|\n|! |\\? ");
        System.out.println(tempSentence.length);
        for(int i=0;i<tempSentence.length;i++)
        {
            System.out.println(tempSentence[i]+"|||");
        }
    }
}
