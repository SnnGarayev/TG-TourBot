package az.code.telegrambot.repo;

import az.code.telegrambot.entity.Question;
import az.code.telegrambot.entity.QuestionType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionTypeRepoMain {

    private final EntityManager em;

    @Cacheable("allQuestionTypes")
    public List<QuestionType> getQuestionTypes(){
        return em.createQuery("SELECT a from QuestionType a").getResultList();
    }


    public QuestionType getQuestionTypeById(Long id){
        return em.find(QuestionType.class, id);
    }

}
