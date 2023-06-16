package az.code.telegrambot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Data
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionTypeId")
    private QuestionType questionType;


    /**
     * Dil üzrə sual seçildikdən sonra sualın mətni burda olacaq
     * DB tablelarında context sualı başa düşmək üçündür
     */
    private String context;

    @ManyToOne(fetch = FetchType.EAGER)
    private Locale translations;
//    @OneToOne
//    @JoinColumn(name = "nextQuestionId")
//    @ToString.Exclude
//    private Question nextQuestion;


    @Column(name = "queue")
    private Integer queueN;

    @OneToMany(mappedBy = "questionId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<QuestionsAnswer> answers;

    public String toStringJ() {
        String answersI = null;

        answers.forEach(a -> {
            answersI.concat("\n" + a.getContext());
        });

        return "Question{" +
                "id=" + id +
                "\n" + answersI +
                '}';
    }
}
