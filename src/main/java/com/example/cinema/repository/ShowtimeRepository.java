package com.example.cinema.repository;

import com.example.cinema.model.Auditorium;
import com.example.cinema.model.Movie;
import com.example.cinema.model.Showtime;
import com.example.cinema.model.ShowtimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    
    @Query("SELECT s FROM Showtime s WHERE s.startsAt >= :start AND s.startsAt <= :end " +
           "AND (:movie IS NULL OR s.movie = :movie) " +
           "AND (:auditorium IS NULL OR s.auditorium = :auditorium) " +
           "ORDER BY s.startsAt ASC")
    List<Showtime> findShowtimes(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("movie") Movie movie,
                                 @Param("auditorium") Auditorium auditorium);

    List<Showtime> findByMovieAndStatusOrderByStartsAtAsc(Movie movie, ShowtimeStatus status);
}
