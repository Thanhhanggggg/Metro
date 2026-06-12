package View;

import Controller.*;
import Metro.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StaffView implements Observer {
	private IController controller;

    private static final Color BLUE       = new Color(30, 90, 180);
    private static final Color BLUE_LIGHT = new Color(70, 130, 220);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BG         = new Color(245, 247, 250);

    private JFrame frame;
    private JTabbedPane tabbedPane;

    // Tab 1: Kiem tra ve
    private JTextField txtCheckTicketId;
    private JButton    btnCheckTicket;
    private JLabel     lblCheckResult;

    // Tab 2: Hoan ve
    private JTextField txtRefundTicketId;
    private JButton    btnRefund;
    private JTextArea  taRefundResult;

    // Tab 3: Bao cao su co
    private JTextField txtGateId;
    private JTextField txtFaultDesc;
    private JButton    btnReportFault;
    private JLabel     lblFaultResult;

    // Tab 4: Thng bao Heatnao
    private JTextArea  taAlerts;


    public StaffView() {
        buildUI();
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    //UI
    private void buildUI() {
        frame = new JFrame("Station Staff Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("STATION STAFF MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        header.add(title);
        frame.add(header, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
        tabbedPane.setBackground(BG);
        tabbedPane.addTab("Kiểm tra vé",  buildTabCheckTicket());
        tabbedPane.addTab("Hoàn vé",       buildTabRefund());
        tabbedPane.addTab("Báo cáo sự cố", buildTabFault());
        tabbedPane.addTab("Thông báo",      buildTabAlerts());
        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    //Tab 1- Kiem tra ve
    private JPanel buildTabCheckTicket() {
        JPanel p = createTabPanel();

        JLabel lbl = styledLabel("Nhập mã vé cần kiểm tra:");
        txtCheckTicketId = styledTextField();
        btnCheckTicket   = styledButton("Kiểm tra");
        lblCheckResult   = new JLabel(" ");
        lblCheckResult.setFont(new Font("Arial", Font.BOLD, 13));

        btnCheckTicket.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("CHECK_TICKET", txtCheckTicketId.getText().trim());
        });

        p.add(lbl);
        p.add(Box.createVerticalStrut(8));
        p.add(txtCheckTicketId);
        p.add(Box.createVerticalStrut(10));
        p.add(btnCheckTicket);
        p.add(Box.createVerticalStrut(16));
        p.add(lblCheckResult);
        return p;
    }

    //Tab 2 - Hoan ve
    private JPanel buildTabRefund() {
        JPanel p = createTabPanel();

        JLabel lbl      = styledLabel("Nhập mã vé cần hoàn:");
        txtRefundTicketId = styledTextField();
        btnRefund         = styledButton("Xác nhận hoàn vé");
        taRefundResult    = new JTextArea(5, 30);
        taRefundResult.setEditable(false);
        taRefundResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taRefundResult.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane sp = new JScrollPane(taRefundResult);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(460, 100));

        btnRefund.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("REFUND", txtRefundTicketId.getText().trim());
        });

        p.add(lbl);
        p.add(Box.createVerticalStrut(8));
        p.add(txtRefundTicketId);
        p.add(Box.createVerticalStrut(10));
        p.add(btnRefund);
        p.add(Box.createVerticalStrut(16));
        p.add(sp);
        return p;
    }

    // Tab 3: Bao cao su co
    private JPanel buildTabFault() {
        JPanel p = createTabPanel();

        JLabel lblGate = styledLabel("Mã cổng (Gate ID):");
        txtGateId      = styledTextField();
        JLabel lblDesc = styledLabel("Mô tả sự cố:");
        txtFaultDesc   = styledTextField();
        btnReportFault = styledButton("Báo cáo sự cố");
        lblFaultResult = new JLabel(" ");
        lblFaultResult.setFont(new Font("Arial", Font.BOLD, 13));

        btnReportFault.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("FAULT",
                        txtGateId.getText().trim(),
                        txtFaultDesc.getText().trim());
        });

        
        p.add(lblGate);
        p.add(Box.createVerticalStrut(6));
        p.add(txtGateId);
        p.add(Box.createVerticalStrut(12));
        p.add(lblDesc);
        p.add(Box.createVerticalStrut(6));
        p.add(txtFaultDesc);
        p.add(Box.createVerticalStrut(12));
        p.add(btnReportFault);
        p.add(Box.createVerticalStrut(16));
        p.add(lblFaultResult);
        return p;
        
    }

    
    //Tab 4 - Thong bao Heatmap
    private JPanel buildTabAlerts() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lbl = styledLabel("Thông báo cảnh báo lưu lượng:");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        taAlerts = new JTextArea();
        taAlerts.setEditable(false);
        taAlerts.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taAlerts.setBackground(new Color(240, 244, 255));
        JScrollPane sp = new JScrollPane(taAlerts);

        p.add(lbl, BorderLayout.NORTH);
        p.add(sp,  BorderLayout.CENTER);
        return p;
    }

    
    //Controller goi de cap nhat View
    //Hien thi ket qua kiem tra ve
    public void showCheckResult(String ticketId, boolean valid, String stateDesc) {
        SwingUtilities.invokeLater(() -> {
            if (valid) {
                lblCheckResult.setForeground(new Color(0, 140, 0));
                lblCheckResult.setText("Vé [" + ticketId + "] HỢP LỆ — " + stateDesc);
            } else {
                lblCheckResult.setForeground(Color.RED);
                lblCheckResult.setText("Vé [" + ticketId + "] KHÔNG HỢP LỆ — " + stateDesc);
            }
        });
    }

    //Hien thi ket qua hoan ve
    public void showRefundResult(String ticketId, boolean success, double amount, String reason) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                taRefundResult.setText(
                    "Hoàn vé thành công!\n" +
                    "Mã vé  : " + ticketId + "\n" +
                    "Số tiền: " + String.format("%,.0f VND", amount) + "\n" +
                    "Lý do  : " + reason
                );
            } else {
                taRefundResult.setText(
                    "Không thể hoàn vé!\n" +
                    "Mã vé  : " + ticketId + "\n" +
                    "Lý do  : " + reason
                );
            }
        });
    }

    //Hien thi ket qua bao cao su co
    public void showFaultLogged(String gateId, boolean success) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                lblFaultResult.setForeground(new Color(0, 140, 0));
                lblFaultResult.setText("✅ Đã vô hiệu hóa cổng [" + gateId + "] và lưu FaultLog.");
            } else {
                lblFaultResult.setForeground(Color.RED);
                lblFaultResult.setText("❌ Không tìm thấy cổng [" + gateId + "].");
            }
        });
    }

    // Hien thi loi chung
    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(frame, message, "Lỗi", JOptionPane.ERROR_MESSAGE));
    }

    
    @Override
    public void update() {
        HeatmapAlert alert = HeatmapService.getInstance().getLatestAlert();
        if (alert != null) showAlert(alert);
    }

    public void showAlert(HeatmapAlert alert) {
        SwingUtilities.invokeLater(() -> {
            taAlerts.append(alert.toString() + "\n");
            taAlerts.setCaretPosition(taAlerts.getDocument().getLength());
            // Tự chuyển sang tab thông báo nếu CRITICAL
            if (alert.getAlertLevel() == AlertLevel.CRITICAL) {
                tabbedPane.setSelectedIndex(3);
                JOptionPane.showMessageDialog(frame,
                    "KHẨN CẤP: Ga " + alert.getStation().getStationName()
                    + " đã quá tải " + String.format("%.0f%%", alert.getOccupancyRate() * 100),
                    "CẢNH BÁO KHẨN CẤP", JOptionPane.WARNING_MESSAGE);
            }
        });
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
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BLUE_LIGHT); }
            public void mouseExited (MouseEvent e) { btn.setBackground(BLUE); }
        });
        return btn;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
