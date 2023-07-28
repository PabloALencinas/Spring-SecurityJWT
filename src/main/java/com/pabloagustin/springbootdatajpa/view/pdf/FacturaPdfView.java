package com.pabloagustin.springbootdatajpa.view.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pabloagustin.springbootdatajpa.models.Factura;
import com.pabloagustin.springbootdatajpa.models.ItemFactura;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import java.awt.*;
import java.util.Locale;
import java.util.Map;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView{

	// Para la traduccion de idiomas para el pdf de factura
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private LocaleResolver localeResolver;

	// Sobreescribimos el metodo del AbstractPdfView para poder renderizar nuestro pdf para facturas
	@Override
	protected void buildPdfDocument(Map<String, Object> model,
	                                Document document,
	                                PdfWriter writer,
	                                HttpServletRequest request,
	                                HttpServletResponse response) throws Exception {

		// Capturamos los parametros que nos esten enviando
		Factura factura = (Factura) model.get("factura");

		// LocaleResolver para la traduccion
		Locale locale = localeResolver.resolveLocale(request);

		// Usamos otra forma de traduccion con el MessageSourceAccessor de la superclase
		// Que realiza por debajo los pasos hecho a traves del MessageSource y el LocaleResolver
		MessageSourceAccessor mensajes = getMessageSourceAccessor();

		// Partimos por la tabla de 'detalle' de factura
		PdfPTable tabla = new PdfPTable(1);
		tabla.setSpacingAfter(20);

		PdfPCell cell = null;

		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente", null, locale)));
		cell.setBackgroundColor(new Color(184, 218, 255));
		cell.setPadding(8f);
		tabla.addCell(cell);

		tabla.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		tabla.addCell(factura.getCliente().getEmail());

		// Tabla Dos para los datos de las Facturas
		PdfPTable tablaDos = new PdfPTable(1);
		tablaDos.setSpacingAfter(20);

		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura", null, locale)));
		cell.setBackgroundColor(new Color(195, 230, 203));
		cell.setPadding(8f);

		tablaDos.addCell(cell);
		// Traduciremos con la forma del MessageSourceAccessor
		tablaDos.addCell(mensajes.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
		tablaDos.addCell(mensajes.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
		tablaDos.addCell(mensajes.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());

		// Guardamos las tablas en el documento
		document.add(tabla);
		document.add(tablaDos);

		// Tabla Tres para detalle de factura
		PdfPTable tablaTres = new PdfPTable(4);
		tablaTres.setSpacingAfter(20);

		tablaTres.setWidths(new float [] {3.5f, 1, 1, 1});

		tablaTres.addCell(mensajes.getMessage("text.factura.form.item.nombre"));
		tablaTres.addCell(mensajes.getMessage("text.factura.form.item.precio"));
		tablaTres.addCell(mensajes.getMessage("text.factura.form.item.cantidad"));
		tablaTres.addCell(mensajes.getMessage("text.factura.form.item.total"));

		// For para crear las lineas por cada detalle
		for(ItemFactura item: factura.getItems()){
			tablaTres.addCell(item.getProducto().getNombre());
			tablaTres.addCell(item.getProducto().getPrecio().toString());

			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

			tablaTres.addCell(cell);
			tablaTres.addCell(item.calcularImporte().toString());
		}

		// Footer para el calculo del Gran Total
		cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total")+ ": "));
		// Ocupa 3 espacios con setColspan. 3 COLUMNAS
		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		tablaTres.addCell(cell);
		// Total
		tablaTres.addCell(factura.getTotal().toString());

		// Guardamos la tabla 3 con el detalle
		document.add(tablaTres);
	}
}
