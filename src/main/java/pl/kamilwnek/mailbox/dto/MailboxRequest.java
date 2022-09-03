package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class MailboxRequest {

    private final boolean newMail;
    private final boolean attemptedDeliveryNoticePresent;
    private final Double battery;
    private final Double temperature;
    private final Double pressure;
    private final Double humidity;
    private final Double newMailDistance;
}
