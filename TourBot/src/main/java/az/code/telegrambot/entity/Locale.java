package az.code.telegrambot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locales")
public class Locale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "locale",fetch = FetchType.EAGER)
    private List<LocaleTranslations> localeTranslations;


}
