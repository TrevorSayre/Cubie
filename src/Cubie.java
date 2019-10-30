import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;

// Rubik's Cube simulator

public class Cubie extends JApplet
{
   static CubieWindow mainPanel;
   public static Settings settings = new Settings();
   
   public void init() {
      // the look&feel must be set before any swing objects are made.
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) { }
      
      mainPanel = new CubieWindow();
      getContentPane().add(mainPanel);
      mainPanel.init();
   }
   public String getAppletInfo() {
      return "Cubie, by Jaap Scherphuis";
   }
   public void stop(){
      mainPanel.stop();
   }
   public void destroy(){
      mainPanel.destroy();
   }

   
   private static JFrame frame;
   public static void main(String[] args) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) { }

      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      mainPanel = new CubieWindow();
      mainPanel.init();
      frame.getContentPane().add(mainPanel);

      frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
             mainPanel.destroy();
          }
      });

      frame.pack();
      frame.setVisible(true);
      frame.setResizable(false);
   }
}
