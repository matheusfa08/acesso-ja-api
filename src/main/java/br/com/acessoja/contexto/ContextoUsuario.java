package br.com.acessoja.contexto;

import br.com.acessoja.dao.UsuarioDAO;
import br.com.acessoja.entity.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Descobre qual usuário está chamando a API. O front envia o id do usuário (o que veio no login)
 * no cabeçalho "X-Usuario-Id"; perfil e órgão são lidos do banco a partir desse id.
 * Não há verificação de identidade nem de permissão.
 */
public final class ContextoUsuario {

    public static final String CABECALHO = "X-Usuario-Id";

    /** Guarda o usuário na própria requisição para não ir ao banco mais de uma vez. */
    private static final String ATRIBUTO = ContextoUsuario.class.getName() + ".SESSAO";

    private ContextoUsuario() {
    }

    /** Usuário identificado, ou vazio (cabeçalho ausente, inválido ou de um usuário que não existe). */
    public static Optional<SessaoUsuario> atual() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos)) {
            return Optional.empty();
        }
        HttpServletRequest request = atributos.getRequest();

        if (request.getAttribute(ATRIBUTO) instanceof SessaoUsuario guardada) {
            return Optional.of(guardada);
        }

        String cabecalho = request.getHeader(CABECALHO);
        if (cabecalho == null || cabecalho.isBlank()) {
            return Optional.empty();
        }
        int id;
        try {
            id = Integer.parseInt(cabecalho.trim());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        Usuario usuario = new UsuarioDAO().buscarUsuarioPorId(id);
        if (usuario == null) {
            return Optional.empty();
        }
        SessaoUsuario sessao = new SessaoUsuario(usuario.getIdUsuario(), usuario.getPerfil(), usuario.getIdOrgao());
        request.setAttribute(ATRIBUTO, sessao);
        return Optional.of(sessao);
    }

    /** Usuário identificado ou 401. */
    public static SessaoUsuario exigir() {
        return atual().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Usuário não identificado. Envie o cabeçalho " + CABECALHO));
    }
}
