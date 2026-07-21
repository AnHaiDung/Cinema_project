package com.example.cinema.service;

import com.example.cinema.model.Auditorium;
import com.example.cinema.repository.AuditoriumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditoriumService {

    private final AuditoriumRepository auditoriumRepository;

    public List<Auditorium> getAllAuditoriums() {
        return auditoriumRepository.findAll();
    }

    public Optional<Auditorium> getAuditoriumById(Long id) {
        return auditoriumRepository.findById(id);
    }

    public Auditorium saveAuditorium(Auditorium auditorium) {
        if (auditorium.getId() == null) {
            if (auditoriumRepository.existsByName(auditorium.getName())) {
                throw new IllegalArgumentException("Phòng chiếu '" + auditorium.getName() + "' đã tồn tại.");
            }
        } else {
            if (auditoriumRepository.existsByNameAndIdNot(auditorium.getName(), auditorium.getId())) {
                throw new IllegalArgumentException("Phòng chiếu '" + auditorium.getName() + "' đã tồn tại.");
            }
        }
        return auditoriumRepository.save(auditorium);
    }

    public void deleteAuditorium(Long id) {
        auditoriumRepository.deleteById(id);
    }
}
