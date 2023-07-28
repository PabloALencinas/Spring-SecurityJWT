package com.pabloagustin.springbootdatajpa.controllers;

import com.pabloagustin.springbootdatajpa.models.Cliente;
import com.pabloagustin.springbootdatajpa.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

	// Inyectamos el service de clientes para manejar las peticiones
	@Autowired
	private IClienteService clienteService;

	@GetMapping("/listar")
	public List<Cliente> listar(){
		return clienteService.findAll();
	}
}
