package com.example.cinema.controller.admin;

import com.example.cinema.model.*;
import com.example.cinema.service.AuditoriumService;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final AuditoriumService auditoriumService;

    @GetMapping
    public String listShowtimes(@RequestParam(value = "date", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                @RequestParam(value = "movieId", required = false) Long movieId,
                                @RequestParam(value = "auditoriumId", required = false) Long auditoriumId,
                                Model model) {
        LocalDate searchDate = (date != null) ? date : LocalDate.now();
        Movie movieFilter = (movieId != null) ? movieService.getMovieById(movieId).orElse(null) : null;
        Auditorium auditoriumFilter = (auditoriumId != null) ? auditoriumService.getAuditoriumById(auditoriumId).orElse(null) : null;

        List<Showtime> showtimes = showtimeService.getShowtimesFiltered(searchDate, movieFilter, auditoriumFilter);
        List<Movie> movies = movieService.getAllMovies();
        List<Auditorium> auditoriums = auditoriumService.getAllAuditoriums();

        model.addAttribute("showtimes", showtimes);
        model.addAttribute("movies", movies);
        model.addAttribute("auditoriums", auditoriums);
        model.addAttribute("searchDate", searchDate);
        model.addAttribute("selectedMovieId", movieId);
        model.addAttribute("selectedAuditoriumId", auditoriumId);
        model.addAttribute("statuses", ShowtimeStatus.values());
        model.addAttribute("showtime", new Showtime());
        return "admin/showtimes";
    }

    @GetMapping("/new")
    public String newShowtimeForm(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("auditoriums", auditoriumService.getAllAuditoriums());
        model.addAttribute("statuses", ShowtimeStatus.values());
        model.addAttribute("showtime", new Showtime());
        model.addAttribute("showtimes", showtimeService.getAllShowtimes());
        model.addAttribute("searchDate", LocalDate.now());
        return "admin/showtimes";
    }

    @PostMapping("/save")
    public String saveShowtime(@RequestParam("movieId") Long movieId,
                                @RequestParam("auditoriumId") Long auditoriumId,
                                @RequestParam("startsAt") String startsAt,
                                @RequestParam("ticketPrice") Double ticketPrice,
                                @RequestParam("status") ShowtimeStatus status,
                                @RequestParam(value = "note", required = false) String note,
                                RedirectAttributes redirectAttributes) {
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        Auditorium auditorium = auditoriumService.getAuditoriumById(auditoriumId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .auditorium(auditorium)
                .startsAt(java.time.LocalDateTime.parse(startsAt))
                .ticketPrice(ticketPrice)
                .status(status)
                .note(note)
                .build();

        try {
            showtimeService.saveShowtime(showtime);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu lịch chiếu thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/showtimes";
    }

    @GetMapping("/edit/{id}")
    public String editShowtimeForm(@PathVariable Long id, Model model) {
        Showtime showtime = showtimeService.getShowtimeById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu"));
        model.addAttribute("showtime", showtime);
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("auditoriums", auditoriumService.getAllAuditoriums());
        model.addAttribute("statuses", ShowtimeStatus.values());
        model.addAttribute("showtimes", showtimeService.getAllShowtimes());
        model.addAttribute("searchDate", LocalDate.now());
        model.addAttribute("isEdit", true);
        return "admin/showtimes";
    }

    @PostMapping("/edit/{id}")
    public String updateShowtime(@PathVariable Long id,
                                  @RequestParam("movieId") Long movieId,
                                  @RequestParam("auditoriumId") Long auditoriumId,
                                  @RequestParam("startsAt") String startsAt,
                                  @RequestParam("ticketPrice") Double ticketPrice,
                                  @RequestParam("status") ShowtimeStatus status,
                                  @RequestParam(value = "note", required = false) String note,
                                  RedirectAttributes redirectAttributes) {
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        Auditorium auditorium = auditoriumService.getAuditoriumById(auditoriumId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));

        Showtime showtime = Showtime.builder()
                .id(id)
                .movie(movie)
                .auditorium(auditorium)
                .startsAt(java.time.LocalDateTime.parse(startsAt))
                .ticketPrice(ticketPrice)
                .status(status)
                .note(note)
                .build();

        try {
            showtimeService.saveShowtime(showtime);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật lịch chiếu thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/showtimes";
    }

    @PostMapping("/delete/{id}")
    public String deleteShowtime(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        showtimeService.deleteShowtime(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa lịch chiếu thành công.");
        return "redirect:/showtimes";
    }
}
