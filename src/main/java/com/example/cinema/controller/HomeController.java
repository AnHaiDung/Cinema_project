package com.example.cinema.controller;

import com.example.cinema.model.Booking;
import com.example.cinema.model.Movie;
import com.example.cinema.model.Showtime;
import com.example.cinema.model.User;
import com.example.cinema.service.BookingService;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ShowtimeService;
import com.example.cinema.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/showtimes";
        }
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        return "home";
    }

    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        List<Showtime> showtimes = showtimeService.getActiveShowtimesForMovie(movie);
        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        return "movie_details";
    }

    @GetMapping("/booking/{showtimeId}")
    public String showBookingPage(@PathVariable Long showtimeId, Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        Set<String> occupiedSeats = bookingService.getOccupiedSeatsForShowtime(showtime);

        model.addAttribute("showtime", showtime);
        model.addAttribute("occupiedSeats", occupiedSeats);
        return "booking";
    }

    @PostMapping("/booking/{showtimeId}/confirm")
    public String confirmBooking(@PathVariable Long showtimeId,
                                  @RequestParam(value = "seats", required = false) String seatsParam,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (seatsParam == null || seatsParam.isBlank()) {
            model.addAttribute("showtime", showtime);
            model.addAttribute("occupiedSeats", bookingService.getOccupiedSeatsForShowtime(showtime));
            model.addAttribute("errorMessage", "Vui lòng chọn ít nhất một ghế.");
            return "booking";
        }

        List<String> selectedSeats = Arrays.stream(seatsParam.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        try {
            Booking booking = bookingService.createBooking(user, showtime, selectedSeats);
            model.addAttribute("booking", booking);
            model.addAttribute("showtime", showtime);
            return "booking_confirmation";
        } catch (IllegalArgumentException e) {
            model.addAttribute("showtime", showtime);
            model.addAttribute("occupiedSeats", bookingService.getOccupiedSeatsForShowtime(showtime));
            model.addAttribute("errorMessage", e.getMessage());
            return "booking";
        }
    }

    @GetMapping("/my-bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        List<Booking> bookings = bookingService.getBookingsByUser(user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);
        return "my_bookings";
    }
}
