package br.com.acessoja.services;

import br.com.acessoja.dao.HistoricoStatusDAO;
import br.com.acessoja.dao.OrgaoDAO;
import br.com.acessoja.dao.UsuarioDAO;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.enums.EnumPerfil;
import br.com.acessoja.contexto.ContextoUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

/**
 * Regras de negócio de usuário: cadastro, perfil, exclusão da conta e promoção a servidor.
 */
@Service
public class UsuarioService {

    /** Tamanho mínimo da senha no cadastro. */
    public static final int SENHA_MINIMA = 8;

    // Tamanhos máximos. Ajuste se as colunas do banco forem diferentes das que o script criou.
    private static final int MAX_NOME = 100;
    private static final int MAX_EMAIL = 100;

    /** Formato simples de e-mail (algo@algo.algo). Troque por uma regra mais forte se precisar. */
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /**
     * POST /auth/cadastro. A entidade vem do mapper (sempre CIDADAO);
     * aqui validamos tudo, guardamos a senha e gravamos.
     */
    public Usuario cadastrar(Usuario usuario, String senha, String confirmarSenha) {
        Validacao.texto(usuario.getNomeUsuario(), "nome", MAX_NOME);
        String email = Validacao.texto(usuario.getEmailUsuario(), "email", MAX_EMAIL);
        Validacao.exigir(EMAIL.matcher(email).matches(), "Informe um e-mail válido");
        Validacao.exigir(senha != null && senha.length() >= SENHA_MINIMA,
                "A senha precisa ter pelo menos " + SENHA_MINIMA + " caracteres");
        Validacao.exigir(senha.equals(confirmarSenha), "As senhas não são iguais");

        if (new UsuarioDAO().buscarUsuarioPorEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail");
        }

        usuario.setSenha(senha);
        usuario.setIdUsuario(new UsuarioDAO().cadastrarUsuario(usuario));
        return usuario;
    }

    /** Devolve o usuário ou responde 404. */
    public Usuario buscarPorId(int id) {
        Usuario usuario = new UsuarioDAO().buscarUsuarioPorId(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        return usuario;
    }

    /** GET /usuarios/me: o usuário identificado pelo cabeçalho X-Usuario-Id. */
    public Usuario buscarLogado() {
        return buscarPorId(ContextoUsuario.exigir().idUsuario());
    }

    /** Órgão do servidor (null para cidadão e moderador). */
    public Orgao buscarOrgaoDoUsuario(Usuario usuario) {
        return usuario.getIdOrgao() == null ? null : new OrgaoDAO().buscarOrgaoPorId(usuario.getIdOrgao());
    }

    /**
     * DELETE /usuarios/me (HU17, RF17).
     * O banco apaga em cascata os avisos, confirmações e sinalizações da pessoa.
     * Servidor que já atualizou algum status NÃO pode sair, porque historico_status não tem cascade.
     */
    public void excluirMinhaConta(String senha) {
        Usuario usuario = buscarLogado();

        if (senha == null || !senha.equals(usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senha incorreta");
        }
        if (new HistoricoStatusDAO().existeHistoricoDoUsuario(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta conta tem atualizações de status registradas e não pode ser excluída");
        }
        new UsuarioDAO().excluirUsuario(usuario.getIdUsuario());
    }

    /**
     * PUT /moderacao/usuarios/{id}/servidor.
     * Transforma um usuário em SERVIDOR de um órgão. O banco exige órgão para o perfil SERVIDOR.
     */
    public Usuario promoverServidor(int idUsuario, Integer idOrgao) {
        Validacao.obrigatorio(idOrgao, "orgaoId");
        Usuario usuario = buscarPorId(idUsuario);
        if (new OrgaoDAO().buscarOrgaoPorId(idOrgao) == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Órgão não encontrado");
        }
        // Moderador não vira servidor; servidor já vinculado pode trocar de órgão.
        Validacao.exigir(usuario.getPerfil() != EnumPerfil.MODERADOR, "Um moderador não pode virar servidor");

        new UsuarioDAO().promoverServidor(idUsuario, idOrgao);

        usuario.setPerfil(EnumPerfil.SERVIDOR);
        usuario.setIdOrgao(idOrgao);
        return usuario;
    }
}
