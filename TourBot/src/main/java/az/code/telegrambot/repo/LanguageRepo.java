package az.code.telegrambot.repo;

import az.code.telegrambot.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepo extends JpaRepository<Language,Long> {

    List<Language> findAll();

}
