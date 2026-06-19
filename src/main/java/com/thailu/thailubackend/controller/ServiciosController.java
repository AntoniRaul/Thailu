package com.thailu.thailubackend.controller;

import com.thailu.thailubackend.model.Servicio;
import com.thailu.thailubackend.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ServiciosController {

    @Autowired
    private ServicioRepository servicioRepository;

    @GetMapping("/servicios")
    public String servicios(Model model) {
        List<Servicio> servicios = servicioRepository.findAll();
        model.addAttribute("servicios", servicios);
        return "servicios";
    }
}