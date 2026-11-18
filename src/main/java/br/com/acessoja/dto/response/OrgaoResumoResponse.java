package br.com.acessoja.dto.response;

/**
 * Versão curta de um órgão, usada dentro de outras respostas.
 */
public record OrgaoResumoResponse(int id, String nome, String sigla) {
}
