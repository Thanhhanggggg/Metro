package main;

import Metro.*;
import view.SmartGateView;
import javax.swing.*;

/**
 * ============================================================
 *  SmartGateMain – Lớp Main để test giao diện SmartGate
 * ============================================================
 *  Tạo dữ liệu mẫu (vé, hành khách) → đưa vào TicketManager
 *  → khởi chạy SmartGateView
 *
 *  Dữ liệu mẫu:
 *  ┌─────────┬────────────────┬─────────────────┬──────────────────┐
 *  │ Ticket  │  Hành khách    │  Loại vé        │  Trạng thái      │
 *  ├─────────┼────────────────┼─────────────────┼──────────────────┤
 *  │ T001    │ Nguyen Van A   │ Single Trip      │ ActiveState ✅   │
 *  │ T002    │ Tran Thi B     │ Single Trip      │ ActiveState ✅   │
 *  │ T003    │ Le Van C       │ Single Trip      │ UsedState  🔵    │
 *  │ T004    │ Pham Thi D     │ Single Trip      │ ExpiredState ❌  │
 *  │ T005    │ Hoang Van E    │ Single Trip      │ RefundedState ❌ │
 *  └─────────┴────────────────┴─────────────────┴──────────────────┘
 *
 *  Test cases gợi ý:
 *  - Check-in  : nhập T001 hoặc T002 → thành công
 *  - Check-in  : nhập T003/T004/T005 → thất bại
 *  - Check-out : nhập T003 → thành công (đang ở UsedState)
 *  - Check-out : nhập T001 → thất bại (chưa check-in)
 *  - Xác thực  : chọn G001 (IN) + nhập T002 → hợp lệ
 *  - Xác thực  : chọn G003 (OUT) + nhập T004 → không hợp lệ
 *  - Báo cáo sự cố: chọn G002 + nhập mô tả → cổng bị disable
 * ============================================================
 */
public class SmartGateMain {

    public static void main(String[] args) {

        // ── 1. Tạo dữ liệu mẫu ──────────────────────────────
        System.out.println("==============================================");
        System.out.println("  SMARTGATE SYSTEM – Khởi tạo dữ liệu mẫu  ");
        System.out.println("==============================================");

        // Hành khách
        Passenger p1 = new Passenger("P001", "Nguyen Van A",  PassengerType.NORMAL, "ID001", 200000);
        Passenger p2 = new Passenger("P002", "Tran Thi B",    PassengerType.STUDENT, "ID001", 100000);
        Passenger p3 = new Passenger("P003", "Le Van C",      PassengerType.NORMAL, "ID003", 50000);
        Passenger p4 = new Passenger("P004", "Pham Thi D",    PassengerType.SENIOR, "ID004", 100000);
        Passenger p5 = new Passenger("P005", "Hoang Van E",   PassengerType.NORMAL, "ID005", 75000);

        // Vé – dùng SingleTrip (concrete subclass của Ticket)
        // T001, T002: ActiveState – có thể check-in
        Ticket t1 = new SingleTrip("T001", p1, 3);
        Ticket t2 = new SingleTrip("T002", p2, 2);

        // T003: giả lập UsedState – đã check-in, chờ check-out
        Ticket t3 = new SingleTrip("T003", p3, 4);
        t3.setState(new UsedState());

        // T004: giả lập ExpiredState
        Ticket t4 = new SingleTrip("T004", p4, 1);
        t4.setState(new ExpiredState());

        // T005: giả lập RefundedState
        Ticket t5 = new SingleTrip("T005", p5, 5);
        t5.setState(new RefundedState());

        // ── 2. Đưa vé vào TicketManager (Singleton) ─────────
        TicketManager tm = TicketManager.getInstance();
        tm.saveTicket(t1);
        tm.saveTicket(t2);
        tm.saveTicket(t3);
        tm.saveTicket(t4);
        tm.saveTicket(t5);

        // ── 3. In bảng trạng thái ra console ─────────────────
        System.out.println("\n[TicketManager] Danh sách vé đã khởi tạo:");
        System.out.println("─────────────────────────────────────────────────");
        System.out.printf("%-8s %-16s %-14s %-20s%n",
                "Vé ID", "Hành khách", "Giá (VND)", "Trạng thái");
        System.out.println("─────────────────────────────────────────────────");
        for (Ticket t : new Ticket[]{t1, t2, t3, t4, t5}) {
            System.out.printf("%-8s %-16s %-14.0f %-20s%n",
                    t.getTicketId(),
                    t.getPassenger().getName(),
                    t.getPrice(),
                    t.getState().getClass().getSimpleName());
        }
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("\n[SmartGateMain] Khởi chạy giao diện...\n");

        // ── 4. Khởi chạy giao diện Swing ─────────────────────
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(SmartGateView::new);
    }
}