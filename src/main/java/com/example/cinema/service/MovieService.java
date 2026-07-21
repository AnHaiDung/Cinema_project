package com.example.cinema.service;

import com.example.cinema.model.Movie;
import com.example.cinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private static final String UPLOAD_DIR = "uploads/movies/";

    public List<Movie> getAllMovies() {
        return movieRepository.findAllByOrderByTitleAsc();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Movie saveMovie(Movie movie, MultipartFile posterFile) throws IOException {
        if (posterFile != null && !posterFile.isEmpty()) {
            String posterUrl = savePoster(posterFile);
            movie.setPosterUrl(posterUrl);
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie updatedMovie, MultipartFile posterFile) throws IOException {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + id));
        
        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setDurationMinutes(updatedMovie.getDurationMinutes());
        existingMovie.setAgeRating(updatedMovie.getAgeRating());
        existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
        existingMovie.setDescription(updatedMovie.getDescription());

        if (posterFile != null && !posterFile.isEmpty()) {
            String posterUrl = savePoster(posterFile);
            existingMovie.setPosterUrl(posterUrl);
        } else if (updatedMovie.getPosterUrl() != null) {
            existingMovie.setPosterUrl(updatedMovie.getPosterUrl());
        }

        return movieRepository.save(existingMovie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    private String savePoster(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return resource URL mapped in MVC Config
        return "/uploads/movies/" + uniqueFilename;
    }
}
