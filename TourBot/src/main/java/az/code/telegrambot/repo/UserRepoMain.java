package az.code.telegrambot.repo;


import az.code.telegrambot.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Transient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepoMain {

        private final EntityManager em;

        public User getUserByIdAndChatId(Long id, Long chatId){

            User u = null;
            List<User> ul = em.createQuery("select u from User u where u.chatId = :chatId ", User.class) // u.telegramUserId = :userId and
//                  .setParameter("userId", id.toString())
                  .setParameter("chatId", chatId.toString())
                    .getResultList();

            if (ul.size()>0){
                u = ul.get(0);
            }

            return u;
        }

        @Transactional
        public User saveUser(User u){
            em.merge(u);
            return u;
        }

        @Transactional
        public void addNumberToUser(Long userId, Long chatId, String number){
            User u = getUserByIdAndChatId(chatId, userId);
            u.setPhoneNumber(number);

            em.merge(u);
        }
}
