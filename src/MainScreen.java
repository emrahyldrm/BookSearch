import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.rmi.CORBA.Util;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
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
    private String [] columns = {"Store", "Name", "Author", "Publisher", "Type", "Price", "Link"};
    private DefaultTableModel tableModel = null;
    private ActionListener actionListener = null;
    public MainScreen() {

        if(! Utils.checkInternetConnection()) {
            JOptionPane.showMessageDialog(null, "Check your internet connection");
            System.exit(404);
        }
        actionListener = e -> search();

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println(resultTable.getSelectedRow() + " clicked");
                    Document link = Jsoup.parse((String)resultTable.getValueAt(resultTable.getSelectedRow(), resultTable.getColumnCount() - 1 ));
                    Utils.openBrowser(link.select("a").first().attr("href"));
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


    private void search() {
        target_name = bookNameField.getText().equals("") ? "deneme" : bookNameField.getText().trim();
        target_author = authorField.getText().equals("") ? "deneme" : authorField.getText().trim();
        tableTitle.setText("Searching the book named \"" + target_name + "\"");

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
        tableModel = Utils.createNonEditableTableModel(columns);

        for(Product p : ps){
            if (!combineCheckBox.isSelected() && p.getName().contains(target_name))
                tableModel.addRow(new Object[]{p.getStore(), p.getName(), p.getAuthor(), p.getPublisher(), p.getType(), Utils.createBoldString(p.getPrice() + " TL"), Utils.createLink(p.getLink())});
            else if(combineCheckBox.isSelected() && p.getName().contains(target_name) && p.getAuthor().contains(target_author))
                tableModel.addRow(new Object[]{p.getStore(), p.getName(), p.getAuthor(),p.getPublisher(), p.getType(), Utils.createBoldString(p.getPrice() + " TL"), Utils.createLink(p.getLink())});
        }

        resultTable.setModel(tableModel);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(75); // Store
        resultTable.getColumnModel().getColumn(6).setMaxWidth(50); // Link
        resultTable.getColumnModel().getColumn(5).setMaxWidth(75); // Price
        tablePane.getVerticalScrollBar().setBackground(Color.GRAY);

        if (tableModel.getRowCount() == 0)
            tableTitle.setText("No results found..");
    }


    public JPanel getPanelMain(){
        return panelMain;
    }
}

class ImageRenderer extends DefaultTableCellRenderer {
    JLabel lbl = new JLabel();

    ImageIcon icon = new ImageIcon(getClass().getResource("sample.png"));

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        lbl.setText((String) value);
        lbl.setIcon(icon);
        return lbl;
    }
}