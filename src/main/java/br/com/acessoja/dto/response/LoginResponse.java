package br.com.acessoja.dto.response;

/**
 * Resposta de POST /auth/login. O front guarda usuario.id e o envia no cabeçalho "X-Usuario-Id"
 * nas chamadas que exigem usuário identificado.
 */
public record LoginResponse(UsuarioResponse usuario) {
}
