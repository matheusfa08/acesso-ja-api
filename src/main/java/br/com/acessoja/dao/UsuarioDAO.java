package br.com.acessoja.dao;

import br.com.acessoja.entity.Usuario;
import br.com.acessoja.enums.EnumPerfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Acesso à tabela usuario.
 * ATENÇÃO: usa a coluna senha e a sequence seq_usuario, que NÃO existem no script original.
 * Rode antes resources/sql/ajustes_sugeridos.sql.
 */
public class UsuarioDAO {

    private static final String COLUNAS =
            "id_usuario, nome_usuario, email_usuario, senha, perfil, data_cadastro, id_orgao";

    /**
     * Insere o usuário e devolve o id gerado pela sequence.
     * @param usuario dados já validados (nome, e-mail, senha, perfil e data de cadastro).
     */
    public int cadastrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (id_usuario, nome_usuario, email_usuario, senha, perfil, data_cadastro, id_orgao) "
                + "VALUES (seq_usuario.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_USUARIO"})) {
            ps.setString(1, usuario.getNomeUsuario());
            ps.setString(2, usuario.getEmailUsuario());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil().name());
            ps.setTimestamp(5, Timestamp.valueOf(usuario.getDataCadastro()));
            if (usuario.getIdOrgao() != null) {
                ps.setInt(6, usuario.getIdOrgao());
            } else {
                ps.setNull(6, Types.INTEGER); // só SERVIDOR tem órgão (regra do banco)
            }
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return o usuário, ou {@code null} se não existir. */
    public Usuario buscarUsuarioPorId(int id) {
        return buscarUm("SELECT " + COLUNAS + " FROM usuario WHERE id_usuario = ?", id);
    }

    /** Busca pelo e-mail, sem diferenciar maiúsculas. @return o usuário, ou {@code null}. */
    public Usuario buscarUsuarioPorEmail(String email) {
        return buscarUm("SELECT " + COLUNAS + " FROM usuario WHERE LOWER(email_usuario) = LOWER(?)", email);
    }

    /** Vira SERVIDOR de um órgão (a regra do banco exige id_orgao junto com o perfil SERVIDOR). */
    public void promoverServidor(int idUsuario, int idOrgao) {
        String sql = "UPDATE usuario SET perfil = 'SERVIDOR', id_orgao = ? WHERE id_usuario = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idOrgao);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Exclui o usuário. Avisos, confirmações e sinalizações dele caem em cascata (ON DELETE CASCADE). */
    public void excluirUsuario(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** SELECT de um único usuário; o parâmetro pode ser número (id) ou texto (e-mail). */
    private Usuario buscarUm(String sql, Object parametro) {
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNomeUsuario(rs.getString("nome_usuario"));
                u.setEmailUsuario(rs.getString("email_usuario"));
                u.setSenha(rs.getString("senha"));
                u.setPerfil(EnumPerfil.valueOf(rs.getString("perfil")));
                Timestamp cadastro = rs.getTimestamp("data_cadastro");
                u.setDataCadastro(cadastro == null ? null : cadastro.toLocalDateTime());
                int idOrgao = rs.getInt("id_orgao");
                u.setIdOrgao(rs.wasNull() ? null : idOrgao); // nulo para cidadão e moderador
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
