/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author lance
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

public class DocColorTest extends JFrame {

    JTextPane textPane = new JTextPane();
    JPanel contPane = new JPanel();

    public DocColorTest() {
        super("DocColorTest");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((d.width - 300) / 2, (d.height - 200) / 2, 300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contPane.setLayout(new BorderLayout());
        contPane.add(new JScrollPane(textPane), "Center");

        insertDocument("您可以对问题设定匿名", Color.RED);
        insertDocument("，但您需要付出积分10分。 ", Color.BLACK);

        setContentPane(contPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        new DocColorTest();
    }

    public void insertDocument(String text, Color textColor)//根据传入的颜色及文字，将文字插入文本域 
    {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, textColor);//设置文字颜色 
        StyleConstants.setFontSize(set, 12);//设置字体大小 
        Document doc = textPane.getStyledDocument();
        try 
        {
            doc.insertString(doc.getLength(), text, set);//插入文字 
        } catch (BadLocationException e) {
        }
    }
}

