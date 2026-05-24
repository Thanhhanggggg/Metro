package Metro;

import java.time.LocalDate;

public class DayPass extends Ticket {

    private LocalDate validDate;
    private int ridesUsed;
    private boolean unlimitedRides;

    public DayPass(
            String ticketId,
            Passenger passenger
    ) {

        super(
                ticketId,
                TicketType.DAILY,
                0,
                passenger
        );

        this.validDate = LocalDate.now();

        this.ridesUsed = 0;

        this.unlimitedRides = true;

        this.price =
                calcPrice(TicketType.DAILY);
    }

    @Override
    public boolean isValid() {

        return LocalDate.now()
                .equals(validDate)
                && getState().isValid();
    }

    @Override
    public double calcPrice(
            TicketType type
    ) {

        return FareConfig.getInstance()
                .getFixedPriceDaily();
    }

    public void incrementRide() {

        ridesUsed++;
    }
}
