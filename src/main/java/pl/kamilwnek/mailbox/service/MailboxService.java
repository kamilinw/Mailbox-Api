package pl.kamilwnek.mailbox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kamilwnek.mailbox.dto.MailboxRequest;
import pl.kamilwnek.mailbox.exception.IdNotFoundException;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.repository.MailboxRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MailboxService {
    private final UserService userService;
    private final MailboxRepository mailboxRepository;

    @Autowired
    public MailboxService(UserService userService, MailboxRepository mailboxRepository) {
        this.userService = userService;
        this.mailboxRepository = mailboxRepository;
    }



    public Mailbox updateAll(Long id, MailboxRequest mailboxUpdate) {
        var mailbox = mailboxRepository.findById(id).orElse(null);

        if (mailbox == null){
            return null;
        }

        if (!mailbox.isNewMail() && mailboxUpdate.isNewMail()){
            List<LocalDateTime> history = mailbox.getMailHistory();
            history.add(LocalDateTime.now());
            mailbox.setMailHistory(history);
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
        var mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getTemperature();
    }

    public Double getPressure(String username, Long id) {
        var mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getPressure();
    }

    public Double getHumidity(String username, Long id) {
        var mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getHumidity();
    }

    public Double getBattery(String username, Long id) {
        var mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getBattery();
    }

    public List<LocalDateTime> getHistory(String username, Long id) {
        var mailbox = getMailboxById(username, id);
        return mailbox == null ? null : mailbox.getMailHistory();
    }

    public boolean isNewMail(String username, Long id) {
        var mailbox = getMailboxById(username, id);
        if (mailbox == null){
            throw new IdNotFoundException("Mailbox with this id not found");
        }
        return mailbox.isNewMail();
    }

    public boolean isNotice(String username, Long id) {
        var mailbox = getMailboxById(username, id);
        if (mailbox == null){
            throw new IdNotFoundException("Mailbox with this id not found");
        }
        return mailbox.isAttemptedDeliveryNoticePresent();
    }



    public Mailbox getMailboxById(String username, Long id) {
        var mailbox = mailboxRepository.findById(id).orElse(null);
        if (mailbox == null){
            return null;
        }
        var currentUser = userService.findUserByUsername(username);
        List<User> mailboxUsers = mailbox.getUsers();

        for (User mailboxUser: mailboxUsers) {
            if (mailboxUser.getUserId().equals(currentUser.getUserId()))
                return mailbox;
        }
        return null;
    }

    public Mailbox getFirstMailbox(String username) {
        var currentUser = userService.findUserByUsername(username);
        var mailboxes = currentUser.getMailboxes();

        return mailboxes.isEmpty() ? null : mailboxes.get(0);
    }
}
