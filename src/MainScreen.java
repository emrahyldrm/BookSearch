import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by place on 20.06.2017.
 */
public class MainScreen {
    private String target_name = null;
    private String target_author = null;
    private JPanel panelMain;
    private JButton btnSearch;
    private JTextField bookNameField;
    private JTextField authorField;
    private JTable resultTable;
    private JLabel tableTitle;
    private JCheckBox combineCheckBox;
    private JScrollPane tablePane;
    private JPanel boxPanel;
    private String [] columns = {"Store", "Name", "Author", "Price", "Link"};
    private DefaultTableModel tableModel = null;
    private ActionListener actionListener = null;
    public MainScreen() {

        if(! checkInternetConnection())
        {
            JOptionPane.showMessageDialog(null, "Check your internet connection");
            System.exit(404);

        }
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        };

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println(resultTable.getSelectedRow() + " clicked");
                    Document link = Jsoup.parse((String)resultTable.getValueAt(resultTable.getSelectedRow(), 4));
                    openBrowser(link.select("a").first().attr("href"));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        //for setting header
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        resultTable.setModel(model);
        resultTable.setRowSelectionAllowed(true);
        resultTable.addMouseListener(mouseListener);
        btnSearch.addActionListener(actionListener);
        bookNameField.addActionListener(actionListener);
        authorField.addActionListener(actionListener);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        btnSearch.setBackground(new Color(195, 189, 180));
        resultTable.setBackground(new Color(195, 189, 180));
    }


    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void search() {
        target_name = bookNameField.getText().equals("") ? "deneme" : bookNameField.getText().trim();
        target_author = authorField.getText().equals("") ? "deneme" : authorField.getText().trim();
        tableTitle.setText("Searching for book named \"" + target_name + "\"");

        new Thread(() -> {
            try {
                ArrayList<Product> p = OnlineSearcher.run(target_name);
                updateTable(p);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }


    private void updateTable(ArrayList<Product> ps) {
        tableModel = createNonEditableTableModel();

        for(Product p : ps){
            if (!combineCheckBox.isSelected() && p.getName().contains(target_name))
                tableModel.addRow(new Object[]{p.getStore(), p.getName(), p.getAuthor(), p.getPrice() + " TL", createLink(p.getLink())});
            else if(combineCheckBox.isSelected() && p.getName().contains(target_name) && p.getAuthor().contains(target_author))
                tableModel.addRow(new Object[]{p.getStore(), p.getName(), p.getAuthor(), p.getPrice() + " TL",createLink(p.getLink())});
        }

        resultTable.setModel(tableModel);
        if (tableModel.getRowCount() == 0)
            tableTitle.setText("No results found..");
    }


    private String createLink(String rawlink) {

        return rawlink != null ? "<html><a href="  + rawlink + ">Link</a></html>" : "";
    }

    private DefaultTableModel createNonEditableTableModel(){
        return new DefaultTableModel(columns, 0){
            public boolean isCellEditable(int row, int column){ return false; }
        };
    }

    private void openBrowser(String link){
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    public boolean checkInternetConnection(){
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

    public JPanel getPanelMain(){
        return panelMain;
    }
}
