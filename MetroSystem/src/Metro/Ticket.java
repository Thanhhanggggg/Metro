package Metro;

import java.time.LocalDateTime;

public class Ticket {

    // Mã vé
    private String ticketId;

    // Trạng thái vé (State Pattern)
    private TicketState state;

    // Thời gian tạo vé
    private LocalDateTime createdTime;

    // Thời gian hết hạn
    private LocalDateTime expiredTime;

    // Giá vé
    private double price;

    // Loại hành khách
    private PassengerType passengerType;

    // Ga bắt đầu
    private Station fromStation;

    // Ga đích
    private Station destinationStation;

    // QR Code vé
    private String qrCode;

    // Constructor
    public Ticket(
            String ticketId,
            double price,
            PassengerType passengerType,
            Station fromStation,
            Station destinationStation
    ) {

        this.ticketId = ticketId;

        this.price = price;

        this.passengerType = passengerType;

        this.fromStation = fromStation;

        this.destinationStation =
                destinationStation;

        // mặc định vé mới là Active
        this.state = new ActiveState();

        // thời gian tạo
        this.createdTime =
                LocalDateTime.now();

        // ví dụ: vé hết hạn sau 1 ngày
        this.expiredTime =
                createdTime.plusDays(1);

        // tạo QR giả lập
        this.qrCode =
                "QR-" + ticketId;
    }

    // Check-in
    public void checkIn() {

        if(state.isValid()) {

            state.handle(this);

        } else {

            System.out.println(
                    "Ticket invalid for check-in!"
            );
        }
    }

    // Check-out
    public void checkOut() {

        if(state instanceof UsedState) {

            state.handle(this);

        } else {

            System.out.println(
                    "Ticket invalid for check-out!"
            );
        }
    }

    // Hoàn vé
    public void refund() {

        if(state.canRefund()) {

            setState(
                    new RefundedState()
            );

            System.out.println(
                    "Refund successful!"
            );

        } else {

            System.out.println(
                    "Ticket cannot be refunded!"
            );
        }
    }

    // Kiểm tra hết hạn
    public boolean checkExpiry() {

        return LocalDateTime.now()
                .isBefore(expiredTime);
    }

    // Hiển thị thông tin vé
    public void displayTicketInfo() {

        System.out.println(
                "===== TICKET INFO ====="
        );

        System.out.println(
                "Ticket ID: " + ticketId
        );

        System.out.println(
                "Passenger Type: "
                + passengerType
        );

        System.out.println(
                "Price: " + price
        );

        System.out.println(
                "From: "
                + fromStation
        );

        System.out.println("Destination: "
                + destinationStation
        );

        System.out.println(
                "State: "
                + state.getClass()
                        .getSimpleName()
        );

        System.out.println(
                "Created Time: "
                + createdTime
        );

        System.out.println(
                "Expired Time: "
                + expiredTime
        );
    }

    // Getter
    public String getTicketId() {
        return ticketId;
    }

    public TicketState getState() {
        return state;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public double getPrice() {
        return price;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public Station getDestinationStation() {
        return destinationStation;
    }

    public String getQrCode() {
        return qrCode;
    }

    // Setter
    public void setState(
            TicketState state
    ) {

        this.state = state;
    }

    public void setExpiredTime(
            LocalDateTime expiredTime
    ) {

        this.expiredTime = expiredTime;
    }
}