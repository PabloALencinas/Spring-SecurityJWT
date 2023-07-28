package com.pabloagustin.springbootdatajpa.view.xml;

import com.pabloagustin.springbootdatajpa.models.Cliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import java.util.Map;

@Component("listar.xml")
public class ClienteListXmlView extends MarshallingView {

	// Generamos los constructores de la superclase del Jaxb2Marshaller (Inyeccion)
	// Como ya esta anotado con @Bean, ya esta inyectado al contenedor del spring
	@Autowired
	public ClienteListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
	                                       HttpServletRequest request,
	                                       HttpServletResponse response) throws Exception {

		// Eliminamos lo que no vamos a necesitar dentro del XML
		model.remove("titulo");
		model.remove("page");

		// Antes de eliminar, tenemos que obtener los clientes
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");

		model.remove("clientes");

		model.put("clienteList", new ClienteList(clientes.getContent()));

		super.renderMergedOutputModel(model, request, response);
	}
}
