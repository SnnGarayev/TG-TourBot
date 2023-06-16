package az.code.telegrambot.repo;

import az.code.telegrambot.entity.Language;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageRepoMain {

    private final EntityManager em;


    @Cacheable("languages")
    public List<Language> getLanguages(){
        return em.createQuery("select a from Language a",Language.class ).getResultList();
    }

    @Cacheable("languages")
    public Language getLanguageByButton(String buttonText){
        String queryS = "select b from Language b where b.buttonText = '" + buttonText + "'";
       Language ll = em.createQuery(queryS, Language.class)
                .getSingleResult();
        return ll;
    }
}
