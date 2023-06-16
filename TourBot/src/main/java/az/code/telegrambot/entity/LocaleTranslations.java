package az.code.telegrambot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "localeTranslations")
public class LocaleTranslations {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String translate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language", referencedColumnName = "id")
    private Language language;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "localeId", referencedColumnName = "id")
    private Locale locale;
}
