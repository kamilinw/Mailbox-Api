package pl.kamilwnek.mailbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
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
    private String name;
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
    @JoinTable(name = "mailbox_subscribe_email",
            joinColumns = @JoinColumn(name = "mailbox_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribe_email_id"))
    private Set<SubscribeEmail> subscribeEmails;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "user_mailbox",
            joinColumns = @JoinColumn(name = "mailbox_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private Set<User> users;

    public Mailbox(String name) {
        this.name = name;
    }

    public void addEmail(SubscribeEmail email) {
        if (subscribeEmails.isEmpty()){
            subscribeEmails = new HashSet<>();
        }
        subscribeEmails.add(email);
    }

    public void removeEmail(SubscribeEmail email) {
        if (subscribeEmails.isEmpty()){
            return;
        }
        subscribeEmails.remove(email);
    }
}
