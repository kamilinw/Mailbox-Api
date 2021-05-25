package pl.kamilwnek.mailbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.kamilwnek.mailbox.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername (String username);
    User findByUsername (String username);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.isEnabled = TRUE WHERE a.username = ?1")
    int enableAppUser(String username);

    @Query("SELECT u FROM User u " +
            "WHERE u.email = ?1")
    User findByEmail(String email);
}
