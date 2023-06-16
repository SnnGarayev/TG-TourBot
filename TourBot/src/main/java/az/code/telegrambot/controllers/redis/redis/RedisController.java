package az.code.telegrambot.controllers.redis.redis;

import az.code.telegrambot.entity.*;
import jakarta.annotation.PostConstruct;
import org.glassfish.grizzly.nio.transport.UDPNIOServerConnection;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("/redis")
public class RedisController {

    private final RedisInter redisTemplate;

    public RedisController(RedisInter redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init(){

        List<Session> session=List.of(Session.builder().id(1l).build(),
                Session.builder().id(1l).build());
        User user=User.builder().telegramUserId("78964").chatId("45").userSessions(session).phoneNumber("0559786541").id(4l).build();
        Language l=Language.builder().language("Azerbaycan dili").buttonText("aze").id(1l).build();
        List<LocaleTranslations> translations=List.of(LocaleTranslations.builder().id(1l).language(l).build(),
                LocaleTranslations.builder().id(2l).language(l).build());
        Locale local=Locale.builder().id(2l).localeTranslations(translations).build();
        List<QuestionsAnswer> answer=List.of(QuestionsAnswer.builder().id(4l).context("Salamlasma").locale(local).build(),
                QuestionsAnswer.builder().id(5l).context("Sagollasma").locale(local).build());
//        RedisChat redis=RedisChat.builder().id(1l).stage(2l).language(l).user(user).answerList(answer).build();
        RedisChat redis=RedisChat.builder().id(1l).stage(2l).language(l).user(user).answerList(new String[3]).build();

        redisTemplate.saveRedis(redis);

    }


//    @GetMapping("/{id}")
//    public ResponseEntity<RedisChat> getById(@PathVariable Long id){
//        return ResponseEntity.ok(redis.getOneRedis(id));
//    }
//    @PostMapping()
//    public ResponseEntity<RedisChat> save(@RequestBody RedisChat redisChat){
//        return ResponseEntity.ok(redis.saveRedis(redisChat));
//    }
//
//
//    @GetMapping()//Long,RedisChat
//    public Map<Object,Object> getAll(){
//        return redis.getAllRedis();
//    }
//    @PutMapping
//    public void update(@RequestBody RedisChat redisChat){
//        redis.updateRedis(redisChat);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable Long id){
//        redis.deleteRedis(id);
//    }

}
