package az.code.telegrambot.repo;

import az.code.telegrambot.entity.Session;
import az.code.telegrambot.entity.SessionReply;
import az.code.telegrambot.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionRepoMain {
    private final EntityManager em;
    @Transactional
    public Session saveSession(Session s){
        em.merge(s);
        return s;
    }

    @Transactional
    public SessionReply saveReply(SessionReply sr){
        return em.merge(sr);
    }
}
