package pl.kamilwnek.mailbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kamilwnek.mailbox.model.Mailbox;

@Repository
public interface MailboxRepository extends JpaRepository<Mailbox, Long> {

}
