package pl.kamilwnek.mailbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class SubscribeEmail {

    @Id
    @SequenceGenerator(
            name = "email_seq",
            sequenceName = "email_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "email_seq"
    )
    private Long subscribeEmailId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Email
    private String email;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "mailbox_subscribe_email",
            joinColumns = @JoinColumn(name = "subscribe_email_id"),
            inverseJoinColumns = @JoinColumn(name = "mailbox_id"))
    @JsonIgnore
    private Set<Mailbox> mailboxes;

}
