package br.com.acessoja.mapper;

import br.com.acessoja.dto.request.CadastroRequest;
import br.com.acessoja.dto.response.UsuarioResponse;
import br.com.acessoja.dto.response.UsuarioResumoResponse;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.enums.EnumPerfil;

import java.time.LocalDateTime;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class UsuarioMapper {

    /**
     * Cadastro (POST /auth/cadastro) vira um usuário CIDADAO novo.
     * A senha NÃO é copiada aqui: o service preenche o campo senha.
     */
    public static Usuario toEntity(CadastroRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(dto.nome() == null ? null : dto.nome().trim());
        usuario.setEmailUsuario(dto.email() == null ? null : dto.email().trim().toLowerCase());
        usuario.setPerfil(EnumPerfil.CIDADAO);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setIdOrgao(null); // só SERVIDOR tem órgão (regra do banco)
        return usuario;
    }

    /** Só id e nome. Usado onde a identidade pode aparecer (órgão, moderador). */
    public static UsuarioResumoResponse toResumo(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResumoResponse(usuario.getIdUsuario(), usuario.getNomeUsuario());
    }

    /** Nunca expõe a senha. */
    public static UsuarioResponse toResponse(Usuario usuario, Orgao orgao) {
        return new UsuarioResponse(usuario.getIdUsuario(), usuario.getNomeUsuario(), usuario.getEmailUsuario(),
                usuario.getPerfil(), usuario.getDataCadastro(), OrgaoMapper.toResumo(orgao));
    }
}
