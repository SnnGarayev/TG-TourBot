package az.code.telegrambot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -9200917243921539204L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private String chatId;
    private String telegramUserId;
    private String phoneNumber;
    private String userName;
    @OneToMany(mappedBy = "id",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Session> userSessions;
}
