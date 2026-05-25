package Metro;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketFactory {
 public static Ticket factoryMethod(Passenger pass, TicketType type, int stops ) {
        String ticketId = generateTicketId();
        switch (type) {
            case SINGLE:
                return new SingleTrip(ticketId, pass, stops);
            case DAILY:
                return new DayPass(ticketId, pass);
            case MONTHLY:
                List<MetroLine> lines = new ArrayList<>();
                return new MonthlyPass(ticketId, pass, lines);
            default:
                throw new IllegalArgumentException("Invalid ticket type");
        }
    }
    private static String generateTicketId() {
        return "TICKET-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
