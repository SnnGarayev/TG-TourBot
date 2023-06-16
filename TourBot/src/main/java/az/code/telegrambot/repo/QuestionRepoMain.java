package az.code.telegrambot.repo;

import az.code.telegrambot.entity.Language;
import az.code.telegrambot.entity.Question;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionRepoMain {

    private final EntityManager em;

    public Question getQuestionById(Long id){
        return em.find(Question.class, id);
    }

    public Question getQuestionByQueue(Integer queue){
        return em.createQuery("select a from Question a where a.queueN = '" + queue.toString() + "'", Question.class)
                .getSingleResult();

    }


    @Cacheable("allQuestions")
    public List<Question> getQuestions(){
        return em.createQuery("select a from Question a",Question.class ).getResultList();
    }

    public Long getLastId(){
        return em.createQuery("SELECT id FROM Question ORDER BY id DESC LIMIT 1", Long.class).getSingleResult();
    }


}
