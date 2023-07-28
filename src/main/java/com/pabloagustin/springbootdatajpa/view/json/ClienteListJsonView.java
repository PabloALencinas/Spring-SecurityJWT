package com.pabloagustin.springbootdatajpa.view.json;

import com.pabloagustin.springbootdatajpa.models.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Map;

@Component("listar.json")
public class ClienteListJsonView extends MappingJackson2JsonView {

	@Override
	protected Object filterModel(Map<String, Object> model) {

		// Igual que en XML no nos interesa serializar el 'titulo' y el 'page' de cliente
		// Por lo que lo vamos a filtrar a la hora de exportar a JSON

		model.remove("titulo");
		model.remove("page");

		// Para el filtro de clientes
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		model.remove("clientes");
		model.put("clientes", clientes.getContent());

		return super.filterModel(model);
	}
}
