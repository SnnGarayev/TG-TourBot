package az.code.telegrambot.controllers.redis.redis;

import az.code.telegrambot.entity.RedisChat;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Map;

//@Repository
@Component
public class RedisRepo implements RedisInter{
    private final String hashReference= "redis";

//    private final RedisTemplate redisTemplate;

    @Resource(name = "template")
    private HashOperations<String, Long, RedisChat> hashOperations;

    @Override
    public RedisChat saveRedis(RedisChat red) {
        hashOperations.putIfAbsent(hashReference,red.getId(),red);
        return red;
    }

    //Camalin  dediyi methodlar
    @Override
    public RedisChat getRedis(Long chatId) {
        return  hashOperations.get(hashReference,chatId);
    }

    public RedisChat saveRedisWithChatId(RedisChat red,Long chatId) {
        red.setId(chatId);
        hashOperations.put(hashReference,red.getId(),red);
        return red;
    }


    @Override
    public void updateRedis(RedisChat red) {
        hashOperations.put(hashReference, red.getId(), red);
    }

   @Override
   public Map<Long, RedisChat> getAllRedis() {//Long,RedisChat
       return hashOperations.entries(hashReference);
    }

    @Override
    public void deleteRedis(Long id) {
        hashOperations.delete(hashReference,id);
    }


    @Override
    public void saveAllRedis(Map<Long, RedisChat> map) {
        hashOperations.putAll(hashReference,map);
    }



}
