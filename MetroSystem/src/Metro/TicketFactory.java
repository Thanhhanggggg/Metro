package Metro;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketFactory {

    public Ticket factoryMethod(
            Passenger pass,
            TicketType type,
            int stops,
            Station origin,
            Station destination,
            MetroLine line
    ) {

        String ticketId =
                generateTicketId();

        switch (type) {

            case SINGLE:

                return new SingleTrip(
                        ticketId,
                        pass,
                        origin,
                        destination,
                        line,
                        stops
                );

            case DAILY:

                return new DayPass(
                        ticketId,
                        pass
                );

            case MONTHLY:

                List<MetroLine> lines =
                        new ArrayList<>();

                lines.add(line);

                return new MonthlyPass(
                        ticketId,
                        pass,
                        lines
                );

            default:

                throw new IllegalArgumentException(
                        "Invalid ticket type"
                );
        }
    }

    private String generateTicketId() {

        return "TICKET-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8);
    }
}
