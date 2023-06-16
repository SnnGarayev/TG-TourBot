package az.code.telegrambot.repo;

import az.code.telegrambot.entity.operatorSide.MainSession;
import az.code.telegrambot.entity.operatorSide.OperatorAnswer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MainSessionRepo {

    private final EntityManager em;

    @Transactional
    public MainSession saveMainSession(MainSession ms){
        em.merge(ms);
        return ms;
    }

    public OperatorAnswer getOperatorAnswer(Long answerId){
        return em.find(OperatorAnswer.class, answerId);
    }

    @Transactional
    public void saveOperatorAnswer(OperatorAnswer offer){
        em.merge(offer);
    }

    @Transactional
    public void blockMainSession(Long chatId){

////        String queryString = "UPDATE main_session SET stage '-1' where id = " + chatId;
//
////        String test = String.valueOf(em.createQuery("select e.id from MainSession e where e.session.user.chatId = '752688757'").getResultList());
//
//        String qs = "UPDATE MainSession m set m.stage=-1 where m.session.user.chatId = '" + chatId.intValue() + "'";
//
//        Query q = em.createQuery("UPDATE MainSession m set m.stage=-1 where m.session.user.chatId = '752688757'");
//
////        Query q2 = em.createQuery(qs);
//
//        q.executeUpdate();

        List<MainSession> ms = em.createQuery("select e from MainSession e where e.session.user.chatId = '" + chatId.toString() + "'").getResultList();

        List<MainSession> updatedSession = new ArrayList<>();

        ms.forEach(a-> {
           if (a.getStage().intValue() >=0){
               MainSession msTemp = a;
               msTemp.setStage(-1);
               updatedSession.add(msTemp);
           }
        });

        updatedSession.forEach(em::merge);

    }


    public List<MainSession> getMainSessionsOfUser(Long chatId){

        List<MainSession> ms = em.createQuery("select e from MainSession e where e.session.user.chatId = '" + chatId.toString() + "' and e.stage>=0").getResultList();


        return ms;

    }


}
