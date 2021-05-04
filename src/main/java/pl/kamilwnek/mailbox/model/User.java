package pl.kamilwnek.mailbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.kamilwnek.mailbox.security.ApplicationUserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

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
    private List<Mailbox> mailboxes;


    public User() {
    }
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
        this.mailboxes = List.of(mailbox);
        this.applicationUserRole = applicationUserRole;
         isEnabled = true;
    }



    public List<Mailbox> getMailboxes() {
        return mailboxes;
    }

    public void setMailboxes(List<Mailbox> mailboxes) {
        this.mailboxes = mailboxes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getUsername() {
        return username;
    }



    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return applicationUserRole.getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ApplicationUserRole getApplicationUserRole() {
        return applicationUserRole;
    }

    public void setApplicationUserRole(ApplicationUserRole applicationUserRole) {
        this.applicationUserRole = applicationUserRole;
    }

    public void addMailbox(Mailbox mailbox){
        if (mailboxes.isEmpty()){
            this.mailboxes = List.of(mailbox);
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

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}
