package az.code.telegrambot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "questionTypes")
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String typeName;


}
