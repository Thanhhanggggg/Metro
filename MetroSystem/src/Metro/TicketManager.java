package Metro;

import java.util.*;

public class TicketManager {
    // Singleton instance
    private static TicketManager uniqueInstance;
    // Attributes
    private Map<String, Ticket> tickets;
    // Key: ticketId, Value: ten giai doan (vd: "Truoc cap nhat", "Sau cap nhat lan 1")
    private Map<String, String> ticketEraMap;

    // [THEM MOI] - Nhan era dang hoat dong, ap dung cho ve se duoc tao tiep theo
    private String currentEra;
    // Private constructor
    private TicketManager() {
        tickets = new HashMap<>();
        ticketEraMap = new LinkedHashMap<>();
        // [THEM MOI] - Era mac dinh khi chua cap nhat gia lan nao
        currentEra   = "Gia goc (truoc cap nhat)";
    }
    
    
    //
    public void markPriceEra(String eraLabel) {
        this.currentEra = eraLabel;
        System.out.println("[TicketManager] Chuyen sang era moi: " + eraLabel);
    }
    public String getCurrentEra() {
        return currentEra;
    }
    // Singleton method
    public static TicketManager getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new TicketManager();
        }
        return uniqueInstance;
    }
  
    // Issue ticket
    public Ticket issueTicket(Passenger pass, TicketType type, int stops) {
        Ticket ticket = TicketFactory.factoryMethod(pass, type, stops);
        if (ticket != null) {
            tickets.put(ticket.getTicketId(), ticket);
            ticketEraMap.put(ticket.getTicketId(), currentEra);
        }
        return ticket;
    }
    public boolean refundTicket(Ticket ticket) {
        if (!ticket.canRefund()) {
            return false;
        }

        double amount = ticket.refund();

        ticket.setStatus(TicketStatus.REFUNDED);

        return true;
    }
    // Find ticket by ID
    public Ticket findById(String id) {
        return tickets.get(id);
    }
    // Check refund condition
    public boolean canRefund(Ticket ticket) {
        return ticket != null && ticket.canRefund();
    }
    // Find affected tickets
    // Find affected tickets
//    public List<Ticket> findAffectedTickets(String gateId) {
//        List<Ticket> result = new ArrayList<>();
//        for (Ticket t : tickets.values()) {
//            result.add(t);
//        }
//        return result;
//    }
    public List<Ticket> findAffectedTickets(String dateId) {

        // Hiện tại mọi vé đều được xem là affected
        return new ArrayList<>(tickets.values());
    }
    // Revenue report
 public Map<TicketType, Double> getRevenueReport(String dateRange) {
        Map<TicketType, Double> report = new HashMap<>();
        for (Ticket t : tickets.values()) {
            TicketType type = t.getType();
            report.put(type, report.getOrDefault(type, 0.0) + t.getPrice());
        }
        return report;
    }
    // Confirm refund
    public void confirmRefund(Ticket ticket) {

        if (ticket != null) {
            System.out.println("Refund confirmed for ticket: "+ ticket.getTicketId());
        }
    }
    public void saveTicket(Ticket ticket) {

        if(ticket != null) {

            tickets.put(
                ticket.getTicketId(),
                ticket
            );
            ticketEraMap.putIfAbsent(ticket.getTicketId(), currentEra);
        }
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
}
    public Map<String, Map<TicketType, double[]>> getRevenueReportByEra() {
        // Cau truc: era -> (ticketType -> [soVe, tongGiaGoc, tongGiaHienTai])
        Map<String, Map<TicketType, double[]>> result = new LinkedHashMap<>();
 
        for (Map.Entry<String, Ticket> entry : tickets.entrySet()) {
            String ticketId = entry.getKey();
            Ticket ticket   = entry.getValue();
 
            String era = ticketEraMap.getOrDefault(ticketId, "Khong xac dinh");
 
            // Tao nhom era neu chua co
            result.putIfAbsent(era, new LinkedHashMap<>());
            Map<TicketType, double[]> eraMap = result.get(era);
 
            TicketType type = ticket.getType();
            // [soVe=0, tongGiaGoc=1, tongGiaHienTai=2]
            eraMap.putIfAbsent(type, new double[]{0, 0, 0});
            double[] stats = eraMap.get(type);
 
            stats[0] += 1;                          // so ve
            stats[1] += ticket.getPrice();          // gia goc (dong bang luc mua)
            stats[2] += ticket.calcPrice(type);     // gia hien tai (theo FareConfig moi nhat)
        }
 
        return result;

    }
}
