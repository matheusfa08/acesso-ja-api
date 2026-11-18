package br.com.acessoja.services;

import br.com.acessoja.dao.OrgaoDAO;
import br.com.acessoja.dao.UsuarioDAO;
import br.com.acessoja.dto.request.LoginRequest;
import br.com.acessoja.dto.response.LoginResponse;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.mapper.UsuarioMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Login (HU02, HU03, RF02). O mesmo login serve para cidadão, servidor e moderador. */
@Service
public class AutenticacaoService {

    /** POST /auth/login: confere e-mail e senha e devolve o usuário. */
    public LoginResponse login(LoginRequest dto) {
        Validacao.exigir(dto.email() != null && !dto.email().isBlank() && dto.senha() != null && !dto.senha().isBlank(),
                "Informe e-mail e senha");

        // A mesma mensagem para "e-mail não existe" e "senha errada": não revela quais e-mails existem.
        Usuario usuario = new UsuarioDAO().buscarUsuarioPorEmail(dto.email().trim());
        if (usuario == null || !dto.senha().equals(usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos");
        }

        Orgao orgao = usuario.getIdOrgao() == null ? null : new OrgaoDAO().buscarOrgaoPorId(usuario.getIdOrgao());
        return new LoginResponse(UsuarioMapper.toResponse(usuario, orgao));
    }
}
