package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kamilwnek.mailbox.dto.MailboxRequest;
import pl.kamilwnek.mailbox.exception.IdNotFoundException;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.repository.MailboxRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class MailboxService {
    private final UserService userService;
    private final MailboxRepository mailboxRepository;

    public Mailbox updateAll(Long id, MailboxRequest mailboxUpdate) {
        Mailbox mailbox = mailboxRepository.findById(id).orElse(null);

        if (mailbox == null){
            return null;
        }

        if (!mailbox.isNewMail() && mailboxUpdate.isNewMail()){
            mailbox.getMailHistory().add(LocalDateTime.now(ZoneOffset.UTC));
        }

        mailbox.setAttemptedDeliveryNoticePresent(mailboxUpdate.isAttemptedDeliveryNoticePresent());
        mailbox.setBattery(mailboxUpdate.getBattery());
        mailbox.setNewMail(mailboxUpdate.isNewMail());
        mailbox.setTemperature(mailboxUpdate.getTemperature());
        mailbox.setPressure(mailboxUpdate.getPressure());
        mailbox.setHumidity(mailboxUpdate.getHumidity());
        mailboxRepository.save(mailbox);
        return mailbox;
    }


    public Double getTemperature(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getTemperature();
    }

    public Double getPressure(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getPressure();
    }

    public Double getHumidity(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getHumidity();
    }

    public Double getBattery(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getBattery();
    }

    public List<LocalDateTime> getHistory(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getMailHistory();
    }

    public boolean isNewMail(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        if (mailbox == null){
            throw new IdNotFoundException("Mailbox with this id not found");
        }
        return mailbox.isNewMail();
    }

    public boolean isNotice(String username, Long id) {
        Mailbox mailbox = getMailboxById(username, id);
        if (mailbox == null){
            throw new IdNotFoundException("Mailbox with this id not found");
        }
        return mailbox.isAttemptedDeliveryNoticePresent();
    }



    public Mailbox getMailboxById(String username, Long id) {
        Mailbox mailbox = mailboxRepository.findById(id).orElse(null);
        if (mailbox == null){
            return null;
        }
        User currentUser = userService.findUserByUsername(username);
        Set<User> mailboxUsers = mailbox.getUsers();

        for (User mailboxUser: mailboxUsers) {
            if (mailboxUser.getUserId().equals(currentUser.getUserId()))
                return mailbox;
        }
        return null;
    }

    public Mailbox getFirstMailbox(String username) {
        User currentUser = userService.findUserByUsername(username);
        Set<Mailbox> mailboxes = currentUser.getMailboxes();

        return mailboxes.isEmpty() ? null : mailboxes.stream().findFirst().get();
    }
}
