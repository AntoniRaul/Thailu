package com.thailu.thailubackend.controller;

import com.thailu.thailubackend.model.Producto;
import com.thailu.thailubackend.repository.ProductoRepository;
import com.thailu.thailubackend.service.SupabaseStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/panel/catalogo")
public class CatalogoAdminController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SupabaseStorageService storageService;

    private boolean noAutenticado(HttpSession session) {
        return session.getAttribute("usuarioLogueado") == null;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";
        model.addAttribute("productos", productoRepository.findAll());
        return "catalogo-admin";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";
        model.addAttribute("producto", new Producto());
        model.addAttribute("modo", "crear");
        return "catalogo-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        if (!imagenFile.isEmpty()) {
            String url = storageService.uploadFile(imagenFile, "catalogo");
            producto.setImagenUrl(url);
        }

        productoRepository.save(producto);
        return "redirect:/panel/catalogo";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            model.addAttribute("modo", "editar");
            return "catalogo-form";
        }
        return "redirect:/panel/catalogo";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Producto producto,
                             @RequestParam("imagenFile") MultipartFile imagenFile,
                             HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Producto> existente = productoRepository.findById(id);
        if (existente.isPresent()) {
            Producto p = existente.get();
            p.setNombre(producto.getNombre());
            p.setDescripcion(producto.getDescripcion());
            p.setPrecio(producto.getPrecio());
            p.setCategoria(producto.getCategoria());

            if (!imagenFile.isEmpty()) {
                // Eliminar imagen anterior si existe
                if (p.getImagenUrl() != null) {
                    storageService.deleteFile(p.getImagenUrl());
                }
                String url = storageService.uploadFile(imagenFile, "catalogo");
                p.setImagenUrl(url);
            }

            productoRepository.save(p);
        }
        return "redirect:/panel/catalogo";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long id, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Producto> producto = productoRepository.findById(id);
        producto.ifPresent(p -> {
            if (p.getImagenUrl() != null) {
                storageService.deleteFile(p.getImagenUrl());
            }
            productoRepository.delete(p);
        });

        return "redirect:/panel/catalogo";
    }
}