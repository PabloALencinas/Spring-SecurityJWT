package com.pabloagustin.springbootdatajpa.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocaleController {

    // Metodo handler para manejo de request
    @GetMapping("/locale")
    public String locale(HttpServletRequest request){
        String ultimaUrl = request.getHeader("referer");

        return "redirect:".concat(ultimaUrl);
    }
}
