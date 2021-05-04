package pl.kamilwnek.mailbox.dto;

public class MailboxRequest {

    private final boolean newMail;
    private final boolean attemptedDeliveryNoticePresent;
    private final Double battery;
    private final Double temperature;
    private final Double pressure;
    private final Double humidity;

    public MailboxRequest(boolean newMail, boolean attemptedDeliveryNoticePresent, Double battery, Double temperature, Double pressure, Double humidity) {
        this.newMail = newMail;
        this.attemptedDeliveryNoticePresent = attemptedDeliveryNoticePresent;
        this.battery = battery;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public boolean isNewMail() {
        return newMail;
    }

    public boolean isAttemptedDeliveryNoticePresent() {
        return attemptedDeliveryNoticePresent;
    }

    public Double getBattery() {
        return battery;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getHumidity() {
        return humidity;
    }
}
