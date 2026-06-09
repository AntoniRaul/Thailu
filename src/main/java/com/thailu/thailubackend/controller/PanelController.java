package com.thailu.thailubackend.controller;

import com.thailu.thailubackend.model.EstadoOrden;
import com.thailu.thailubackend.model.Orden;
import com.thailu.thailubackend.repository.OrdenRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class PanelController {

    @Autowired
    private OrdenRepository ordenRepository;

    // Verificar sesión
    private boolean noAutenticado(HttpSession session) {
        return session.getAttribute("usuarioLogueado") == null;
    }

    // Generar código automático: THL-001, THL-002...
    private String generarCodigo() {
        long total = ordenRepository.count();
        return String.format("THL-%03d", total + 1);
    }

    @GetMapping("/panel")
    public String panel(Model model, HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        List<Orden> ordenes = ordenRepository.findAll();
        model.addAttribute("ordenes", ordenes);
        model.addAttribute("estados", EstadoOrden.values());
        return "panel";
    }

    @PostMapping("/panel/nueva-orden")
    public String nuevaOrden(@RequestParam String nombreCliente,
                             @RequestParam String telefonoCliente,
                             @RequestParam String modeloBalanza,
                             HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Orden orden = new Orden();
        orden.setCodigoSeguimiento(generarCodigo());
        orden.setNombreCliente(nombreCliente);
        orden.setTelefonoCliente(telefonoCliente);
        orden.setModeloBalanza(modeloBalanza);
        orden.setEstado(EstadoOrden.EN_ESPERA);
        orden.setHoraEntrada(LocalDateTime.now());

        ordenRepository.save(orden);
        return "redirect:/panel";
    }

    @PostMapping("/panel/cambiar-estado")
    public String cambiarEstado(@RequestParam Long id,
                                @RequestParam EstadoOrden estado,
                                HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";

        Optional<Orden> orden = ordenRepository.findById(id);
        orden.ifPresent(o -> {
            o.setEstado(estado);
            if (estado == EstadoOrden.LISTO_PARA_RECOGER) {
                o.setHoraListo(LocalDateTime.now());
            }
            ordenRepository.save(o);
        });
        return "redirect:/panel";
    }

    @PostMapping("/panel/eliminar")
    public String eliminarOrden(@RequestParam Long id,
                                HttpSession session) {
        if (noAutenticado(session)) return "redirect:/login";
        ordenRepository.deleteById(id);
        return "redirect:/panel";
    }
}