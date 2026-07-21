package com.example.cinema.repository;

import com.example.cinema.model.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
    Optional<Auditorium> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
