package com.thailu.thailubackend.controller;

import com.thailu.thailubackend.model.Orden;
import com.thailu.thailubackend.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class SeguimientoController {

    @Autowired
    private OrdenRepository ordenRepository;

    @GetMapping("/seguimiento")
    public String seguimiento() {
        return "seguimiento";
    }

    @PostMapping("/seguimiento/buscar")
    public String buscarOrden(@RequestParam String codigo,
                              Model model,
                              RedirectAttributes redirectAttrs) {
        Optional<Orden> orden = ordenRepository.findByCodigoSeguimiento(codigo.trim().toUpperCase());

        if (orden.isPresent()) {
            model.addAttribute("orden", orden.get());
            return "seguimiento";
        } else {
            redirectAttrs.addFlashAttribute("error", "Código no encontrado. Verifica e intenta nuevamente.");
            return "redirect:/";
        }
    }
}