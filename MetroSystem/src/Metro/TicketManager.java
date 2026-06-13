package Metro;

import java.util.*;

public class TicketManager {
    private static TicketManager uniqueInstance;

    private Map<String, Ticket> tickets;
    private Map<String, String> ticketEraMap;

    // [GIU NGUYEN] Snapshot gia tai thoi diem mua
    private Map<String, FareSnapshot> ticketFareSnapshot;

    private String currentEra;

    // ==== Inner class luu snapshot gia ====
    public static class FareSnapshot {
        public final double baseFare;
        public final double farePerStop;
        public final double maxFare;
        public final double fixedPriceDaily;
        public final double fixedPriceMonthly;
        public final Map<PassengerType, Double> discountRate;
        /** Gia thuc te da thu tai thoi diem phat hanh ve */
        public final double actualPriceAtPurchase;

        public FareSnapshot(FareConfig cfg, double actualPrice) {
            this.baseFare              = cfg.getBaseFare();
            this.farePerStop           = cfg.getFarePerStop();
            this.maxFare               = cfg.getMaxFare();
            this.fixedPriceDaily       = cfg.getFixedPriceDaily();
            this.fixedPriceMonthly     = cfg.getFixedPriceMonthly();
            this.discountRate          = new HashMap<>(cfg.getDiscountRate());
            this.actualPriceAtPurchase = actualPrice;
        }
    }

    private TicketManager() {
        tickets            = new LinkedHashMap<>();
        ticketEraMap       = new LinkedHashMap<>();
        ticketFareSnapshot = new LinkedHashMap<>();
        currentEra         = "Gia goc (truoc cap nhat)";
    }

    public void markPriceEra(String eraLabel) {
        this.currentEra = eraLabel;
        System.out.println("[TicketManager] Chuyen sang era moi: " + eraLabel);
    }

    public String getCurrentEra() {
        return currentEra;
    }

    public static TicketManager getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new TicketManager();
        }
        return uniqueInstance;
    }

    // Phat hanh ve moi - luon tao snapshot
    public Ticket issueTicket(Passenger pass, TicketType type, int stops) {
        Ticket ticket = TicketFactory.factoryMethod(pass, type, stops);
        if (ticket != null) {
            tickets.put(ticket.getTicketId(), ticket);
            ticketEraMap.put(ticket.getTicketId(), currentEra);
            // [GIU NGUYEN] Snapshot gia thuc te luc phat hanh
            ticketFareSnapshot.put(ticket.getTicketId(),
                    new FareSnapshot(FareConfig.getInstance(), ticket.getPrice()));
        }
        return ticket;
    }

    public boolean refundTicket(Ticket ticket) {
        if (!ticket.canRefund()) return false;
        ticket.refund();
        ticket.setStatus(TicketStatus.REFUNDED);
        return true;
    }

    public Ticket findById(String id) {
        return tickets.get(id);
    }

    public boolean canRefund(Ticket ticket) {
        return ticket != null && ticket.canRefund();
    }

    public List<Ticket> findAffectedTickets(String dateId) {
        return new ArrayList<>(tickets.values());
    }

    public Map<TicketType, Double> getRevenueReport(String dateRange) {
        Map<TicketType, Double> report = new HashMap<>();
        for (Ticket t : tickets.values()) {
            TicketType type = t.getType();
            report.put(type, report.getOrDefault(type, 0.0) + t.getPrice());
        }
        return report;
    }

    public void confirmRefund(Ticket ticket) {
        if (ticket != null)
            System.out.println("Refund confirmed for ticket: " + ticket.getTicketId());
    }

    // [SUA] saveTicket gio cung tao snapshot - tranh null khi lay bao cao
    public void saveTicket(Ticket ticket) {
        if (ticket != null) {
            tickets.put(ticket.getTicketId(), ticket);
            ticketEraMap.putIfAbsent(ticket.getTicketId(), currentEra);
            // [SUA] Them snapshot cho ca ve duoc luu bang saveTicket()
            // Truoc day thieu doan nay nen snapshot null → giaLucMua sai
            ticketFareSnapshot.putIfAbsent(ticket.getTicketId(),
                    new FareSnapshot(FareConfig.getInstance(), ticket.getPrice()));
        }
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    /**
     * Bao cao doanh thu phan theo giai doan gia.
     *
     * stats[0] = so ve
     * stats[1] = tong gia luc mua  (snapshot tai thoi diem phat hanh - CHINH XAC)
     * stats[2] = tong gia theo FareConfig HIEN TAI (de thay chenh lech)
     */
    public Map<String, Map<TicketType, double[]>> getRevenueReportByEra() {
        Map<String, Map<TicketType, double[]>> result = new LinkedHashMap<>();
        FareConfig currentCfg = FareConfig.getInstance();

        for (Map.Entry<String, Ticket> entry : tickets.entrySet()) {
            String ticketId = entry.getKey();
            Ticket ticket   = entry.getValue();

            String era = ticketEraMap.getOrDefault(ticketId, "Khong xac dinh");

            result.putIfAbsent(era, new LinkedHashMap<>());
            Map<TicketType, double[]> eraMap = result.get(era);

            TicketType type = ticket.getType();
            eraMap.putIfAbsent(type, new double[]{0, 0, 0});
            double[] stats = eraMap.get(type);

            // [SUA] Lay gia thuc te luc mua tu snapshot
            // Neu snapshot null (truong hop cu) → fallback ve ticket.getPrice()
            FareSnapshot snap = ticketFareSnapshot.get(ticketId);
            double giaLucMua  = (snap != null) ? snap.actualPriceAtPurchase : ticket.getPrice();

            stats[0] += 1;
            stats[1] += giaLucMua;                          // gia thuc te luc mua
            stats[2] += calcCurrentPrice(ticket, currentCfg); // gia theo cfg hien tai
        }

        return result;
    }

    /** Tinh gia ve neu ap dung FareConfig hien tai */
    private double calcCurrentPrice(Ticket ticket, FareConfig cfg) {
        switch (ticket.getType()) {
            case DAILY:   return cfg.getFixedPriceDaily();
            case MONTHLY: return cfg.getFixedPriceMonthly();
            case SINGLE:  return ticket.calcPrice(ticket.getType());
            default:      return ticket.getPrice();
        }
    }

    public FareSnapshot getSnapshot(String ticketId) {
        return ticketFareSnapshot.get(ticketId);
    }
}
