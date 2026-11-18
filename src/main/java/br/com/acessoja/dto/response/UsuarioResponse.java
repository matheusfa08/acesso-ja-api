package br.com.acessoja.dto.response;

import br.com.acessoja.enums.EnumPerfil;
import java.time.LocalDateTime;

/**
 * Resposta de GET /usuarios/me e dentro do login. Nunca inclui a senha.
 */
public record UsuarioResponse(
        int id,
        String nome,
        String email,
        EnumPerfil perfil,
        LocalDateTime dataCadastro,
        /** Só preenchido para SERVIDOR. */
        OrgaoResumoResponse orgao
) {
}
