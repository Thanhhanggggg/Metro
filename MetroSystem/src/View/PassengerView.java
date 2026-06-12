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
    // TAB 4
    private JTextField txtTicketId;
    private JButton btnCheckIn;
    private JLabel lblCheckIn;
    // Tab Check Out
    private JTextField txtCheckOutTicketId;
    private JButton btnCheckOut;

    // Tab Hủy vé
    private JTextField txtCancelTicketId;
    private JButton btnCancelTicket;

	private JLabel lblCheckOut;
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
        tabbedPane.addTab("Check-In", buildCheckInTab());
        add(tabbedPane, BorderLayout.CENTER);
       
        tabbedPane.addTab("Check-Out", buildCheckOutTab());
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

    private JPanel buildCheckInTab() {
        JPanel p = createTabPanel();
        txtTicketId = styledTextField();
        btnCheckIn = styledButton("Check-In");
        lblCheckIn = new JLabel(" ");
        btnCheckIn.addActionListener(e -> {
            if(controller != null) {
                controller.handleAction("CHECK_IN", txtTicketId.getText().trim());
            }
        });

        p.add(styledLabel("Nhập mã vé cần Check-In"));
        p.add(Box.createVerticalStrut(8));
        p.add(txtTicketId);
        p.add(Box.createVerticalStrut(12));
        p.add(btnCheckIn);
        p.add(Box.createVerticalStrut(15));
        p.add(lblCheckIn);

        return p;
    }
    private JPanel buildCheckOutTab() {
        JPanel panel = createTabPanel();
        JLabel lblTitle = styledLabel("Nhập mã vé cần Check-Out");
        lblCheckOut = new JLabel(" ");

        txtCheckOutTicketId = styledTextField();
        btnCheckOut = styledButton("Check-Out");
        btnCheckOut.addActionListener(e -> {
            if(controller != null) {
                controller.handleAction("CHECK_OUT",txtCheckOutTicketId.getText().trim());
            }
        });
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(8));
        panel.add(txtCheckOutTicketId);
        panel.add(Box.createVerticalStrut(12));
        panel.add(btnCheckOut);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblCheckOut);

        return panel;
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
    public void showCheckInResult(String message) {
        JOptionPane.showMessageDialog(this,message,"Check-In",JOptionPane.INFORMATION_MESSAGE);
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
    public void showCheckOutResult(String message) {
        JOptionPane.showMessageDialog(this,message,"Check-Out",JOptionPane.INFORMATION_MESSAGE);
    }
    public void showCancelResult(String message) {
        JOptionPane.showMessageDialog(this,message, "Hủy vé",JOptionPane.INFORMATION_MESSAGE);
    }
}