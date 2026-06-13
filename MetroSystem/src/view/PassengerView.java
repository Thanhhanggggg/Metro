package view;

import controller.IController;
import Metro.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PassengerView extends JPanel {
package view;

import controller.IController;
import Metro.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PassengerView extends JPanel {

    private IController controller;

    private static final Color BLUE       = new Color(30, 90, 180);
    private static final Color BLUE_LIGHT = new Color(70, 130, 220);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BG         = new Color(245, 247, 250);

    private JTabbedPane tabbedPane;

    // TAB 1
    private JComboBox<MetroLine> cboLine;
    private JButton btnViewRoute;
    private JTextArea taRoute;

    // TAB 2
    private JComboBox<Station> cboFrom;
    private JComboBox<Station> cboTo;
    private JComboBox<TicketType> cboType;
    private JButton btnBuyTicket;
    private JTextArea taBuyResult;

    // TAB 3
    private JButton btnRefreshTickets;
    private JTextArea taTickets;

    // TAB 4 - Huy ve
    private JTextField txtCancelTicketId;
    private JButton btnCancelTicket;

    public PassengerView() {
        buildUI();
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("PASSENGER MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
        tabbedPane.setBackground(BG);
        tabbedPane.addTab("Tra cứu tuyến", buildRouteTab());
        tabbedPane.addTab("Mua vé",        buildBuyTicketTab());
        tabbedPane.addTab("Vé của tôi",    buildMyTicketTab());
        tabbedPane.addTab("Hủy vé",        buildCancelTicketTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    // TAB 1 - Tra cuu tuyen
    private JPanel buildRouteTab() {
        JPanel p = createTabPanel();

        cboLine = new JComboBox<>();
        cboLine.setFont(new Font("Arial", Font.PLAIN, 13));
        cboLine.setMaximumSize(new Dimension(460, 32));
        cboLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnViewRoute = styledButton("Xem tuyến");

        taRoute = new JTextArea(8, 30);
        taRoute.setEditable(false);
        taRoute.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taRoute.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane sp = new JScrollPane(taRoute);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(460, 150));

        btnViewRoute.addActionListener(e -> {
            MetroLine line = (MetroLine) cboLine.getSelectedItem();
            if (line == null) return;
            StringBuilder sb = new StringBuilder();
            for (Station s : line.getStations()) {
                sb.append(s.getStationName()).append("\n");
            }
            taRoute.setText(sb.toString());
        });

        p.add(styledLabel("Chọn tuyến:"));
        p.add(Box.createVerticalStrut(8));
        p.add(cboLine);
        p.add(Box.createVerticalStrut(10));
        p.add(btnViewRoute);
        p.add(Box.createVerticalStrut(16));
        p.add(sp);
        return p;
    }

    // TAB 2 - Mua ve
    private JPanel buildBuyTicketTab() {
        JPanel p = createTabPanel();

        cboFrom = new JComboBox<>();
        cboFrom.setFont(new Font("Arial", Font.PLAIN, 13));
        cboFrom.setMaximumSize(new Dimension(460, 32));
        cboFrom.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboTo = new JComboBox<>();
        cboTo.setFont(new Font("Arial", Font.PLAIN, 13));
        cboTo.setMaximumSize(new Dimension(460, 32));
        cboTo.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboType = new JComboBox<>(TicketType.values());
        cboType.setFont(new Font("Arial", Font.PLAIN, 13));
        cboType.setMaximumSize(new Dimension(460, 32));
        cboType.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnBuyTicket = styledButton("Mua vé");

        taBuyResult = new JTextArea(5, 30);
        taBuyResult.setEditable(false);
        taBuyResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taBuyResult.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane sp = new JScrollPane(taBuyResult);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(460, 100));

        btnBuyTicket.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("BUY_TICKET", cboFrom.getSelectedItem(), cboTo.getSelectedItem(), cboType.getSelectedItem());
        });

        p.add(styledLabel("Ga đi:"));
        p.add(Box.createVerticalStrut(6));
        p.add(cboFrom);
        p.add(Box.createVerticalStrut(12));

        p.add(styledLabel("Ga đến:"));
        p.add(Box.createVerticalStrut(6));
        p.add(cboTo);
        p.add(Box.createVerticalStrut(12));

        p.add(styledLabel("Loại vé:"));
        p.add(Box.createVerticalStrut(6));
        p.add(cboType);
        p.add(Box.createVerticalStrut(16));

        p.add(btnBuyTicket);
        p.add(Box.createVerticalStrut(16));
        p.add(sp);
        return p;
    }

    // TAB 3 - Ve cua toi
    private JPanel buildMyTicketTab() {
        JPanel p = createTabPanel();

        btnRefreshTickets = styledButton("Làm mới");

        taTickets = new JTextArea(12, 30);
        taTickets.setEditable(false);
        taTickets.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taTickets.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane sp = new JScrollPane(taTickets);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRefreshTickets.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("VIEW_TICKETS");
        });

        p.add(btnRefreshTickets);
        p.add(Box.createVerticalStrut(16));
        p.add(sp);
        return p;
    }

    // TAB 4 - Huy ve
    private JPanel buildCancelTicketTab() {
        JPanel p = createTabPanel();

        JLabel lbl = styledLabel("Nhập mã vé cần hủy:");
        txtCancelTicketId = styledTextField();
        btnCancelTicket   = styledButton("Hủy vé");

        btnCancelTicket.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("CANCEL_TICKET", txtCancelTicketId.getText().trim());
        });

        p.add(lbl);
        p.add(Box.createVerticalStrut(8));
        p.add(txtCancelTicketId);
        p.add(Box.createVerticalStrut(10));
        p.add(btnCancelTicket);
        return p;
    }

    // Controller goi de cap nhat View
    public void setLines(List<MetroLine> lines) {
        cboLine.removeAllItems();
        for (MetroLine line : lines)
            cboLine.addItem(line);
    }

    public void setStations(List<Station> stations) {
        cboFrom.removeAllItems();
        cboTo.removeAllItems();
        for (Station s : stations) {
            cboFrom.addItem(s);
            cboTo.addItem(s);
        }
    }

    public void showBuyResult(String msg) {
        SwingUtilities.invokeLater(() -> taBuyResult.setText(msg));
    }

    public void showMyTickets(List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        for (Ticket t : tickets) {
            sb.append(t.getTicketId())
              .append(" | ")
              .append(t.getType())
              .append(" | ")
              .append(t.getStatus())
              .append("\n");
        }
        SwingUtilities.invokeLater(() -> taTickets.setText(sb.toString()));
    }

    public void showRouteResult(String result) {
        SwingUtilities.invokeLater(() -> taRoute.setText(result));
    }

    public void showFareResult(String result) {
        SwingUtilities.invokeLater(() -> taBuyResult.setText(result));
    }

    public void showCancelResult(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, message, "Hủy vé", JOptionPane.INFORMATION_MESSAGE));
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE));
    }

    // Ho tro
    private JPanel createTabPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 40, 24, 40));
        return p;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(460, 32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(BLUE);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BLUE_LIGHT); }
            public void mouseExited (MouseEvent e) { btn.setBackground(BLUE); }
        });
        return btn;
    }

}
    private IController controller;

    private static final Color BLUE = new Color(30, 90, 180);
    private static final Color BLUE_LIGHT = new Color(70, 130, 220);
    private static final Color WHITE = Color.WHITE;
    private static final Color BG = new Color(245, 247, 250);

    private JTabbedPane tabbedPane;
    // TAB 1
    private JComboBox<MetroLine> cboLine;
    private JButton btnViewRoute;
    private JTextArea taRoute;
    // TAB 2
    private JComboBox<Station> cboFrom;
    private JComboBox<Station> cboTo;
    private JComboBox<TicketType> cboType;
    private JButton btnBuyTicket;
    private JTextArea taBuyResult;
    // TAB 3
    private JButton btnRefreshTickets;
    private JTextArea taTickets;
    // Tab Hủy vé
    private JTextField txtCancelTicketId;
    private JButton btnCancelTicket;
    public PassengerView() {
        buildUI();
    }
    public void setController(IController controller) {
        this.controller = controller;
    }
    private void buildUI() {
        setLayout(new BorderLayout());
        JPanel header = new JPanel();
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(12,20,12,20));
        JLabel title = new JLabel("PASSENGER MANAGEMENT");
        title.setForeground(WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tra cứu tuyến", buildRouteTab());

        tabbedPane.addTab("Mua vé", buildBuyTicketTab());
        tabbedPane.addTab("Vé của tôi", buildMyTicketTab());
        add(tabbedPane, BorderLayout.CENTER);
    
        tabbedPane.addTab("Hủy vé", buildCancelTicketTab());
    }
    private JPanel buildRouteTab() {
        JPanel p = createTabPanel();
        cboLine = new JComboBox<>();
        btnViewRoute = styledButton("Xem tuyến");
        taRoute = new JTextArea(5,30);
        taRoute.setEditable(false);
        
        JScrollPane sp = new JScrollPane(taRoute);
        sp.setAlignmentX(Component.CENTER_ALIGNMENT);
        sp.setMaximumSize(new Dimension(700, 150));
        sp.setPreferredSize(new Dimension(700, 150));
        sp.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cboLine.setFont(new Font("Arial", Font.BOLD, 13));
        cboLine.setMaximumSize(new Dimension(700, 45));
        cboLine.setPreferredSize(new Dimension(700, 45));
        cboLine.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        btnViewRoute.addActionListener(e -> {
        	MetroLine line = (MetroLine)cboLine.getSelectedItem();
            if(line == null)
                return;
            StringBuilder sb = new StringBuilder();
            for(Station s : line.getStations()) {
                sb.append(s.getStationName()).append("\n");
            }
            taRoute.setText(sb.toString());
            });
        p.add(styledLabel("Chọn tuyến"));
        p.add(Box.createVerticalStrut(8));
        p.add(cboLine);
        p.add(Box.createVerticalStrut(10));
        p.add(btnViewRoute);
        p.add(Box.createVerticalStrut(15));
        p.add(sp);
        return p;
    }
    private JPanel buildBuyTicketTab() {
        JPanel p = createTabPanel();
        cboFrom = new JComboBox<>();
        cboTo = new JComboBox<>();
        cboType = new JComboBox<>(TicketType.values());
        btnBuyTicket = styledButton("Mua vé");
        taBuyResult = new JTextArea(8,30);
        taBuyResult.setEditable(false);
        JScrollPane sp = new JScrollPane(taBuyResult);
        btnBuyTicket.addActionListener(e -> {
            if(controller != null) {
                controller.handleAction("BUY_TICKET", cboFrom.getSelectedItem(), cboTo.getSelectedItem(), cboType.getSelectedItem());
            }
        });
        p.add(styledLabel("Ga đi"));
        p.add(Box.createVerticalStrut(5));
        p.add(cboFrom);
        p.add(Box.createVerticalStrut(10));

        p.add(styledLabel("Ga đến"));
        p.add(Box.createVerticalStrut(5));
        p.add(cboTo);

        p.add(Box.createVerticalStrut(10));

        p.add(styledLabel("Loại vé"));
        p.add(Box.createVerticalStrut(5));
        p.add(cboType);

        p.add(Box.createVerticalStrut(15));
        p.add(btnBuyTicket);

        p.add(Box.createVerticalStrut(15));
        p.add(sp);

        return p;
    }
    private JPanel buildMyTicketTab() {
        JPanel p = createTabPanel();
        btnRefreshTickets = styledButton("Làm mới");

        taTickets = new JTextArea(12,30);
        taTickets.setEditable(false);
        JScrollPane sp = new JScrollPane(taTickets);
        btnRefreshTickets.addActionListener(e -> {
            if(controller != null) {
                controller.handleAction("VIEW_TICKETS");
            }
        });

        p.add(btnRefreshTickets);
        p.add(Box.createVerticalStrut(15));
        p.add(sp);

        return p;
    }

  
    private JPanel buildCancelTicketTab() {
        JPanel panel = createTabPanel();
        JLabel lblTitle = styledLabel("Nhập mã vé cần hủy");
        txtCancelTicketId = styledTextField();
        btnCancelTicket = styledButton("Hủy vé");
        btnCancelTicket.addActionListener(e -> {
            if(controller != null) {
                controller.handleAction("CANCEL_TICKET", txtCancelTicketId.getText().trim());
            }
        });
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(txtCancelTicketId);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnCancelTicket);

        return panel;
    }
    public void setLines(
            List<MetroLine> lines) {
        cboLine.removeAllItems();
        for(MetroLine line : lines)
            cboLine.addItem(line);
    }
    public void setStations(
            List<Station> stations) {
        cboFrom.removeAllItems();
        cboTo.removeAllItems();
        for(Station s : stations) {
            cboFrom.addItem(s);
            cboTo.addItem(s);
        }
    }
    public void showBuyResult(
            String msg) {
        taBuyResult.setText(msg);
    }

    public void showMyTickets(
            List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        for(Ticket t : tickets) {

            sb.append(t.getTicketId())
              .append(" | ")
              .append(t.getType())
              .append(" | ")
              .append(t.getStatus())
              .append("\n");
        }
        taTickets.setText(
                sb.toString());
    }
    private JPanel createTabPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 40, 24, 40));

        return p;
    }
    private JLabel styledLabel(
            String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(460, 32));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
        return tf;
    }
    private JButton styledButton(
            String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));

        btn.setBackground(BLUE);
        btn.setForeground(WHITE);

        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8,20,8,20));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
      public void mouseEntered( MouseEvent e) {
        btn.setBackground(BLUE_LIGHT);
        }
      public void mouseExited(MouseEvent e) {
        btn.setBackground(BLUE);
        }
                });
        return btn;
    }
    public void showRouteResult(String result) {
        taRoute.setText(result);
    }
    public void showFareResult(String result) {
        taBuyResult.setText(result);
    }
     public void showCancelResult(String message) {
        JOptionPane.showMessageDialog(this,message, "Hủy vé",JOptionPane.INFORMATION_MESSAGE);
    }
}
