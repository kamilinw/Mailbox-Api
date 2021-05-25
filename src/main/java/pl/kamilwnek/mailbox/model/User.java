package pl.kamilwnek.mailbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.kamilwnek.mailbox.security.ApplicationUserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_accounts")
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq"
    )
    private Long userId;

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String email;

    private String token;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ApplicationUserRole applicationUserRole;

    @NotNull
    private boolean isEnabled;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "user_mailbox",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "mailbox_id"))
    @JsonIgnore
    private Set<Mailbox> mailboxes;


    public User(String username, String password, String email, ApplicationUserRole applicationUserRole) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.applicationUserRole = applicationUserRole;
        isEnabled  = false;
    }
    public User(String username, String password, Mailbox mailbox, ApplicationUserRole applicationUserRole) {
        this.username = username;
        this.password = password;
        this.mailboxes = Set.of(mailbox);
        this.applicationUserRole = applicationUserRole;
         isEnabled = true;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return applicationUserRole.getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }


    public void addMailbox(Mailbox mailbox){
        if (mailboxes.isEmpty()){
            this.mailboxes = Set.of(mailbox);
            return;
        }
        mailboxes.add(mailbox);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
