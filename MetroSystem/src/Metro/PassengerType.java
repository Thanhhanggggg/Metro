package Metro;

public enum PassengerType {

    NORMAL(1.0),
    STUDENT(0.7),
    SENIOR(0.5),
    DISABLE(0.0);

    private double rate;

    PassengerType(double rate) {

        this.rate = rate;
    }

    public double getRate() {

        return rate;
    }
}
