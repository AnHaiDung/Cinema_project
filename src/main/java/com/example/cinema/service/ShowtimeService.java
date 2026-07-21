package com.example.cinema.service;

import com.example.cinema.model.Auditorium;
import com.example.cinema.model.Movie;
import com.example.cinema.model.Showtime;
import com.example.cinema.model.ShowtimeStatus;
import com.example.cinema.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    public Showtime saveShowtime(Showtime showtime) {
        if (showtime.getTicketPrice() <= 0) {
            throw new IllegalArgumentException("Giá vé phải lớn hơn 0.");
        }
        return showtimeRepository.save(showtime);
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }

    public List<Showtime> getShowtimesFiltered(LocalDate date, Movie movie, Auditorium auditorium) {
        LocalDate searchDate = (date != null) ? date : LocalDate.now();
        LocalDateTime start = searchDate.atStartOfDay();
        LocalDateTime end = searchDate.atTime(LocalTime.MAX);
        
        return showtimeRepository.findShowtimes(start, end, movie, auditorium);
    }

    public List<Showtime> getActiveShowtimesForMovie(Movie movie) {
        return showtimeRepository.findByMovieAndStatusOrderByStartsAtAsc(movie, ShowtimeStatus.SELLING);
    }
}
