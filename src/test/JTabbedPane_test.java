/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author lance
 */
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class JTabbedPane_test extends JFrame{
    
    public JTabbedPane_test(){
        JTabbedPane pane = new JTabbedPane();
        JPanel panel = new JPanel();
        JLabel jLabel = new JLabel("静态文本");
        final JTextField textField = new JTextField("文本输入框");
        JButton button = new JButton("按钮");

panel.add(jLabel);
        panel.add(textField);
        panel.add(button);
        
        pane.add(panel);
        getContentPane().add(pane);
        
        setSize(400,300);
        setLocationRelativeTo(null); 
        setVisible(true);
    }

public static void main(String[] args){
        new JTabbedPane_test();
    }
}
