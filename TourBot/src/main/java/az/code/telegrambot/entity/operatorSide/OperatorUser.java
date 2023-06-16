package az.code.telegrambot.entity.operatorSide;


import az.code.telegrambot.entity.QuestionsAnswer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class OperatorUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String phoneNumber;

    @OneToMany(mappedBy = "operatorId",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<OperatorAnswer> answers;
}
