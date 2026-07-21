package com.example.cinema.controller.admin;

import com.example.cinema.model.Movie;
import com.example.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public String listMovies(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("movie", new Movie());
        return "admin/movies";
    }

    @PostMapping("/save")
    public String saveMovie(@ModelAttribute Movie movie,
                             @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                             RedirectAttributes redirectAttributes) {
        try {
            movieService.saveMovie(movie, posterFile);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu phim thành công.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải ảnh poster: " + e.getMessage());
        }
        return "redirect:/movies";
    }

    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("movie", movie);
        return "admin/movies";
    }

    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable Long id,
                               @ModelAttribute Movie movie,
                               @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                               RedirectAttributes redirectAttributes) {
        try {
            movieService.updateMovie(id, movie, posterFile);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật phim thành công.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật ảnh poster: " + e.getMessage());
        }
        return "redirect:/movies";
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        movieService.deleteMovie(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phim thành công.");
        return "redirect:/movies";
    }
}
