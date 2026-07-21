package com.example.cinema.controller.admin;

import com.example.cinema.model.Auditorium;
import com.example.cinema.service.AuditoriumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auditoriums")
@RequiredArgsConstructor
public class AuditoriumController {

    private final AuditoriumService auditoriumService;

    @GetMapping
    public String listAuditoriums(Model model) {
        List<Auditorium> auditoriums = auditoriumService.getAllAuditoriums();
        model.addAttribute("auditoriums", auditoriums);
        model.addAttribute("auditorium", new Auditorium());
        return "admin/auditoriums";
    }

    @PostMapping("/save")
    public String saveAuditorium(@ModelAttribute Auditorium auditorium,
                                  RedirectAttributes redirectAttributes) {
        try {
            auditoriumService.saveAuditorium(auditorium);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu phòng chiếu thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auditoriums";
    }

    @GetMapping("/edit/{id}")
    public String editAuditoriumForm(@PathVariable Long id, Model model) {
        Auditorium auditorium = auditoriumService.getAuditoriumById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        List<Auditorium> auditoriums = auditoriumService.getAllAuditoriums();
        model.addAttribute("auditoriums", auditoriums);
        model.addAttribute("auditorium", auditorium);
        return "admin/auditoriums";
    }

    @PostMapping("/edit/{id}")
    public String updateAuditorium(@PathVariable Long id,
                                    @ModelAttribute Auditorium auditorium,
                                    RedirectAttributes redirectAttributes) {
        auditorium.setId(id);
        try {
            auditoriumService.saveAuditorium(auditorium);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật phòng chiếu thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auditoriums";
    }

    @PostMapping("/delete/{id}")
    public String deleteAuditorium(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        auditoriumService.deleteAuditorium(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phòng chiếu thành công.");
        return "redirect:/auditoriums";
    }
}
