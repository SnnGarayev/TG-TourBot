package az.code.telegrambot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("redis")

 public class RedisChat implements Serializable{


   @Serial
   private static final long serialVersionUID = -9200917243921539204L;

    private Long id;

    private Language language;

    private Long stage;

    private User user;

    private String[] answerList;

}
