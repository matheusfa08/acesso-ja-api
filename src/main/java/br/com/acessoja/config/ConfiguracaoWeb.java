package br.com.acessoja.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS: libera o front (Live Server, site publicado) a chamar a API de outra origem.
 * As origens vêm de acessoja.cors.origens no application.properties (separadas por vírgula).
 */
@Configuration
public class ConfiguracaoWeb implements WebMvcConfigurer {

    private final String[] origens;

    public ConfiguracaoWeb(@Value("${acessoja.cors.origens}") String origens) {
        this.origens = Arrays.stream(origens.split(","))
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origens)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*"); // inclui o cabeçalho X-Usuario-Id
    }
}
