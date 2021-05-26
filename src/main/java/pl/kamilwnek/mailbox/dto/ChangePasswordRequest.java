package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
}
