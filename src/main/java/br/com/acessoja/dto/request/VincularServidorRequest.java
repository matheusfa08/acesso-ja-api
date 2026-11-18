package br.com.acessoja.dto.request;

/**
 * Corpo de PUT /moderacao/usuarios/{id}/servidor. O moderador liga o usuário a um órgão (RN07).
 */
public record VincularServidorRequest(Integer orgaoId) {
}
