package pl.kamilwnek.mailbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kamilwnek.mailbox.model.SubscribeEmail;

import java.util.Optional;

@Repository
public interface SubscribeEmailRepository  extends JpaRepository<SubscribeEmail, Long> {
    Optional<SubscribeEmail> findByEmail(String email);
}
