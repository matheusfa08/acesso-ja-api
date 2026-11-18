package br.com.acessoja.dto.request;

/**
 * Corpo de PATCH /avisos/{id}/categoria (RN07).
 * A subprefeitura usa para mandar o aviso para a categoria (e o órgão) certa.
 */
public record RedirecionarAvisoRequest(Integer categoriaId) {
}
