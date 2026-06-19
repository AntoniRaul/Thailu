package com.thailu.thailubackend.controller;

import com.thailu.thailubackend.model.Servicio;
import com.thailu.thailubackend.repository.ServicioRepository;
import com.thailu.thailubackend.service.SupabaseStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/panel/servicios")
public class ServiciosAdminController {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private SupabaseStorageService storageService;

    private boolean noAutenticado(HttpSession session) {
        return session.getAttribute("usuarioLogueado") == null;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";
        model.addAttribute("servicios", servicioRepository.findAll());
        return "servicios-admin";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("modo", "crear");
        return "servicios-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Servicio servicio,
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        if (!imagenFile.isEmpty()) {
            String url = storageService.uploadFile(imagenFile, "servicios");
            servicio.setImagenUrl(url);
        }

        servicioRepository.save(servicio);
        return "redirect:/panel/servicios";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Servicio> servicio = servicioRepository.findById(id);
        if (servicio.isPresent()) {
            model.addAttribute("servicio", servicio.get());
            model.addAttribute("modo", "editar");
            return "servicios-form";
        }
        return "redirect:/panel/servicios";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Servicio servicio,
                             @RequestParam("imagenFile") MultipartFile imagenFile,
                             HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Servicio> existente = servicioRepository.findById(id);
        if (existente.isPresent()) {
            Servicio s = existente.get();
            s.setTitulo(servicio.getTitulo());
            s.setDescripcion(servicio.getDescripcion());

            if (!imagenFile.isEmpty()) {
                if (s.getImagenUrl() != null) {
                    storageService.deleteFile(s.getImagenUrl());
                }
                String url = storageService.uploadFile(imagenFile, "servicios");
                s.setImagenUrl(url);
            }

            servicioRepository.save(s);
        }
        return "redirect:/panel/servicios";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long id, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Servicio> servicio = servicioRepository.findById(id);
        servicio.ifPresent(s -> {
            if (s.getImagenUrl() != null) {
                storageService.deleteFile(s.getImagenUrl());
            }
            servicioRepository.delete(s);
        });

        return "redirect:/panel/servicios";
    }
}