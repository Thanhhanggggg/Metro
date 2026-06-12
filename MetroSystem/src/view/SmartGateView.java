package view;

import controller.SmartGateController;
import Metro.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SmartGateView – Java Swing UI for Smart Gate Management System
 * Pattern: MVC  |  Module: UC07 Check-in, UC08 Check-out,
 *                           UC09 Xác thực
 *
 * Style: matches StationStaff UI (blue header, tabbed panel, status bar)
 * Dependencies: only standard Java Swing + Metro package
 */
public class SmartGateView extends JFrame {

    // ─── Palette ─────────────────────────────────────────────
    private static final Color C_HEADER  = new Color(0,  82, 164);
    private static final Color C_SUCCESS = new Color(0,  140, 60);
    private static final Color C_DANGER  = new Color(190, 20, 20);
    private static final Color C_WARNING = new Color(200, 120,  0);
    private static final Color C_PRIMARY = new Color(0,  102, 204);
    private static final Color C_VIOLET  = new Color(110,  0, 190);
    private static final Color C_BG      = new Color(235, 241, 252);
    private static final Color C_WHITE   = Color.WHITE;
    private static final Color C_BORDER  = new Color(195, 210, 235);
    private static final Color C_TEXT    = new Color(25,  30,  65);
    private static final Color C_MUTED   = new Color(110, 120, 150);

    // ─── MVC ─────────────────────────────────────────────────
    private final SmartGateController ctrl = new SmartGateController();

    // ─── Shared widgets ──────────────────────────────────────
    private JLabel   lblClock, lblStatus;
    private JPanel   pnlGates;
    private JTextArea taLog;

    // Tab 1
    private JTextField txtCI;
    private JTextArea  taCI;

    // Tab 2
    private JTextField txtCO;
    private JTextArea  taCO;

    // Tab 3
    private JComboBox<String> cbValGate;
    private JTextField        txtValTicket;
    private JTextArea         taVal;

    // Tab 4
//    private JComboBox<String> cbFaultGate;
//    private JTextArea         taFaultDesc, taFaultResult;

    // ─────────────────────────────────────────────────────────
    public SmartGateView() {
        setTitle("Smart Gate Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildBody(),      BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        setContentPane(root);

        startClock();
        setVisible(true);
    }

    // ═══════════════════════════════════════════════════════
    //  HEADER
    // ═══════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(C_HEADER);
        h.setBorder(new EmptyBorder(14, 22, 14, 22));

        // Left – icon + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel ico = new JLabel();
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        ico.setForeground(C_WHITE);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        JLabel t1 = new JLabel("SMART GATE MANAGEMENT");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t1.setForeground(C_WHITE);
        JLabel t2 = new JLabel("Metro Ticket & Gate Control System");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t2.setForeground(new Color(170, 200, 255));
        titles.add(t1); titles.add(t2);
        left.add(ico); left.add(titles);

        // Right – clock
        lblClock = new JLabel("", SwingConstants.RIGHT);
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblClock.setForeground(C_WHITE);

        h.add(left, BorderLayout.WEST);
        h.add(lblClock, BorderLayout.EAST);
        return h;
    }

    // ═══════════════════════════════════════════════════════
    //  BODY  (left sidebar + right tabs)
    // ═══════════════════════════════════════════════════════
    private JSplitPane buildBody() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSidebar(), buildTabs());
        sp.setDividerLocation(215);
        sp.setDividerSize(5);
        sp.setBorder(null);
        return sp;
    }

    // ─── Sidebar: gate status ────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 4));

        JLabel lbl = new JLabel(" Trạng thái cổng");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(C_HEADER);
        lbl.setBorder(new MatteBorder(0, 0, 2, 0, C_HEADER));
        p.add(lbl, BorderLayout.NORTH);

        pnlGates = new JPanel();
        pnlGates.setLayout(new BoxLayout(pnlGates, BoxLayout.Y_AXIS));
        pnlGates.setBackground(C_BG);
        refreshGates();

        JScrollPane sc = new JScrollPane(pnlGates,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        p.add(sc, BorderLayout.CENTER);

        JButton btn = btn("Làm mới", C_PRIMARY);
        btn.addActionListener(e -> refreshGates());
        JPanel bp = new JPanel();
        bp.setBackground(C_BG);
        bp.add(btn);
        p.add(bp, BorderLayout.SOUTH);
        return p;
    }

    private void refreshGates() {
        pnlGates.removeAll();
        List<SmartGate> gates = ctrl.getGates();
        for (SmartGate g : gates) {
            pnlGates.add(gateCard(g));
            pnlGates.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        if (gates.isEmpty()) {
            JLabel none = new JLabel("  (Không có cổng hoạt động)");
            none.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            none.setForeground(C_MUTED);
            pnlGates.add(none);
        }
        pnlGates.revalidate();
        pnlGates.repaint();
    }

    private JPanel gateCard(SmartGate gate) {
        String gid = gate.getGateId();
        int num = 0;
        try { num = Integer.parseInt(gid.replaceAll("[^0-9]", "")); } catch (Exception ignored){}
        String typeStr = (num <= 2) ? "VÀO (IN)" : "RA (OUT)";
        Color typeColor = (num <= 2) ? C_SUCCESS : C_PRIMARY;

        JPanel card = new JPanel(new GridLayout(3, 1, 0, 1));
        card.setBackground(C_WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        JLabel id = new JLabel(gid);
        id.setFont(new Font("Segoe UI", Font.BOLD, 13)); id.setForeground(C_TEXT);

        JLabel type = new JLabel("Loại: " + typeStr);
        type.setFont(new Font("Segoe UI", Font.PLAIN, 11)); type.setForeground(typeColor);

        JLabel status = new JLabel("● Hoạt động");
        status.setFont(new Font("Segoe UI", Font.BOLD, 11)); status.setForeground(C_SUCCESS);

        card.add(id); card.add(type); card.add(status);
        return card;
    }

    // ─── Tabs ─────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tp = new JTabbedPane();
        tp.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tp.addTab("  Check-in  ",       tabCheckIn());
        tp.addTab("  Check-out  ",      tabCheckOut());
        tp.addTab("  Xác thực  ",       tabValidate());
        tp.addTab("  Nhật ký  ",        tabLog());

        tp.setForegroundAt(0, C_SUCCESS);
        tp.setForegroundAt(1, C_PRIMARY);
        tp.setForegroundAt(2, C_VIOLET);
        tp.setForegroundAt(3, C_DANGER);
        return tp;
    }

    // ═══════════════════════════════════════════════════════
    //  TAB 1 – CHECK-IN  (UC07)
    // ═══════════════════════════════════════════════════════
    private JPanel tabCheckIn() {
        JPanel p = tabBase();
        p.add(sectionLbl("Check-in hành khách"), gbc(0,0,2,1,0,0, GridBagConstraints.BOTH));

        p.add(lbl("Mã vé (Ticket ID):"), gbc(0,1,1,1,0,0, GridBagConstraints.NONE));
        txtCI = input();
        p.add(txtCI, gbc(1,1,1,1,1,0, GridBagConstraints.HORIZONTAL));

        JButton b = btn("Thực hiện Check-in", C_SUCCESS);
        b.addActionListener(e -> {
            String id = txtCI.getText().trim();
            if (id.isEmpty()) { warn("Vui lòng nhập mã vé!"); return; }
            String res = ctrl.checkIn(id);
            showResult(taCI, res);
            log(res); txtCI.setText("");
            refreshGates();
        });
        p.add(b, gbc(1,2,1,1,0,0, GridBagConstraints.NONE));

        p.add(lbl("Kết quả:"), gbc(0,3,1,1,0,0, GridBagConstraints.NONE));
        taCI = result(); p.add(scroll(taCI), gbc(1,3,1,1,1,1, GridBagConstraints.BOTH));
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  TAB 2 – CHECK-OUT  (UC08)
    // ═══════════════════════════════════════════════════════
    private JPanel tabCheckOut() {
        JPanel p = tabBase();
        p.add(sectionLbl("Check-out hành khách"), gbc(0,0,2,1,0,0, GridBagConstraints.BOTH));

        p.add(lbl("Mã vé (Ticket ID):"), gbc(0,1,1,1,0,0, GridBagConstraints.NONE));
        txtCO = input();
        p.add(txtCO, gbc(1,1,1,1,1,0, GridBagConstraints.HORIZONTAL));

        JButton b = btn("Thực hiện Check-out", C_PRIMARY);
        b.addActionListener(e -> {
            String id = txtCO.getText().trim();
            if (id.isEmpty()) { warn("Vui lòng nhập mã vé!"); return; }
            String res = ctrl.checkOut(id);
            showResult(taCO, res);
            log(res); txtCO.setText("");
        });
        p.add(b, gbc(1,2,1,1,0,0, GridBagConstraints.NONE));

        p.add(lbl("Kết quả:"), gbc(0,3,1,1,0,0, GridBagConstraints.NONE));
        taCO = result(); p.add(scroll(taCO), gbc(1,3,1,1,1,1, GridBagConstraints.BOTH));
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  TAB 3 – XÁC THỰC HÀNH TRÌNH  (UC09)
    // ═══════════════════════════════════════════════════════
    private JPanel tabValidate() {
        JPanel p = tabBase();
        p.add(sectionLbl("Xác thực hành trình qua cổng"), gbc(0,0,2,1,0,0, GridBagConstraints.BOTH));

        p.add(lbl("Chọn cổng:"), gbc(0,1,1,1,0,0, GridBagConstraints.NONE));
        cbValGate = combo(ctrl.getGateIds());
        p.add(cbValGate, gbc(1,1,1,1,1,0, GridBagConstraints.HORIZONTAL));

        p.add(lbl("Mã vé:"), gbc(0,2,1,1,0,0, GridBagConstraints.NONE));
        txtValTicket = input();
        p.add(txtValTicket, gbc(1,2,1,1,1,0, GridBagConstraints.HORIZONTAL));

        JButton b = btn("Xác thực vé", C_VIOLET);
        b.addActionListener(e -> {
            String gateId   = (String) cbValGate.getSelectedItem();
            String ticketId = txtValTicket.getText().trim();
            if (ticketId.isEmpty()) { warn("Vui lòng nhập mã vé!"); return; }
            String res  = ctrl.validateTicket(gateId, ticketId);
            String info = ctrl.getTicketInfo(ticketId);
            showResult(taVal, res + (info.isEmpty() ? "" : "\n\n─────────────\n" + info));
            log(res); txtValTicket.setText("");
        });
        p.add(b, gbc(1,3,1,1,0,0, GridBagConstraints.NONE));

        p.add(lbl("Kết quả:"), gbc(0,4,1,1,0,0, GridBagConstraints.NONE));
        taVal = result(); p.add(scroll(taVal), gbc(1,4,1,1,1,1, GridBagConstraints.BOTH));
        return p;
    }

   

    // ═══════════════════════════════════════════════════════
    //  TAB 4 – NHẬT KÝ
    // ═══════════════════════════════════════════════════════
    private JPanel tabLog() {
        JPanel p = tabBase();
        p.add(sectionLbl("Nhật ký hoạt động cổng"), gbc(0,0,2,1,0,0, GridBagConstraints.BOTH));

        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setFont(new Font("Courier New", Font.PLAIN, 12));
        taLog.setBackground(new Color(18, 18, 38));
        taLog.setForeground(new Color(80, 240, 100));
        taLog.setBorder(new EmptyBorder(8, 10, 8, 10));
        p.add(scroll(taLog), gbc(0,1,2,1,1,1, GridBagConstraints.BOTH));

        JButton clear = btn("Xóa nhật ký", C_WARNING);
        clear.addActionListener(e -> { taLog.setText(""); ctrl.getScanLog().clear(); });
        p.add(clear, gbc(0,2,2,1,0,0, GridBagConstraints.NONE));
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  STATUS BAR
    // ═══════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(218, 228, 248));
        bar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1,0,0,0, C_BORDER),
                new EmptyBorder(5, 14, 5, 14)));

        lblStatus = new JLabel("Sẵn sàng.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(C_TEXT);

//        JLabel ver = new JLabel("SmartGate System v1.0  |  UC07 · UC08 · UC09 · UC13");
//        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
//        ver.setForeground(C_MUTED);

        bar.add(lblStatus, BorderLayout.WEST);
 //       bar.add(ver, BorderLayout.EAST);
        return bar;
    }

    // ═══════════════════════════════════════════════════════
    //  UTILITIES
    // ═══════════════════════════════════════════════════════
    private JPanel tabBase() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        return p;
    }

    private GridBagConstraints gbc(int x, int y, int w, int h, double wx, double wy, int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x; c.gridy=y; c.gridwidth=w; c.gridheight=h;
        c.weightx=wx; c.weighty=wy; c.fill=fill;
        c.insets = new Insets(6, 4, 6, 8);
        c.anchor = GridBagConstraints.NORTHWEST;
        return c;
    }

    private JLabel sectionLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(C_HEADER);
        l.setBorder(new MatteBorder(0,0,2,0, C_HEADER));
        return l;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(C_TEXT);
        return l;
    }

    private JTextField input() {
        JTextField tf = new JTextField(22);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_BORDER, 1, true), new EmptyBorder(6,10,6,10)));
        return tf;
    }

    private JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(C_WHITE);
        return cb;
    }

    private JTextArea result() {
        JTextArea ta = new JTextArea(6, 30);
        ta.setEditable(false);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setBackground(new Color(243, 246, 255));
        ta.setBorder(new EmptyBorder(8,10,8,10));
        return ta;
    }

    private JScrollPane scroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(new LineBorder(C_BORDER, 1));
        return sp;
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg); b.setForeground(C_WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private void showResult(JTextArea ta, String res) {
        String clean = res.replaceFirst("^(OK|FAIL|WARN):", "");
        ta.setText(clean);
        lblStatus.setText(clean);
    }

    private void log(String res) {
        String clean = res.replaceFirst("^(OK|FAIL|WARN):", "");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        taLog.append("[" + ts + "] " + clean + "\n");
        taLog.setCaretPosition(taLog.getDocument().getLength());
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    private void startClock() {
        new Timer(1000, e -> lblClock.setText(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy"))))
                .start();
    }

    // ═══════════════════════════════════════════════════════
    //  MAIN – seed demo data then launch
    // ═══════════════════════════════════════════════════════
    public static void main(String[] args) {
        seedDemo();
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(SmartGateView::new);
    }

    /**
     * Tạo dữ liệu mẫu cho TicketManager để demo check-in/check-out.
     * Chỉnh sửa các class bên dưới cho phù hợp với cách bạn instantiate Ticket con.
     *
     * Vé mẫu:
     *   T001 – ActiveState  → có thể check-in
     *   T002 – ActiveState  → có thể check-in
     *   T003 – ActiveState  → có thể check-in
     */
    private static void seedDemo() {
        TicketManager tm = TicketManager.getInstance();
        System.out.println("[SmartGateView] Seed demo: add your Ticket objects to TicketManager here.");
    }
}