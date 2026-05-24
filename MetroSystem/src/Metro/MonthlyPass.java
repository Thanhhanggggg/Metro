package Metro;

import java.time.LocalDate;
import java.util.List;

public class MonthlyPass extends Ticket {

    private List<MetroLine> validLines;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private int rideCount;
    private int maxRides;

    public MonthlyPass(
            String ticketId,
            double price,
            Passenger passenger,
            List<MetroLine> validLines
    ) {

        super(
                ticketId,
                TicketType.MONTHLY,
                price,
                passenger
        );

        this.validLines = validLines;

        this.validFrom = LocalDate.now();

        this.validUntil =
                validFrom.plusMonths(1);

        this.rideCount = 0;

        this.maxRides = 60;
    }

    @Override
    public boolean isValid() {

        return LocalDate.now()
                .isBefore(validUntil)
                && rideCount < maxRides
                && getState().isValid();
    }

    @Override
    public double calcPrice(
            TicketType type
    ) {

        return FareConfig.MONTHLY_PASS_PRICE;
    }

    public double getRemainingRides() {

        return maxRides - rideCount;
    }

    public boolean coverLine(
            MetroLine line
    ) {

        return validLines.contains(line);
    }
}
