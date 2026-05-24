package Metro;

public class TicketFactory {
	  package Metro;

public class TicketFactory {
    public Ticket factoryMethod(
            Passenger passenger,
            TicketType type,
            int stops,
            Station origin,
            Station destination,
            MetroLine line
    ) 
    {

        double price =
                FareConfig.getInstance()
                        .calculateFare(
                                stops,
                                passenger.getPassengerType()
                        );

        switch (type) {

            case SINGLE:

                return new SingleTrip(
                        generateTicketId(),
                        price,
                        passenger,
                        origin,
                        destination,
                        line,
                        stops
                );

            case DAILY:

                return new DayPass(
                        generateTicketId(),
                        FareConfig.DAY_PASS_PRICE,
                        passenger,
                        origin,
                        destination,
                        line
                );

            case MONTHLY:

                return new MonthlyPass(
                        generateTicketId(),
                        FareConfig.MONTHLY_PASS_PRICE,
                        passenger,
                        origin,
                        destination,
                        line
                );

            default:

                throw new IllegalArgumentException(
                        "Invalid Ticket Type"
                );
        }
    }

    private String generateTicketId() {

        return "TK-" + System.currentTimeMillis();
    }
}
	
}
