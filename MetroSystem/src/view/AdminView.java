package view;

import controller.IController;
import Metro.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class AdminView {

	private IController controller;

	// --- Mau sac ---
	private static final Color BLUE = new Color(25, 80, 160);
	private static final Color BLUE_LIGHT = new Color(60, 120, 210);
	private static final Color BG = new Color(245, 247, 250);
	private static final Color WHITE = Color.WHITE;
	private static final Color GREEN = new Color(0, 130, 0);
	private static final Color RED_DARK = new Color(180, 0, 0);

	// --- Frame chinh ---
	private JFrame frame;
	private JTabbedPane tabbedPane;

	// =========================================================
	// Tab 1: Quan ly Tuyen & Ga
	// =========================================================
	// Panel tuyen
	private DefaultTableModel lineTableModel;
	private JTable lineTable;
	private MetroLine selectedLine;

	private JTextField txtLineName;
	private JComboBox<LineStatus> cbLineStatus;
	private JButton btnAddLine, btnUpdateLine, btnRemoveLine;

	// Panel ga
	private DefaultTableModel stationTableModel;
	private JTable stationTable;
	private Station selectedStation;

	private JTextField txtStationName;
	private JTextField txtStationCapacity;
	private JButton btnAddStation, btnUpdateStation, btnRemoveStation;

	// =========================================================
	// Tab 2: Cau hinh Gia ve
	// =========================================================
	private JTextField txtBaseFare, txtPerStop;
    private JTextField txtDailyPrice, txtMonthlyPrice;      
	private JTextField txtNormalRate, txtStudentRate, txtSeniorRate, txtDisableRate;
	private JButton btnSetFare, btnUpdateDiscounts;
	private JLabel lblFareResult;

	// =========================================================
	// Tab 3: Bao cao
	// =========================================================
	private JTextField txtDateRange;
	private JButton btnRevenue, btnHeatmap;
	private JTextArea taReport;

	// =========================================================
	public AdminView() {
		buildUI();
	}

	public void setController(IController ctrl) {
		this.controller = ctrl;
		if (ctrl != null)
			ctrl.handleAction("LOAD_LINES");
	}

	// =========================================================
	// Xay dung giao dien chinh
	// =========================================================
	private void buildUI() {
		frame = new JFrame("He Thong Quan Ly Metro - Admin");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(860, 600);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		// Header
		JPanel header = new JPanel();
		header.setBackground(BLUE);
		header.setBorder(new EmptyBorder(14, 24, 14, 24));
		JLabel title = new JLabel("HE THONG QUAN LY METRO - ADMIN");
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setForeground(WHITE);
		header.add(title);
		frame.add(header, BorderLayout.NORTH);

		// Tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
		tabbedPane.addTab("Quan ly Tuyen & Ga", buildTabLineStation());
		tabbedPane.addTab("Cau hinh Gia ve", buildTabFare());
		tabbedPane.addTab("Bao cao", buildTabReport());
		frame.add(tabbedPane, BorderLayout.CENTER);
	}

	// =========================================================
	// Tab 1: Quan ly Tuyen & Ga
	// =========================================================
	private JPanel buildTabLineStation() {
		JPanel outer = new JPanel(new GridLayout(1, 2, 10, 0));
		outer.setBackground(BG);
		outer.setBorder(new EmptyBorder(12, 12, 12, 12));
		outer.add(buildLinePanel());
		outer.add(buildStationPanel());
		return outer;
	}

	// --- Panel trai: Danh sach tuyen ---
	private JPanel buildLinePanel() {
		JPanel p = new JPanel(new BorderLayout(0, 8));
		p.setBackground(BG);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BLUE, 1), "Danh sach Tuyen",
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 13), BLUE));

		// Bang tuyen: STT | Ma tuyen | Ten tuyen | Trang thai
		lineTableModel = new DefaultTableModel(new String[] { "STT", "Ma tuyen", "Ten tuyen", "Trang thai" }, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		lineTable = new JTable(lineTableModel);
		styleTable(lineTable);
		lineTable.getColumnModel().getColumn(0).setPreferredWidth(35);
		lineTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		lineTable.getColumnModel().getColumn(2).setPreferredWidth(170);
		lineTable.getColumnModel().getColumn(3).setPreferredWidth(80);

		// Click chon tuyen
		lineTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				onLineSelected();
		});

		p.add(new JScrollPane(lineTable), BorderLayout.CENTER);

		// Form nhap
		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		form.setBackground(BG);
		form.setBorder(new EmptyBorder(6, 4, 4, 4));

		txtLineName = styledTextField();
		cbLineStatus = new JComboBox<>(LineStatus.values());
		cbLineStatus.setMaximumSize(new Dimension(9999, 30));
		cbLineStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

		btnAddLine = styledButton("Them tuyen", GREEN);
		btnUpdateLine = styledButton("Cap nhat", BLUE);
		btnRemoveLine = styledButton("Xoa tuyen", RED_DARK);

		btnAddLine.addActionListener(e -> controller.handleAction("ADD_LINE", txtLineName.getText()));

		btnUpdateLine.addActionListener(e -> controller.handleAction("UPDATE_LINE", selectedLine, txtLineName.getText(),
				(LineStatus) cbLineStatus.getSelectedItem()));

		btnRemoveLine.addActionListener(e -> {
			if (selectedLine == null) {
				showError("Chua chon tuyen!");
				return;
			}
			int c = JOptionPane.showConfirmDialog(frame, "Xac nhan xoa tuyen: " + selectedLine.getLineName() + "?",
					"Xac nhan", JOptionPane.YES_NO_OPTION);
			if (c == JOptionPane.YES_OPTION)
				controller.handleAction("REMOVE_LINE", selectedLine);
		});

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		btnRow.setBackground(BG);
		btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnRow.add(btnAddLine);
		btnRow.add(btnUpdateLine);
		btnRow.add(btnRemoveLine);

		form.add(styledLabel("Ten tuyen:"));
		form.add(Box.createVerticalStrut(3));
		form.add(txtLineName);
		form.add(Box.createVerticalStrut(6));
		form.add(styledLabel("Trang thai:"));
		form.add(Box.createVerticalStrut(3));
		form.add(cbLineStatus);
		form.add(Box.createVerticalStrut(8));
		form.add(btnRow);

		p.add(form, BorderLayout.SOUTH);
		return p;
	}

	// --- Panel phai: Danh sach ga ---
	private JPanel buildStationPanel() {
		JPanel p = new JPanel(new BorderLayout(0, 8));
		p.setBackground(BG);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BLUE, 1),
				"Danh sach Ga (theo tuyen da chon)", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("Arial", Font.BOLD, 13), BLUE));

		// Bang ga: STT | Ma ga | Ten ga | Suc chua | Ty le
		stationTableModel = new DefaultTableModel(new String[] { "STT", "Ma ga", "Ten ga", "Suc chua", "Hien tai" },
				0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		stationTable = new JTable(stationTableModel);
		styleTable(stationTable);
		stationTable.getColumnModel().getColumn(0).setPreferredWidth(35);
		stationTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		stationTable.getColumnModel().getColumn(2).setPreferredWidth(140);
		stationTable.getColumnModel().getColumn(3).setPreferredWidth(65);
		stationTable.getColumnModel().getColumn(4).setPreferredWidth(60);

		// Click chon ga -> dien vao form
		stationTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				onStationSelected();
		});

		p.add(new JScrollPane(stationTable), BorderLayout.CENTER);

		// Form nhap ga
		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		form.setBackground(BG);
		form.setBorder(new EmptyBorder(6, 4, 4, 4));

		txtStationName = styledTextField();
		txtStationCapacity = styledTextField();

		btnAddStation = styledButton("Them ga", GREEN);
		btnUpdateStation = styledButton("Cap nhat", BLUE);
		btnRemoveStation = styledButton("Xoa ga", RED_DARK);

		btnAddStation.addActionListener(e -> {
			int cap = parseCapacity(txtStationCapacity.getText());
			controller.handleAction("ADD_STATION", txtStationName.getText(), selectedLine, cap);
		});

		btnUpdateStation.addActionListener(e -> {
			if (selectedStation == null) {
				showError("Chua chon ga!");
				return;
			}
			int cap = parseCapacity(txtStationCapacity.getText());
			controller.handleAction("UPDATE_STATION", selectedStation, txtStationName.getText(), cap);
		});

		btnRemoveStation.addActionListener(e -> {
			if (selectedStation == null) {
				showError("Chua chon ga!");
				return;
			}
			int c = JOptionPane.showConfirmDialog(frame, "Xac nhan xoa ga: " + selectedStation.getStationName() + "?",
					"Xac nhan", JOptionPane.YES_NO_OPTION);
			if (c == JOptionPane.YES_OPTION)
				controller.handleAction("REMOVE_STATION", selectedStation, selectedLine);
		});

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		btnRow.setBackground(BG);
		btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnRow.add(btnAddStation);
		btnRow.add(btnUpdateStation);
		btnRow.add(btnRemoveStation);

		form.add(styledLabel("Ten ga:"));
		form.add(Box.createVerticalStrut(3));
		form.add(txtStationName);
		form.add(Box.createVerticalStrut(6));
		form.add(styledLabel("Suc chua:"));
		form.add(Box.createVerticalStrut(3));
		form.add(txtStationCapacity);
		form.add(Box.createVerticalStrut(8));
		form.add(btnRow);

		p.add(form, BorderLayout.SOUTH);
		return p;
	}

	// =========================================================
	// Tab 2: Cau hinh gia ve
	// =========================================================
	private JPanel buildTabFare() {
		JPanel outer = new JPanel();
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
		outer.setBackground(BG);
		outer.setBorder(new EmptyBorder(20, 40, 20, 40));

		// --- Gia co ban ---
		JPanel farePanel = new JPanel(new GridLayout(0, 2, 10, 6));
        farePanel.setBackground(BG);
        farePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        farePanel.setMaximumSize(new Dimension(520, 999));
        farePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BLUE, 1), "Gia ve co ban",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 13), BLUE));
 
        FareConfig cfg = FareConfig.getInstance();
 
        txtBaseFare    = styledTextField(); txtBaseFare.setText(String.valueOf((int) cfg.getBaseFare()));
        txtPerStop     = styledTextField(); txtPerStop.setText(String.valueOf((int) cfg.getFarePerStop()));
        // NEW: daily and monthly price fields
        txtDailyPrice  = styledTextField(); txtDailyPrice.setText(String.valueOf((int) cfg.getFixedPriceDaily()));
        txtMonthlyPrice= styledTextField(); txtMonthlyPrice.setText(String.valueOf((int) cfg.getFixedPriceMonthly()));
 
        btnSetFare  = styledButton("Cap nhat gia", BLUE);
        lblFareResult = new JLabel(" ");
        lblFareResult.setFont(new Font("Arial", Font.BOLD, 12));
 
        btnSetFare.addActionListener(e -> {
            try {
                double base     = Double.parseDouble(txtBaseFare.getText().trim());
                double perStop  = Double.parseDouble(txtPerStop.getText().trim());
                double daily    = Double.parseDouble(txtDailyPrice.getText().trim());
                double monthly  = Double.parseDouble(txtMonthlyPrice.getText().trim());
 
                // Pass all 4 values to controller
                controller.handleAction("SET_FARE", base, perStop, daily, monthly);
 
                lblFareResult.setForeground(new Color(0, 130, 0));
                lblFareResult.setText("Cap nhat thanh cong!");
            } catch (NumberFormatException ex) {
                showError("Gia phai la so hop le!");
            }
        });
 
        farePanel.add(styledLabel("Gia co ban (VND):"));     farePanel.add(txtBaseFare);
        farePanel.add(styledLabel("Gia moi tram (VND):"));   farePanel.add(txtPerStop);
        farePanel.add(styledLabel("Gia ve ngay (VND):"));    farePanel.add(txtDailyPrice);
        farePanel.add(styledLabel("Gia ve thang (VND):"));   farePanel.add(txtMonthlyPrice);
        farePanel.add(btnSetFare);                            farePanel.add(lblFareResult);
 
		// --- Bang chiet khau ---
		JPanel discPanel = new JPanel(new GridLayout(0, 2, 10, 6));
		discPanel.setBackground(BG);
		discPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		discPanel.setMaximumSize(new Dimension(520, 999));
		discPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BLUE, 1),
				"He so chiet khau (0.0 - 1.0)", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 13),
				BLUE));

		txtNormalRate = styledTextField();
		txtNormalRate.setText(String.valueOf(cfg.getDiscount(PassengerType.NORMAL)));
		txtStudentRate = styledTextField();
		txtStudentRate.setText(String.valueOf(cfg.getDiscount(PassengerType.STUDENT)));
		txtSeniorRate = styledTextField();
		txtSeniorRate.setText(String.valueOf(cfg.getDiscount(PassengerType.SENIOR)));
		txtDisableRate = styledTextField();
		txtDisableRate.setText(String.valueOf(cfg.getDiscount(PassengerType.DISABLE)));
		btnUpdateDiscounts = styledButton("Cap nhat chiet khau", BLUE);

		btnUpdateDiscounts.addActionListener(e -> {
			try {
				Map<PassengerType, Double> map = new HashMap<>();
				map.put(PassengerType.NORMAL, Double.parseDouble(txtNormalRate.getText().trim()));
				map.put(PassengerType.STUDENT, Double.parseDouble(txtStudentRate.getText().trim()));
				map.put(PassengerType.SENIOR, Double.parseDouble(txtSeniorRate.getText().trim()));
				map.put(PassengerType.DISABLE, Double.parseDouble(txtDisableRate.getText().trim()));
				controller.handleAction("UPDATE_DISCOUNTS", map);
			} catch (NumberFormatException ex) {
				showError("He so phai la so hop le!");
			}
		});

		discPanel.add(styledLabel("Khach thuong (NORMAL):"));
		discPanel.add(txtNormalRate);
		discPanel.add(styledLabel("Sinh vien (STUDENT):"));
		discPanel.add(txtStudentRate);
		discPanel.add(styledLabel("Nguoi cao tuoi (SENIOR):"));
		discPanel.add(txtSeniorRate);
		discPanel.add(styledLabel("Nguoi khuyet tat (DISABLE):"));
		discPanel.add(txtDisableRate);
		discPanel.add(btnUpdateDiscounts);
		discPanel.add(new JLabel());

		outer.add(farePanel);
		outer.add(Box.createVerticalStrut(16));
		outer.add(discPanel);
		return outer;
	}

	// =========================================================
	// Tab 3: Bao cao
	// =========================================================
	private JPanel buildTabReport() {
		JPanel p = new JPanel(new BorderLayout(0, 10));
		p.setBackground(BG);
		p.setBorder(new EmptyBorder(16, 30, 16, 30));

		// Top toolbar
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		toolbar.setBackground(BG);
		txtDateRange = styledTextField();
		txtDateRange.setPreferredSize(new Dimension(150, 30));
		txtDateRange.setText("2026-06");

		btnRevenue = styledButton("Doanh thu theo loai ve", BLUE);
		btnHeatmap = styledButton("Bao cao HeatMap", new Color(100, 60, 160));

		btnRevenue.addActionListener(e -> controller.handleAction("REVENUE_REPORT", txtDateRange.getText()));
		btnHeatmap.addActionListener(e -> controller.handleAction("HEATMAP_REPORT"));

		toolbar.add(styledLabel("Thoi gian:"));
		toolbar.add(txtDateRange);
		toolbar.add(btnRevenue);
		toolbar.add(btnHeatmap);

		taReport = new JTextArea();
		taReport.setEditable(false);
		taReport.setFont(new Font("Monospaced", Font.PLAIN, 13));
		taReport.setBackground(new Color(240, 244, 255));
		taReport.setBorder(new EmptyBorder(8, 8, 8, 8));

		p.add(toolbar, BorderLayout.NORTH);
		p.add(new JScrollPane(taReport), BorderLayout.CENTER);
		return p;
	}

	// =========================================================
	// Event handlers noi bo
	// =========================================================
	private void onLineSelected() {
		int row = lineTable.getSelectedRow();
		if (row < 0) {
			selectedLine = null;
			return;
		}

		// Tim doi tuong MetroLine tu controller
		String lineId = (String) lineTableModel.getValueAt(row, 1);
		// Lay lai tu controller
		if (controller instanceof controller.AdminController ac) {
			selectedLine = ac.getMetroLines().stream().filter(l -> l.getLineId().equals(lineId)).findFirst()
					.orElse(null);
		}
		if (selectedLine != null) {
			txtLineName.setText(selectedLine.getLineName());
			cbLineStatus.setSelectedItem(selectedLine.getStatus());
			controller.handleAction("SELECT_LINE", selectedLine);
		}
	}

	private void onStationSelected() {
		int row = stationTable.getSelectedRow();
		if (row < 0) {
			selectedStation = null;
			return;
		}
		if (selectedLine == null)
			return;

		String stId = (String) stationTableModel.getValueAt(row, 1);
		selectedStation = selectedLine.getStations().stream().filter(s -> s.getStationId().equals(stId)).findFirst()
				.orElse(null);

		if (selectedStation != null) {
			txtStationName.setText(selectedStation.getStationName());
			txtStationCapacity.setText(String.valueOf(selectedStation.getCapacity()));
		}
	}

	// =========================================================
	// Public methods goi tu Controller
	// =========================================================
	public void loadLines(List<MetroLine> lines) {
		SwingUtilities.invokeLater(() -> {
			lineTableModel.setRowCount(0);
			int i = 1;
			for (MetroLine ml : lines) {
				lineTableModel.addRow(new Object[] { i++, ml.getLineId(), ml.getLineName(), ml.getStatus() });
			}
		});
	}

	public void loadStations(List<Station> stations, MetroLine ownerLine) {
		SwingUtilities.invokeLater(() -> {
			stationTableModel.setRowCount(0);
			int i = 1;
			for (Station s : stations) {
				stationTableModel.addRow(new Object[] { i++, s.getStationId(), s.getStationName(), s.getCapacity(),
						s.getCheckInCount() });
			}
		});
	}

	public void clearStations() {
		SwingUtilities.invokeLater(() -> stationTableModel.setRowCount(0));
	}

	public void showRevenueReport(Map<TicketType, Double> report) {
		SwingUtilities.invokeLater(() -> {
			if (report == null || report.isEmpty()) {
				taReport.setText("Khong co du lieu doanh thu.");
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("===== BAO CAO DOANH THU =====\n");
			sb.append(String.format("%-20s | %s\n", "Loai ve", "Doanh thu (VND)"));
			sb.append("--------------------------------\n");
			double total = 0;
			for (Map.Entry<TicketType, Double> entry : report.entrySet()) {
				sb.append(String.format("%-20s | %,.0f\n", entry.getKey(), entry.getValue()));
				total += entry.getValue();
			}
			sb.append("--------------------------------\n");
			sb.append(String.format("%-20s | %,.0f\n", "TONG CONG", total));
			sb.append("================================\n");
			taReport.setText(sb.toString());
		});
	}

	public void showHeatmapReport(List<HeatmapAlert> report) {
		SwingUtilities.invokeLater(() -> {
			if (report == null || report.isEmpty()) {
				taReport.setText("Chua co canh bao HeatMap nao.");
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("===== BAO CAO HEATMAP =====\n\n");
			for (HeatmapAlert a : report) {
				sb.append(a.toString()).append("\n");
			}
			sb.append("\nTong canh bao: ").append(report.size());
			taReport.setText(sb.toString());
		});
	}

	public void showError(String msg) {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, msg, "Loi", JOptionPane.ERROR_MESSAGE));
	}

	public void showInfo(String msg) {
		SwingUtilities.invokeLater(
				() -> JOptionPane.showMessageDialog(frame, msg, "Thong bao", JOptionPane.INFORMATION_MESSAGE));
	}

	public void show() {
		SwingUtilities.invokeLater(() -> frame.setVisible(true));
	}

	// =========================================================
	// Ho tro
	// =========================================================
	private int parseCapacity(String s) {
		try {
			return Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private void styleTable(JTable table) {
		table.setFont(new Font("Arial", Font.PLAIN, 12));
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		table.getTableHeader().setBackground(BLUE);
		table.getTableHeader().setForeground(WHITE);
		table.setRowHeight(24);
		table.setSelectionBackground(new Color(190, 210, 255));
		table.setGridColor(new Color(210, 215, 225));
		table.setShowGrid(true);
	}

	private JLabel styledLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(new Font("Arial", Font.PLAIN, 12));
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		return l;
	}

	private JTextField styledTextField() {
		JTextField tf = new JTextField();
		tf.setFont(new Font("Arial", Font.PLAIN, 12));
		tf.setMaximumSize(new Dimension(9999, 30));
		tf.setAlignmentX(Component.LEFT_ALIGNMENT);
		return tf;
	}

	private JButton styledButton(String text, Color bg) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("Arial", Font.BOLD, 12));
		btn.setBackground(bg);
		btn.setForeground(WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(new EmptyBorder(6, 14, 6, 14));
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Color lighter = bg.brighter();
		btn.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(lighter);
			}

			public void mouseExited(MouseEvent e) {
				btn.setBackground(bg);
			}
		});
		return btn;
	}
}
