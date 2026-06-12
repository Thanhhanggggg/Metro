package controller;

import java.util.List;

import Metro.*;
import view.PassengerView;

public class PassengerController implements IController {

    private Passenger passenger;
    private PassengerView view;

    public PassengerController(Passenger passenger, PassengerView view) {
        this.passenger = passenger;
        this.view = view;
    }
    @Override
    public void handleAction(String action, Object... params) {
        switch (action) {
        //UC01 - TÌM LỘ TRÌNH
        case "SEARCH_ROUTE":
            handleSearchRoute((Station) params[0],(Station) params[1]);
            break;
        // UC02 - Mua vé
        case "BUY_TICKET":
            handleBuyTicket((Station) params[0],(Station) params[1],(TicketType) params[2]);
            break;
        //UC03 - TRA CỨU GIÁ VÉ
        case "CALCULATE_FARE":
            handleCalculateFare((Station) params[0],(Station) params[1],(TicketType) params[2]);
            break;
        // UC04 - Xem vé đã mua
        case "VIEW_TICKETS":
            handleViewTickets();
            break;
        // UC06 - Hoàn vé
        case "REFUND":
            handleRefund((String) params[0]);
            break;
        //HUỶ VÉ
        case "CANCEL_TICKET" : handleCancelTicket((String) params[0]);
            break;
        default:
            System.out.println("Action không hợp lệ: " + action);
        }
    }
    @Override
    public boolean validate(Object input) {
        if (input == null)
            return false;
        if (input instanceof String s)
            return !s.trim().isEmpty();
        return true;
    }
 // ==================================================
 // UC01 - TÌM LỘ TRÌNH
 // Chức năng:
 // - Chọn ga đi
 // - Chọn ga đến
 // - Hiển thị các ga trên đường đi
 // ==================================================
 private void handleSearchRoute(Station from,Station to) {
     if(from == null || to == null) {
         view.showRouteResult("Vui lòng chọn ga.");
         return;
     }
     MetroLine line = from.getMetroLine();
     if(line == null) {
         view.showRouteResult("Không tìm thấy tuyến.");
         return;
     }
     List<Station> route = line.findRoute(from,to);
     if(route == null) {
         view.showRouteResult("Không tìm thấy lộ trình.");
         return;
     }
     StringBuilder sb = new StringBuilder();
     sb.append("LỘ TRÌNH\n\n");
     for(Station s : route) {
         sb.append("➜ ").append(s.getStationName()).append("\n");
     }
     view.showRouteResult(sb.toString());
 }
    // ==================================================
    // UC02 - MUA VÉ
    // Chức năng:
    // - Chọn ga đi
    // - Chọn ga đến
    // - Chọn loại vé
    // - Tính số trạm
    // - Phát hành vé
    // ==================================================
    private void handleBuyTicket(Station from,Station to,TicketType type) {
        if (from == null || to == null) {
            view.showBuyResult("Vui lòng chọn ga đi và ga đến.");
            return;
        }
        MetroLine line = from.getMetroLine();
        if (line == null) {
            view.showBuyResult("Không tìm thấy tuyến metro.");
            return;
        }
        int stops = line.getStopCount(from, to);
        if (stops < 0) {
            view.showBuyResult("Không tìm được lộ trình.");
            return;
        }
        Ticket ticket = TicketManager.getInstance().issueTicket(passenger,type,stops);
        if (ticket == null) {
            view.showBuyResult("Mua vé thất bại.");
            return;
        }
        passenger.getTickets().add(ticket);
        StringBuilder result = new StringBuilder();

        result.append("MUA VÉ THÀNH CÔNG\n\n");
        result.append("Mã vé: ").append(ticket.getTicketId()).append("\n");

        result.append("Loại vé: ").append(ticket.getType()).append("\n");
        result.append("Số trạm: ").append(stops).append("\n");

        result.append("Giá vé: ").append(String.format("%,.0f",ticket.getPrice())).append(" VNĐ");

        view.showBuyResult(result.toString());
    }
    // ==================================================
    // UC03 - TRA CỨU GIÁ VÉ
    // Chức năng:
    // - Chọn ga đi
    // - Chọn ga đến
    // - Chọn loại vé
    // - Tính giá tham khảo
    // ==================================================
    private void handleCalculateFare(Station from,Station to,TicketType type) {
        if(from == null || to == null) {
            view.showFareResult("Vui lòng chọn ga");
            return;
        }
        MetroLine line = from.getMetroLine();

        if(line == null) {
            view.showFareResult("Không tìm thấy tuyến");
            return;
        }
        double fare = line.listFare(from,to,type);

        int stops = line.getStopCount(from,to);
        String result = "LOẠI VÉ : " + type + "\nSỐ TRẠM : " + stops + "\nGIÁ THAM KHẢO : " + String.format("%,.0f", fare) + " VNĐ";
        view.showFareResult(result);
    }
    // ==================================================
    // UC04 - XEM DANH SÁCH VÉ
    // Chức năng:
    // - Hiển thị toàn bộ vé hành khách đang sở hữu
    // ==================================================
    private void handleViewTickets() {
        List<Ticket> tickets = passenger.getTickets();
        view.showMyTickets(tickets);
    }
   
    //HUỶ VÉ
        private void handleCancelTicket(String ticketId) {
        if (!validate(ticketId)) {view.showCancelResult("Vui lòng nhập mã vé");
         return;
     }
        Ticket ticket = TicketManager.getInstance().findById(ticketId);
        if (ticket == null) {
          view.showCancelResult("Không tìm thấy vé");
         return;
     }
        if (!ticket.canRefund()) {
          view.showCancelResult("Vé không đủ điều kiện hoàn");
         return;
     }
        TicketState before = ticket.getState();
        double refundAmount = ticket.refund();
        TicketState after = ticket.getState();
          if (before != after) {
              view.showCancelResult("Hủy vé thành công\n"+ "Số tiền hoàn: " + String.format("%,.0f", refundAmount) + " VNĐ");
          } else {
              view.showCancelResult("Không thể hủy vé");
     }
 }
    // ==================================================
    // UC06 - HOÀN VÉ
    // Chức năng:
    // - Tìm vé theo mã
    // - Kiểm tra điều kiện hoàn
    // - Thực hiện hoàn tiền
    // ==================================================
    private void handleRefund(
            String ticketId) {
        if (!validate(ticketId)) {
            view.showBuyResult("Vui lòng nhập mã vé");
            return;
        }
        Ticket ticket = TicketManager.getInstance().findById(ticketId);
        if (ticket == null) {
            view.showBuyResult("Không tìm thấy vé");
            return;
        }
        boolean success = TicketManager.getInstance().refundTicket(ticket);
        if (success) {
            view.showBuyResult("Hoàn vé thành công");
        } else {
            view.showBuyResult( "Không đủ điều kiện hoàn vé");
        }
    }
    // ==================================================
    // Khởi tạo dữ liệu cho View
    // ==================================================
    public void loadData(
            List<MetroLine> lines,
            List<Station> stations) {
        view.setLines(lines);
        view.setStations(stations);
    }
    
}
