package ru.yandex.practicum.compilation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
