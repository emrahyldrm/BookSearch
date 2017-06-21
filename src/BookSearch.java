import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by place on 22.06.2017.
 */
public class BookSearch {

    public static void main(String [] args) throws IOException
    {

        JFrame jfm = new JFrame("MainScreen");
        jfm.setTitle("Book Search");
        jfm.setContentPane(new MainScreen().getPanelMain());
        jfm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfm.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        jfm.setSize(new Dimension((int)(width/2), (int)(height - height/10)));
        jfm.setResizable(false);
        jfm.setVisible(true);


    }
}
