package pl.kamilwnek.mailbox.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static pl.kamilwnek.mailbox.security.ApplicationUserPermission.*;


public enum ApplicationUserRole {
    USER(Sets.newHashSet(MAILBOX_READ, MAILBOX_WRITE, USER_READ, USER_WRITE)),
    ADMIN(Sets.newHashSet(MAILBOX_READ, MAILBOX_WRITE, USER_READ, USER_WRITE)),
    MAILBOX(Sets.newHashSet(MAILBOX_READ, MAILBOX_WRITE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return simpleGrantedAuthorities;
    }
}
