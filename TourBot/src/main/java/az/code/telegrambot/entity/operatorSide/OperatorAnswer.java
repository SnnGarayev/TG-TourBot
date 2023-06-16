package az.code.telegrambot.entity.operatorSide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OperatorAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "operatorId")
    private OperatorUser operatorId;

    private String answer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "answerId")
    private MainSession mainSession;

    /**
     * status 1 accepted
     * status -1 declined

     */
    private Integer status;
}
