package com.pabloagustin.springbootdatajpa.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;

	// Constructor para el login segun nuestro SERVICE
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	// Metodo de intento de Autenticacion que vamos a sobreescribir para nuestra app
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		// Obtenemos los datos del usuario para la autenticacion
		String username = obtainUsername(request);
		String password = obtainPassword(request);

		if (username == null){
			username = "";
		}

		if (password == null){
			password = "";
		}

		// Debug para saber que valores estamos recibiendo en nuestra api rest
		if(username != null && password != null){
			logger.info("Username desde request paramater (form-data): " + username);
			logger.info("Username desde request paramater (form-data): " + password);
		}

		username = username.trim();

		// Contenedor para las credenciales
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		// Autenticamos nuestras credenciales con el authenticationManager
		return authenticationManager.authenticate(authToken);
	}
}
