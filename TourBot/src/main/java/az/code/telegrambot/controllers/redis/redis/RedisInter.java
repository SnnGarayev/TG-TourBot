package az.code.telegrambot.controllers.redis.redis;//package az.code.telegrambot.controllers.redis;

import az.code.telegrambot.entity.RedisChat;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface RedisInter {

        RedisChat saveRedis(RedisChat red);
        RedisChat getRedis(Long id);
        RedisChat saveRedisWithChatId(RedisChat red,Long chatId) ;
        void updateRedis(RedisChat red);
        Map<Long, RedisChat> getAllRedis();//Long,RedisChat
        void deleteRedis(Long id);
        void saveAllRedis(Map<Long, RedisChat> map);
}
