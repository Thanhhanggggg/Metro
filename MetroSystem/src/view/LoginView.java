package view;

import controller.*;
import Metro.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LoginView – Màn hình chọn vai trò.
 *
 * Cách hoạt động:
 *   1. Hiện màn hình chọn vai trò (loginPanel).
 *   2. Người dùng chọn vai trò → swap sang panel tương ứng trong cùng JFrame.
 *   3. Nút "Đổi vai trò" ở topBar → swap ngược lại loginPanel.
 *
 * Tất cả 4 view (Passenger, Staff, SmartGate, Admin) đều là JPanel
 * → ghép trực tiếp vào JFrame, không cần tạo JFrame riêng cho từng view.
 */
public class LoginView {

    // ── Màu dùng chung ────────────────────────────────────────────────────────
    private static final Color BLUE       = new Color(30, 90, 180);
    private static final Color BLUE_LIGHT = new Color(70, 130, 220);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BG         = new Color(235, 241, 252);

    // ── JFrame dùng chung cho toàn hệ thống ──────────────────────────────────
    private JFrame frame;

    // ── Hai lớp panel chính ───────────────────────────────────────────────────
    private JPanel loginPanel;   // màn hình chọn vai trò
    private JPanel topBar;       // thanh trên khi đã vào view (Đổi vai trò + đồng hồ)
    private JLabel lblClock;
    private Timer  clockTimer;

    // ─────────────────────────────────────────────────────────────────────────
    public LoginView() {
        buildFrame();
        buildLoginPanel();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  JFrame dùng chung
    // ═════════════════════════════════════════════════════════════════════════
    private void buildFrame() {
        frame = new JFrame("Metro Ticket & Route Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 430);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Màn hình chọn vai trò
    // ═════════════════════════════════════════════════════════════════════════
    private void buildLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("METRO TICKET & ROUTE");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Management System");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(new Color(180, 210, 255));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        loginPanel.add(header, BorderLayout.NORTH);

        // ── Body ─────────────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(30, 60, 30, 60));

        JLabel lbl = new JLabel("Chọn vai trò của bạn:");
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(lbl);
        body.add(Box.createVerticalStrut(24));

        JButton btnPassenger = roleButton("Hành khách (Passenger)", new Color(25, 118, 210));
        JButton btnStaff     = roleButton("Nhân viên ga (Staff)", new Color(56, 142, 60));
        JButton btnGate      = roleButton("Quản lý cổng (Smart Gate)", new Color(123, 31, 162));
        JButton btnAdmin     = roleButton("Quản trị viên (Admin)", new Color(183, 28, 28));

        btnPassenger.addActionListener(e -> openView("PASSENGER"));
        btnStaff    .addActionListener(e -> openView("STAFF"));
        btnGate     .addActionListener(e -> openView("SMARTGATE"));
        btnAdmin    .addActionListener(e -> openView("ADMIN"));

        body.add(btnPassenger); body.add(Box.createVerticalStrut(12));
        body.add(btnStaff);     body.add(Box.createVerticalStrut(12));
        body.add(btnGate);      body.add(Box.createVerticalStrut(12));
        body.add(btnAdmin);
        loginPanel.add(body, BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────────────────────
        JPanel footer = new JPanel();
        footer.setBackground(new Color(220, 228, 245));
        footer.setBorder(new EmptyBorder(8, 0, 8, 0));
        JLabel fLbl = new JLabel("Nhóm 5 – OOP Project 2025");
        fLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        fLbl.setForeground(new Color(100, 100, 130));
        footer.add(fLbl);
        loginPanel.add(footer, BorderLayout.SOUTH);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Mở view theo vai trò – swap panel trong cùng JFrame
    // ═════════════════════════════════════════════════════════════════════════
    private void openView(String role) {
        JPanel viewPanel = createViewPanel(role);
        if (viewPanel == null) return;

        // Kích thước phù hợp theo từng view
        switch (role) {
        case "PASSENGER" -> frame.setSize(900, 660);
        case "STAFF"     -> frame.setSize(900, 660);   
        case "SMARTGATE" -> frame.setSize(900, 660);   
        case "ADMIN"     -> frame.setSize(900, 660); 
        }
        frame.setLocationRelativeTo(null);

        // Dọn frame, thêm topBar + view
        frame.getContentPane().removeAll();
        frame.add(buildTopBar(role), BorderLayout.NORTH);
        frame.add(viewPanel,         BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        // Bắt đồng hồ
        startClock();
    }

    /** Tạo panel tương ứng với vai trò, kết nối controller */
    private JPanel createViewPanel(String role) {
        switch (role) {

            case "PASSENGER": {
                PassengerView view = new PassengerView();
                PassengerController ctrl = new PassengerController();
                view.setController(ctrl);
                ctrl.setView(view);
//                ctrl.loadInitialData();
                return view;
            }

            case "STAFF": {
                StaffView view = new StaffView();
                StaffController ctrl = new StaffController();
                view.setController(ctrl);
                ctrl.setView(view);
                return view;
            }

            case "SMARTGATE": {
                // SmartGateView tự khởi tạo controller bên trong
                return new SmartGateView();
            }

            case "ADMIN": {
                AdminView view = new AdminView();
                AdminController ctrl = new AdminController(main.Main.ADMIN, view, main.Main.METRO_LINES);
                ctrl.setView(view);
                view.setController(ctrl);
                return view;
            }

            default:
                return null;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TopBar – hiển thị khi đang ở một view (có nút đổi vai trò + đồng hồ)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildTopBar(String role) {
        topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BLUE);
        topBar.setBorder(new EmptyBorder(6, 14, 6, 14));

        // Tên vai trò hiện tại
        String roleLabel = switch (role) {
            case "PASSENGER" -> "Hành khách";
            case "STAFF"     -> "Nhân viên ga";
            case "SMARTGATE" -> "Smart Gate";
            case "ADMIN"     -> "Admin";
            default          -> role;
        };
        JLabel lblRole = new JLabel(roleLabel);
        lblRole.setFont(new Font("Arial", Font.BOLD, 14));
        lblRole.setForeground(WHITE);
        topBar.add(lblRole, BorderLayout.WEST);

        // Phải: đồng hồ + nút đổi vai trò
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        lblClock = new JLabel();
        lblClock.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblClock.setForeground(new Color(180, 210, 255));
        right.add(lblClock);

        JButton btnSwitch = new JButton("← Đổi vai trò");
        btnSwitch.setFont(new Font("Arial", Font.BOLD, 12));
        btnSwitch.setBackground(new Color(255, 255, 255, 50));
        btnSwitch.setForeground(Color.BLACK);
        btnSwitch.setFocusPainted(false);
        btnSwitch.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnSwitch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSwitch.addActionListener(e -> returnToLogin());
        btnSwitch.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnSwitch.setBackground(BLUE_LIGHT); }
            public void mouseExited (MouseEvent e) { btnSwitch.setBackground(new Color(255,255,255,50)); }
        });
        right.add(btnSwitch);

        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Quay về màn hình chọn vai trò
    // ═════════════════════════════════════════════════════════════════════════
    private void returnToLogin() {
        stopClock();

        frame.getContentPane().removeAll();
        frame.setSize(480, 430);
        frame.setLocationRelativeTo(null);
        frame.add(loginPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Đồng hồ
    // ═════════════════════════════════════════════════════════════════════════
    private void startClock() {
        stopClock();
        clockTimer = new Timer(1000, e -> {
            if (lblClock != null)
                lblClock.setText(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy")));
        });
        clockTimer.start();
        // Cập nhật ngay lập tức
        lblClock.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy")));
    }

    private void stopClock() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Helper
    // ═════════════════════════════════════════════════════════════════════════
    private JButton roleButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(320, 46));
        btn.setPreferredSize(new Dimension(320, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Show + Main
    // ═════════════════════════════════════════════════════════════════════════
    public void show() {
        frame.getContentPane().removeAll();
        frame.add(loginPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginView().show();
        });
    }
}
