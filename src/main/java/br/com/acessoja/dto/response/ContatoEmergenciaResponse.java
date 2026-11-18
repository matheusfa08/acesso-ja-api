package br.com.acessoja.dto.response;

/**
 * Resposta de GET /emergencia/contatos (HU05, RF13).
 */
public record ContatoEmergenciaResponse(String numero, String nome) {
}
