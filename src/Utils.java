import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.*;

/**
 * Created by place on 22.06.2017.
 */
public class Utils {

    /**
     * Open browser with given url
     * @param link
     */
    public static void openBrowser(String link){
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }


    public static  boolean checkInternetConnection(){
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            conn.getContent();
        } catch (MalformedURLException e) {
            return false;
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Craete JTable cell formatted link
     * @param rawlink
     * @return
     */
    public static String createLink(String rawlink) {

        return rawlink != null ? "<html><a href="  + rawlink + ">Link</a></html>" : "";
    }


    public static String createBoldString(String text) {

        return text != null ? "<html><strong>"  + text + "</strong></html>" : "";
    }


    public static DefaultTableModel createNonEditableTableModel(String [] columns){
        return new DefaultTableModel(columns, 0){
            public boolean isCellEditable(int row, int column){ return false; }
        };
    }
}
