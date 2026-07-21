package com.example.cinema.config;

import com.example.cinema.model.*;
import com.example.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // ---- Users ----
        User admin = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Quản Trị Viên")
                .email("admin@cinema.vn")
                .role("ROLE_ADMIN")
                .build());

        userRepository.save(User.builder()
                .username("customer")
                .password(passwordEncoder.encode("customer123"))
                .fullName("Khách Hàng Demo")
                .email("customer@cinema.vn")
                .role("ROLE_CUSTOMER")
                .build());

        // ---- Auditoriums ----
        Auditorium roomA = auditoriumRepository.save(Auditorium.builder()
                .name("Phòng A - IMAX")
                .capacity(80)
                .screenType("IMAX")
                .build());

        Auditorium roomB = auditoriumRepository.save(Auditorium.builder()
                .name("Phòng B - 3D")
                .capacity(60)
                .screenType("3D")
                .build());

        Auditorium roomC = auditoriumRepository.save(Auditorium.builder()
                .name("Phòng C - 2D")
                .capacity(50)
                .screenType("2D")
                .build());

        // ---- Movies ----
        Movie movie1 = movieRepository.save(Movie.builder()
                .title("Avengers: Secret Wars")
                .genre("Hành động, Khoa học viễn tưởng")
                .durationMinutes(150)
                .ageRating("T13")
                .releaseDate(LocalDate.of(2026, 5, 1))
                .posterUrl("https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?w=400&h=600&fit=crop")
                .description("Các siêu anh hùng tập hợp để đối mặt với mối đe dọa vũ trụ lớn nhất từ trước đến nay trong cuộc chiến xác định số phận của toàn bộ đa vũ trụ.")
                .build());

        Movie movie2 = movieRepository.save(Movie.builder()
                .title("Vùng Tối: Hành Trình Cuối")
                .genre("Kinh dị, Hồi hộp")
                .durationMinutes(115)
                .ageRating("T16")
                .releaseDate(LocalDate.of(2026, 6, 15))
                .posterUrl("https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400&h=600&fit=crop")
                .description("Một nhóm thám hiểm bị mắc kẹt trong khu rừng bí ẩn nơi bóng tối che giấu những sinh vật kinh hoàng đang săn đuổi họ từng bước một.")
                .build());

        Movie movie3 = movieRepository.save(Movie.builder()
                .title("Tình Yêu Dưới Mưa")
                .genre("Tình cảm, Lãng mạn")
                .durationMinutes(100)
                .ageRating("P")
                .releaseDate(LocalDate.of(2026, 7, 1))
                .posterUrl("https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?w=400&h=600&fit=crop")
                .description("Câu chuyện tình yêu cảm động giữa hai con người gặp nhau trong một đêm mưa tầm tã và cùng nhau tìm lại ý nghĩa cuộc sống.")
                .build());

        Movie movie4 = movieRepository.save(Movie.builder()
                .title("Robot Tương Lai 2099")
                .genre("Hoạt hình, Phiêu lưu")
                .durationMinutes(95)
                .ageRating("P")
                .releaseDate(LocalDate.of(2026, 7, 10))
                .posterUrl("https://images.unsplash.com/photo-1485846234645-a62644f84728?w=400&h=600&fit=crop")
                .description("Chú robot nhỏ R7 trong hành trình tìm kiếm gia đình đã gặp gỡ những người bạn đặc biệt và khám phá ý nghĩa của tình bạn chân thật.")
                .build());

        // ---- Showtimes (today and tomorrow) ----
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        showtimeRepository.save(Showtime.builder()
                .movie(movie1).auditorium(roomA)
                .startsAt(today.atTime(9, 30))
                .ticketPrice(120000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie1).auditorium(roomA)
                .startsAt(today.atTime(14, 0))
                .ticketPrice(120000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie1).auditorium(roomB)
                .startsAt(today.atTime(18, 30))
                .ticketPrice(100000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie2).auditorium(roomB)
                .startsAt(today.atTime(10, 0))
                .ticketPrice(100000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie2).auditorium(roomC)
                .startsAt(today.atTime(15, 30))
                .ticketPrice(80000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie3).auditorium(roomC)
                .startsAt(today.atTime(20, 0))
                .ticketPrice(80000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie4).auditorium(roomC)
                .startsAt(today.atTime(8, 30))
                .ticketPrice(70000.0).status(ShowtimeStatus.SELLING).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie1).auditorium(roomA)
                .startsAt(tomorrow.atTime(10, 0))
                .ticketPrice(120000.0).status(ShowtimeStatus.SCHEDULED).build());

        showtimeRepository.save(Showtime.builder()
                .movie(movie3).auditorium(roomB)
                .startsAt(tomorrow.atTime(14, 30))
                .ticketPrice(100000.0).status(ShowtimeStatus.SCHEDULED).build());

        System.out.println("=== Data Initializer: Đã khởi tạo dữ liệu mẫu thành công! ===");
        System.out.println("Admin: admin / admin123");
        System.out.println("Khách hàng: customer / customer123");
    }
}
