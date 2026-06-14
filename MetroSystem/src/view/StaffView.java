package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import Metro.Observer;
import controller.*;
import Metro.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StaffView extends JPanel implements Observer {
    private IController controller;


    private static final Color BLUE       = new Color(30, 90, 180);
    private static final Color BLUE_LIGHT = new Color(70, 130, 220);
    private static final Color GREEN      = new Color(0, 140, 0);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BG         = new Color(245, 247, 250);
    private static final Color ROW_EVEN   = new Color(235, 242, 255);

    private JTabbedPane tabbedPane;

    //Tab 1 kiem tra ve
    private JTextField txtCheckTicketId;
    private JButton    btnCheckTicket;
    private JLabel     lblCheckResult;

    //Tab 2 hoan ve
    private JTextField txtRefundTicketId;
    private JButton    btnRefund;
    private JTextArea  taRefundResult;

    // Tab 3 bao cao su co
    private JTextField        txtGateId;
    private JTextField        txtFaultDesc;
    private JButton           btnReportFault;
    private JLabel            lblFaultResult;
    private DefaultTableModel faultTableModel;
    private static final String[] FAULT_COLS =
        { "#", "Gate ID", "Mô tả", "Thời gian", "Trạng thái" };

    // Tab 4 thong bao
    private DefaultTableModel alertTableModel;
    private static final String[] ALERT_COLS =
        { "#", "Ga", "Mật độ", "Mức", "Thời gian", "Trạng thái" };
    private JButton btnAckSelected;
    private JTable  alertTable;

    private final List<HeatmapAlert> alertList = new ArrayList<>();
    private int faultCount = 0;
    private int alertCount = 0;

    public StaffView() {
        buildView();
    }

    public void setController(IController controller) {
        this.controller = controller;
    }


    private void buildView() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // Header
        JPanel header = new JPanel();
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("STATION STAFF MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
        tabbedPane.setBackground(BG);
        tabbedPane.addTab("Kiểm tra vé",   buildTabCheckTicket());
        tabbedPane.addTab("Hoàn vé",        buildTabRefund());
        tabbedPane.addTab("Xử lý sự cố",  buildTabFault());
        tabbedPane.addTab("Thông báo",       buildTabAlerts());
        add(tabbedPane, BorderLayout.CENTER);
    }

    //Tab 1
    private JPanel buildTabCheckTicket() {
        JPanel p = createTabPanel();
        txtCheckTicketId = styledTextField();
        btnCheckTicket   = styledButton("Kiểm tra");
        lblCheckResult   = new JLabel(" ");
        lblCheckResult.setFont(new Font("Arial", Font.BOLD, 13));
        lblCheckResult.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCheckTicket.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("CHECK_TICKET", txtCheckTicketId.getText().trim());
        });

        p.add(styledLabel("Nhập mã vé cần kiểm tra:"));
        p.add(Box.createVerticalStrut(8));
        p.add(txtCheckTicketId);
        p.add(Box.createVerticalStrut(10));
        p.add(btnCheckTicket);
        p.add(Box.createVerticalStrut(16));
        p.add(lblCheckResult);
        return p;
    }

    //  TAB 2
    private JPanel buildTabRefund() {
        JPanel p = createTabPanel();
        txtRefundTicketId = styledTextField();
        btnRefund         = styledButton("Xác nhận hoàn vé");
        taRefundResult    = new JTextArea(5, 30);
        taRefundResult.setEditable(false);
        taRefundResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taRefundResult.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane sp = new JScrollPane(taRefundResult);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(580, 110));

        btnRefund.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("REFUND", txtRefundTicketId.getText().trim());
        });

        p.add(styledLabel("Nhập mã vé cần hoàn:"));
        p.add(Box.createVerticalStrut(8));
        p.add(txtRefundTicketId);
        p.add(Box.createVerticalStrut(10));
        p.add(btnRefund);
        p.add(Box.createVerticalStrut(16));
        p.add(sp);
        return p;
    }

    //  TAB 3 
    private JComponent buildTabFault() {
        JPanel formPanel = createTabPanel();
        formPanel.setBorder(new EmptyBorder(16, 30, 12, 30));

        txtGateId      = styledTextField();
        txtFaultDesc   = styledTextField();
        btnReportFault = styledButton("Báo cáo sự cố");
        lblFaultResult = new JLabel(" ");
        lblFaultResult.setFont(new Font("Arial", Font.BOLD, 13));
        lblFaultResult.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnReportFault.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("FAULT",
                    txtGateId.getText().trim(),
                    txtFaultDesc.getText().trim());
        });
     // ── Kích hoạt lại cổng ──────────────────────────── ← THÊM VÀO ĐÂY
        JTextField txtEnableGateId = styledTextField();
        JButton btnEnableGate = styledButton("Kích hoạt lại cổng");
        btnEnableGate.setOpaque(true);
        btnEnableGate.setBorderPainted(false);
        btnEnableGate.setBackground(new Color(0, 120, 60));
        btnEnableGate.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnEnableGate.setBackground(new Color(0, 160, 80)); }
            public void mouseExited (MouseEvent e) { btnEnableGate.setBackground(new Color(0, 120, 60)); }
        });
        btnEnableGate.addActionListener(e -> {
            if (controller != null)
                controller.handleAction("ENABLE_GATE", txtEnableGateId.getText().trim());
        });
        
        formPanel.add(styledLabel("Mã cổng (Gate ID):"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(txtGateId);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(styledLabel("Mô tả sự cố:"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(txtFaultDesc);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(btnReportFault);
        formPanel.add(Box.createVerticalStrut(12)); // 
        formPanel.add(styledLabel("Kích hoạt lại cổng (Gate ID):")); // 
        formPanel.add(Box.createVerticalStrut(6));  // 
        formPanel.add(txtEnableGateId);             // 
        formPanel.add(Box.createVerticalStrut(8));  // 
        formPanel.add(btnEnableGate);               // 
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblFaultResult);
        

        faultTableModel = new DefaultTableModel(FAULT_COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable faultTable = new JTable(faultTableModel);
        styleTable(faultTable);
        faultTable.getColumnModel().getColumn(0).setMaxWidth(35);
        faultTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        faultTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        faultTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        faultTable.getColumnModel().getColumn(4).setPreferredWidth(90);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG);
        historyPanel.setBorder(new CompoundBorder(
            new EmptyBorder(0, 16, 12, 16),
            new TitledBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)),
                " Lịch sử sự cố trong phiên ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), BLUE)
        ));
        historyPanel.add(new JScrollPane(faultTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, historyPanel);
        split.setDividerLocation(290);
        split.setResizeWeight(0.45);
        split.setBorder(null);
        return split;
    }

    //  TAB 4
    private JPanel buildTabAlerts() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG);
        JLabel lbl = styledLabel("Cảnh báo lưu lượng hành khách:");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        btnAckSelected = styledButton("Xác nhận đã xử lý");
        btnAckSelected.setOpaque(true);         // 
        btnAckSelected.setBorderPainted(false); // 
        btnAckSelected.setBackground(new Color(0, 120, 60));
        btnAckSelected.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnAckSelected.setBackground(new Color(0, 160, 80)); }
            public void mouseExited (MouseEvent e) { btnAckSelected.setBackground(new Color(0, 120, 60)); }
        });
        btnAckSelected.addActionListener(e -> acknowledgeSelected());
        topBar.add(lbl,            BorderLayout.WEST);
        topBar.add(btnAckSelected, BorderLayout.EAST);

        alertTableModel = new DefaultTableModel(ALERT_COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        alertTable = new JTable(alertTableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    String level  = (String) getValueAt(row, 3);
                    String status = (String) getValueAt(row, 5);
                    if ("Đã xử lý".equals(status))       c.setBackground(new Color(220, 240, 220));
                    else if ("CRITICAL".equals(level))    c.setBackground(new Color(255, 220, 220));
                    else if ("WARNING".equals(level))     c.setBackground(new Color(255, 240, 200));
                    else if ("ATTENTION".equals(level))   c.setBackground(new Color(220, 235, 255));
                    else c.setBackground(row % 2 == 0 ? ROW_EVEN : WHITE);
                }
                return c;
            }
        };
        styleTable(alertTable);
        alertTable.getColumnModel().getColumn(0).setMaxWidth(35);
        alertTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        alertTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        alertTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        alertTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        alertTable.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane sp = new JScrollPane(alertTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));

        p.add(topBar,        BorderLayout.NORTH);
        p.add(sp,            BorderLayout.CENTER);
        p.add(buildLegend(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildLegend() {
        JPanel l = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        l.setBackground(BG);
        l.add(legendDot(new Color(255, 220, 220), "CRITICAL"));
        l.add(legendDot(new Color(255, 240, 200), "WARNING"));
        l.add(legendDot(new Color(220, 235, 255), "ATTENTION"));
        l.add(legendDot(new Color(220, 240, 220), "Đã xử lý"));
        return l;
    }

    private JPanel legendDot(Color color, String label) {
        JPanel dot = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dot.setBackground(BG);
        JLabel box = new JLabel("  ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel txt = new JLabel(label);
        txt.setFont(new Font("Arial", Font.PLAIN, 11));
        dot.add(box); dot.add(txt);
        return dot;
    }

    //Controller
    public void showCheckResult(String ticketId, boolean valid, String stateDesc) {
        SwingUtilities.invokeLater(() -> {
            lblCheckResult.setForeground(valid ? GREEN : Color.RED);
            lblCheckResult.setText((valid ? "HỢP LỆ" : "KHÔNG HỢP LỆ")
                + " — [" + ticketId + "] " + stateDesc);
        });
    }

    public void showRefundResult(String ticketId, boolean success, double amount, String reason) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                taRefundResult.setText(
                    "Hoàn vé thành công!\n" +
                    "Mã vé  : " + ticketId + "\n" +
                    "Số tiền: " + String.format("%,.0f VND", amount) + "\n" +
                    "Lý do  : " + reason);
            } else {
                taRefundResult.setText(
                    "Không thể hoàn vé!\n" +
                    "Mã vé  : " + ticketId + "\n" +
                    "Lý do  : " + reason);
            }
        });
    }

    public void showFaultLogged(String gateId, boolean success, String desc) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                lblFaultResult.setForeground(GREEN);
                lblFaultResult.setText("Đã vô hiệu hóa cổng [" + gateId + "] và lưu FaultLog.");
                faultCount++;
                String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM"));
                faultTableModel.addRow(new Object[]{
                    faultCount, gateId, desc, time, "DISABLED"
                });
            } else {
                lblFaultResult.setForeground(Color.RED);
                lblFaultResult.setText("Không tìm thấy cổng [" + gateId + "].");
            }
        });
    }

    public void showFaultLogged(String gateId, boolean success) {
        showFaultLogged(gateId, success, "-");
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                message, "Lỗi", JOptionPane.ERROR_MESSAGE));
    }


    @Override
    public void update() {
        HeatmapAlert alert = HeatmapService.getInstance().getLatestAlert();
        if (alert != null) showAlert(alert);
    }

    public void showAlert(HeatmapAlert alert) {
        SwingUtilities.invokeLater(() -> {
            alertList.add(alert);
            alertCount++;
            String time = alert.getTimestamp()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM"));
            alertTableModel.addRow(new Object[]{
                alertCount,
                alert.getStation().getStationName(),
                String.format("%.0f%%", alert.getOccupancyRate() * 100),
                alert.getAlertLevel().name(),
                time,
                "Chờ xử lý"
            });
            int last = alertTable.getRowCount() - 1;
            if (last >= 0)
                alertTable.scrollRectToVisible(alertTable.getCellRect(last, 0, true));

            if (alert.getAlertLevel() == AlertLevel.CRITICAL) {
                tabbedPane.setSelectedIndex(3);
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "KHẨN CẤP: Ga " + alert.getStation().getStationName()
                    + " quá tải " + String.format("%.0f%%", alert.getOccupancyRate() * 100),
                    "CẢNH BÁO KHẨN CẤP", JOptionPane.WARNING_MESSAGE);
            }
        });
    }


    private void acknowledgeSelected() {
        int[] rows = alertTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Vui lòng chọn ít nhất một cảnh báo để xác nhận.",
                "Chưa chọn", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (int row : rows) {
            alertTableModel.setValueAt("Đã xử lý", row, 5);
            int idx = (int) alertTableModel.getValueAt(row, 0) - 1;
            if (idx >= 0 && idx < alertList.size())
                alertList.get(idx).acknowledge();
        }
        alertTable.clearSelection();
        alertTable.repaint();
    }

    //Ho tro
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(26);
        table.setGridColor(new Color(210, 220, 235));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(180, 210, 255));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(BLUE);
        table.getTableHeader().setForeground(WHITE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private JPanel createTabPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20, 36, 20, 36));
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
        tf.setMaximumSize(new Dimension(560, 32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(BLUE);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);          // 
        btn.setBorderPainted(false);  //
        btn.setContentAreaFilled(true); // 
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BLUE_LIGHT); }
            public void mouseExited (MouseEvent e) { btn.setBackground(BLUE); }
        });
        return btn;
    }

	public void showGateEnabled(String gateId, boolean success) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(() -> {
	        if (success) {
	            lblFaultResult.setForeground(GREEN);
	            lblFaultResult.setText("Cổng [" + gateId + "] đã được kích hoạt lại.");
	        } else {
	            lblFaultResult.setForeground(Color.RED);
	            lblFaultResult.setText("Không tìm thấy cổng [" + gateId + "].");
	        }
	    });
	}
}
