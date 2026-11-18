package br.com.acessoja.dao;

import br.com.acessoja.entity.Orgao;
import br.com.acessoja.enums.EnumEsfera;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela orgao (somente leitura: a API não cadastra órgãos).
 * Mesmo padrão do projeto Soulpass: cada método abre sua conexão pelo {@link ConnectionFactory}.
 */
public class OrgaoDAO {

    private static final String COLUNAS = "id_orgao, nome_orgao, sigla_orgao, esfera, email_contato";

    /** Lista todos os órgãos, em ordem alfabética. */
    public List<Orgao> listarOrgaos() {
        String sql = "SELECT " + COLUNAS + " FROM orgao ORDER BY nome_orgao";
        List<Orgao> lista = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    /** @return o órgão, ou {@code null} se não existir. */
    public Orgao buscarOrgaoPorId(int id) {
        String sql = "SELECT " + COLUNAS + " FROM orgao WHERE id_orgao = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Transforma a linha atual do ResultSet em um Orgao. */
    private Orgao mapear(ResultSet rs) throws SQLException {
        Orgao orgao = new Orgao();
        orgao.setIdOrgao(rs.getInt("id_orgao"));
        orgao.setNomeOrgao(rs.getString("nome_orgao"));
        orgao.setSiglaOrgao(rs.getString("sigla_orgao"));
        orgao.setEsfera(EnumEsfera.valueOf(rs.getString("esfera")));
        orgao.setEmailContato(rs.getString("email_contato"));
        return orgao;
    }
}
