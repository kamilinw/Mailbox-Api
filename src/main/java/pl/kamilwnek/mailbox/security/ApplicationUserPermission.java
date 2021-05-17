package pl.kamilwnek.mailbox.security;

public enum ApplicationUserPermission {
    MAILBOX_READ("mailbox:read"),
    MAILBOX_WRITE("mailbox:write"),
    USER_READ("user:read"),
    USER_WRITE("user:write");

    private final String permission;

    ApplicationUserPermission(String permission){
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
