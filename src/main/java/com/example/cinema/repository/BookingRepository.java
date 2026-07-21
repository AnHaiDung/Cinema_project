package com.example.cinema.repository;

import com.example.cinema.model.Booking;
import com.example.cinema.model.Showtime;
import com.example.cinema.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByShowtime(Showtime showtime);
    List<Booking> findByUserOrderByBookingDateDesc(User user);
}
