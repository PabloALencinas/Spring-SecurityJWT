package com.pabloagustin.springbootdatajpa.view.xml;

import com.pabloagustin.springbootdatajpa.models.Cliente;

import jakarta.xml.bind.annotation.XmlElement;
import  jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

// Indicamos que es la clase ROOT del XML
@XmlRootElement(name = "clientes")
public class ClienteList {

	// Listado de clientes que vamos a convertir a XML

	// Este es el XML element
	@XmlElement(name = "cliente")
	public List<Cliente> clientes;

	// Constructores
	public ClienteList(){}

	public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
}
