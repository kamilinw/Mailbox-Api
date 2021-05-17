package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kamilwnek.mailbox.model.ConfirmationToken;
import pl.kamilwnek.mailbox.repository.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
