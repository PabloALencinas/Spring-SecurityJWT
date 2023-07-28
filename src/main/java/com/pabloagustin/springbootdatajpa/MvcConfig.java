package com.pabloagustin.springbootdatajpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.file.Paths;
import java.rmi.registry.Registry;
import java.util.Locale;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Sobreescribimos el siguiente metodo para agregar RECURSOS a nuestro proyecto
    // Para el manejo de las fotos de los clientes fuera del proyecto
    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        // Registrar nuestra ruta como recurso estatico
        // Mapeamos este directorio de imagenes a una ruta URL
        String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourcePath);
    }
     */

    // Implementacion del metodo para registrar un controlador de vista
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/error_403").setViewName("error_403");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Implementacion para la configuracion del multilenguaje
    // LocaleResolver
    @Bean // Importante anotar con Bean para registrar dentro del contenedor de Spring
    public LocaleResolver localeResolver(){
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("es", "ES"));
        return localeResolver;
    }

    // Interceptor para cambiar el locale cada vez que se envia el parametro del lenguaje
    // Lo enviado en lang seria por ejemplo -> 'es', lo intecepta y lo cambia
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
        localeInterceptor.setParamName("lang");
        return localeInterceptor;
    }

    // Registramos el interceptor con la sobreescritura del siguiente metodo
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    // Conversion OBJETO a XML
    // Este es el CONVERSOR, es importante
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(new Class[]{com.pabloagustin.springbootdatajpa.view.xml.ClienteList.class});
        return marshaller;
    }
}
