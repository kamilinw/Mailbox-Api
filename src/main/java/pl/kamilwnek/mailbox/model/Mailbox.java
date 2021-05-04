package pl.kamilwnek.mailbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Mailbox {

    @Id
    @SequenceGenerator(
            name = "mailbox_seq",
            sequenceName = "mailbox_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "mailbox_seq"
    )
    private Long mailboxId;
    private boolean newMail;

    @ElementCollection
    @CollectionTable(name="mailbox_mail_history", joinColumns = @JoinColumn(name = "mailbox_id"))
    @Column(name = "mail_history") // 3
    private List<LocalDateTime> mailHistory;
    private boolean attemptedDeliveryNoticePresent;
    private Double battery;
    private Double temperature;
    private Double pressure;
    private Double humidity;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "user_mailbox",
            joinColumns = @JoinColumn(name = "mailbox_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private List<User> users;

    public Mailbox() {
        // empty on purpose
    }

    public Long getMailboxId() {
        return mailboxId;
    }

    public void setMailboxId(Long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public boolean isNewMail() {
        return newMail;
    }

    public void setNewMail(boolean newMail) {
        this.newMail = newMail;
    }

    public List<LocalDateTime> getMailHistory() {
        return mailHistory;
    }

    public void setMailHistory(List<LocalDateTime> mailHistory) {
        this.mailHistory = mailHistory;
    }

    public boolean isAttemptedDeliveryNoticePresent() {
        return attemptedDeliveryNoticePresent;
    }

    public void setAttemptedDeliveryNoticePresent(boolean attemptedDeliveryNoticePresent) {
        this.attemptedDeliveryNoticePresent = attemptedDeliveryNoticePresent;
    }

    public Double getBattery() {
        return battery;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
