package az.code.telegrambot.entity;

import az.code.telegrambot.entity.operatorSide.MainSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Session{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "sessionId",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SessionReply> userReplies;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private MainSession mainSession;

}
