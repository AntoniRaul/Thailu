package com.thailu.thailubackend.controller;

import com.thailu.thailubackend.model.Producto;
import com.thailu.thailubackend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        return "catalogo";
    }
}