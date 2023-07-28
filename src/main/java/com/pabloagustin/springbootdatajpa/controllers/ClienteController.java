package com.pabloagustin.springbootdatajpa.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.pabloagustin.springbootdatajpa.models.Cliente;
import com.pabloagustin.springbootdatajpa.service.IClienteService;
import com.pabloagustin.springbootdatajpa.service.IUploadFileService;
import com.pabloagustin.springbootdatajpa.util.PageRender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Controller
@SessionAttributes("cliente")
public class ClienteController {

	protected final Log logger = LogFactory.getLog(this.getClass());

	// Atributo del DAO para manejar las consultas JPA
	// En vez de INYECTAR DIRECTAMENTE AL DAO, inyectamos el SERVICE
	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;

	// Atributo para el cambio de idioma
	@Autowired
	private MessageSource messageSource;

	// Metodo PARA MOSTRAR el recurso IMAGEN para cada USUARIO - OTRA FORMA RESPECTO AL MVCCONFIG
	// En vez de crear el resource en mvcConfig, lo manejamos mediante un metodo con una http request
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}


	// Metodo para ver la imagen de cada usuario
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash){
		// Obtener el cliente a traves del service
		Cliente cliente = clienteService.findOne(id);
		if(cliente == null){
			flash.addAttribute("error", "El cliente no existe en la BD!");
			return "redirect:/listar";
		}

		// Pasamos el cliente a la vista
		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente: " + cliente.getNombre());
		return "ver";
	}

	// Listar metodo pero para trabajo 100% REST
	// EN VEZ de retornar un simple tipo de dato en JAVA, retornaremos un OBJETO SIMPLE DE JAVA o un ENTITY c/ getters,
	// setters, atributos, etc. Que queramos desplegar sus atributos en formato JSON o XML (Rest)
	@GetMapping("/listar-rest")
	@ResponseBody // IMPORTANTE, para la respuesta del tipo REST es necesaria esta ANOTACION
	public List<Cliente> listarRest(){

		return clienteService.findAll();
	}

	@GetMapping({"/listar", "/"})
	// Agregamos la paginacion con @RequestParam
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
	                     Authentication authentication,
	                     HttpServletRequest request,
	                     Locale locale){

		if(authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth != null) {
			logger.info("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): Usuario autenticado: ".concat(auth.getName()));
		}

		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");

		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}


		// Agregamos la paginacion
		Pageable pageRequest = PageRequest.of(page, 5);
		// Invocamos al service findAll pero de la paginacion
		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		// PageRender para el renderizado del paginador
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		// Lo pasamos a la vista
		model.addAttribute("page", pageRender);
		model.addAttribute("clientes", clientes);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));

		return "listar";
	}

	@GetMapping("/form")
	public String crear(Map<String, Object> model){
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}

	@PostMapping("/form")
	@Transactional
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model
			, @RequestParam("file") MultipartFile foto
			, SessionStatus status) throws IOException {

		// Mostrar los mensajes al usuario sobre los errores y devolver la vista si hay errores
		if (result.hasErrors()){
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}

		// Para iterar sobre el atributo 'foto' y subirla.
		if(!foto.isEmpty()){

			// Usuario existente en la base de datos y quiere reemplazar su foto
			if(cliente.getId() != null
					&& cliente.getId() > 0
					&& cliente.getFoto() != null
					&& cliente.getFoto().length() > 0){
				uploadFileService.delete(cliente.getFoto());
			}

			String uniqueFilename = uploadFileService.copy(foto);

			// Pasamos el nombre de la foto al cliente
			cliente.setFoto(uniqueFilename);

		}

		clienteService.save(cliente);
		status.setComplete();
		return "redirect:listar";
	}

	// EDITAR datos del cliente
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model){
		Cliente cliente = null;
		if(id > 0){
			cliente = clienteService.findOne(id);
		} else {
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}

	// ELIMINAR cliente
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash){
		if(id > 0){
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);

			// Eliminamos la imagen

				if(uploadFileService.delete(cliente.getFoto())){
					flash.addAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito");
				}
		}
		return "redirect:/listar";
	}

	// Obteniendo programaticamente el role del usuario
	private boolean hasRole(String role){

		SecurityContext context = SecurityContextHolder.getContext();

		if(context == null){
			return false;
		}

		Authentication auth = context.getAuthentication();

		if(auth == null){
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));
	}

}
