package az.code.telegrambot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "languages")
public class Language implements Serializable {
    @Serial
    private static final long serialVersionUID = -9200917243921539204L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String language;

    private String buttonText; // to show user as option for example  en - English


}
