package com.pabloagustin.springbootdatajpa.view.csv;

import com.pabloagustin.springbootdatajpa.models.Cliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.util.Map;

@Component("listar.csv")
public class ClienteCsvView extends AbstractView {


	// Constructor
	public ClienteCsvView() {
		setContentType("text/csv");
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
	                                       HttpServletRequest request,
	                                       HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=\"clientes.csv\"");
		response.setContentType(getContentType());

		// Obtenemos el listado de clientes
		model.get("clientes");
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");

		// beanWriter -> Toma un Beans, una clase entity por ejemplo con atributos, getters y setters y lo convierte en una linea
		// de un archivo plano
		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		// Campos que queremos incluir dentro del archivo plano
		String[] header = {"id", "nombre", "apellido", "email", "createAt"};
		// Con el beanWriter vamos a escribir una linea dentro del archivo para el header
		beanWriter.writeHeader(header);

		// For para iterar por clientes y lo guardamos en el archivo plano
		for (Cliente cliente: clientes){
			beanWriter.write(cliente, header);
		}
		// Cerramos el beanWriter
		beanWriter.close();


	}
}
