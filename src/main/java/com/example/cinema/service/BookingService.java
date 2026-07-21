package com.example.cinema.service;

import com.example.cinema.model.Booking;
import com.example.cinema.model.Showtime;
import com.example.cinema.model.User;
import com.example.cinema.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public List<Booking> getBookingsByShowtime(Showtime showtime) {
        return bookingRepository.findByShowtime(showtime);
    }

    public Set<String> getOccupiedSeatsForShowtime(Showtime showtime) {
        List<Booking> bookings = bookingRepository.findByShowtime(showtime);
        Set<String> occupied = new HashSet<>();
        for (Booking booking : bookings) {
            if (booking.getSeats() != null && !booking.getSeats().isBlank()) {
                String[] seats = booking.getSeats().split(",");
                for (String seat : seats) {
                    occupied.add(seat.trim());
                }
            }
        }
        return occupied;
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    @Transactional
    public Booking createBooking(User user, Showtime showtime, List<String> selectedSeats) {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một ghế.");
        }

        // Check if any seat is already occupied
        Set<String> occupied = getOccupiedSeatsForShowtime(showtime);
        for (String seat : selectedSeats) {
            if (occupied.contains(seat)) {
                throw new IllegalArgumentException("Ghế " + seat + " đã có người đặt trước.");
            }
        }

        // Create booking
        String seatsString = String.join(", ", selectedSeats);
        Double totalPrice = showtime.getTicketPrice() * selectedSeats.size();

        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .seats(seatsString)
                .totalPrice(totalPrice)
                .bookingDate(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }
}
