import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    private JPanel cartPanel;
    private JScrollPane cartScrollPane;
    private JList cartList;

    private String [] columns = {"Store", "Name", "Author", "Publisher", "Type", "Price", "Link", "Add2Cart"};
    private DefaultTableModel tableModel = null;
    private ActionListener actionListener = null;
    private ArrayList<Pair<String, Double>> cart = null;
    private ArrayList<Product> searchResults = null;

    private final Integer linkColumn = 6;
    private final Integer addCartColumn = 7;


    public MainScreen() {

        if(! Utils.checkInternetConnection()) {
            JOptionPane.showMessageDialog(null, "Check your internet connection");
            System.exit(404);
        }
        initializeGuiComponents();
        cart = new ArrayList<>();
    }


    private void initializeGuiComponents()
    {
        actionListener = e -> search();

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println(resultTable.getSelectedRow() + " " + resultTable.getSelectedColumn() + " clicked");
                    if(resultTable.getSelectedColumn() == linkColumn) {
                        Document link = Jsoup.parse((String) resultTable.getValueAt(resultTable.getSelectedRow(), linkColumn));
                        Utils.openBrowser(link.select("a").first().attr("href"));
                    }
                    else if(resultTable.getSelectedColumn() == addCartColumn) {
                        String store = searchResults.get(resultTable.getSelectedRow()).getStore().toUpperCase();
                        Double price =  searchResults.get(resultTable.getSelectedRow()).getPrice();
                        add2Cart(new Pair<>(store, price));
                        updateCart(cart);

                    }
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
                searchResults = OnlineSearcher.run(target_name + " " + target_author);
                filterProducts();
                updateTable(searchResults);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void filterProducts()
    {
        ArrayList<Product> tempPr = new ArrayList<>();

        for(Product p : this.searchResults){
            if (!combineCheckBox.isSelected() && p.getName().contains(target_name))
                tempPr.add(p);
            else if(combineCheckBox.isSelected() && p.getName().contains(target_name) && p.getAuthor().contains(target_author))
                tempPr.add(p);
        }
        this.searchResults = tempPr;
    }

    private void updateTable(ArrayList<Product> ps) {
        tableModel = Utils.createNonEditableTableModel(columns);
        for(Product p : ps){
            tableModel.addRow(new Object[]{p.getStore(), p.getName(), p.getAuthor(), p.getPublisher(), p.getType(),
                    Utils.createBoldString(p.getPrice() + " TL"), Utils.createLink(p.getLink()), Utils.createBoldString("ADD")});
        }
        resultTable.setModel(tableModel);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(75); // Store
        resultTable.getColumnModel().getColumn(5).setMaxWidth(75); // Link
        resultTable.getColumnModel().getColumn(6).setMaxWidth(50); // Price
        resultTable.getColumnModel().getColumn(7).setMaxWidth(50); // Add2Cart
        tablePane.getVerticalScrollBar().setBackground(Color.GRAY);

        if (tableModel.getRowCount() == 0)
            tableTitle.setText("No results found..");
    }

    private void updateCart(ArrayList< Pair<String, Double> > items)
    {
        DefaultListModel model = new DefaultListModel();
        for(Pair<String, Double> i : items)
            model.addElement(i.getKey() + "\t\t: " + i.getValue());
        this.cartList.setModel(model);
    }

    private void add2Cart(Pair<String, Double> item)
    {
        for (int i = 0; i < this.cart.size(); i++) {
            if(cart.get(i).getKey().equals(item.getKey()))
            {
                Pair<String, Double> t = cart.get(i);
                cart.remove(i);
                cart.add(new Pair<>(t.getKey(), t.getValue() + item.getValue()));
                Collections.sort(this.cart, (p1, p2) -> Double.compare(p1.getValue(), p2.getValue()));
                return;
            }
        }
        cart.add(item);
        Collections.sort(this.cart, (p1, p2) -> Double.compare(p1.getValue(), p2.getValue()));
    }
    public JPanel getPanelMain(){
        return panelMain;
    }
}