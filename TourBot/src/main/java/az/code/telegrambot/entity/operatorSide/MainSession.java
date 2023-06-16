package az.code.telegrambot.entity.operatorSide;

import az.code.telegrambot.entity.Session;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sessionId", referencedColumnName = "id")
//    @JoinColumn(name = "session")
    private Session session;

    /**
    if stage -1 then session is inactive
     if 0 then session is ready to accept bids
     */
    private Integer stage;

    private LocalDate date;

    @OneToMany(mappedBy = "id",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<OperatorAnswer> offersList;
}
