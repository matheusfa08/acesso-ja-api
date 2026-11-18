package br.com.acessoja.dto.response;

/**
 * Versão curta de um usuário (id e nome). Só vai para quem pode ver a identidade (RN08).
 */
public record UsuarioResumoResponse(int id, String nome) {
}
