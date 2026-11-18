package br.com.acessoja.dao;

import br.com.acessoja.entity.OcorrenciaOficial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela ocorrencia_oficial (comunicados dos órgãos). */
public class OcorrenciaOficialDAO {

    /** Insere a ocorrência e devolve o id gerado pela sequence seq_ocorrencia. */
    public int publicarOcorrencia(OcorrenciaOficial ocorrencia) {
        String sql = "INSERT INTO ocorrencia_oficial (id_ocorrencia, titulo, descricao, data_publicacao, id_orgao) "
                + "VALUES (seq_ocorrencia.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_OCORRENCIA"})) {
            ps.setString(1, ocorrencia.getTitulo());
            ps.setString(2, ocorrencia.getDescricao());
            ps.setTimestamp(3, Timestamp.valueOf(ocorrencia.getDataPublicacao()));
            ps.setInt(4, ocorrencia.getIdOrgao());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lista as ocorrências, mais recentes primeiro.
     * @param idOrgao só deste órgão; {@code null} = de todos os órgãos.
     */
    public List<OcorrenciaOficial> listarOcorrencias(Integer idOrgao) {
        String sql = "SELECT id_ocorrencia, titulo, descricao, data_publicacao, id_orgao FROM ocorrencia_oficial"
                + (idOrgao != null ? " WHERE id_orgao = ?" : "")
                + " ORDER BY data_publicacao DESC, id_ocorrencia DESC";
        List<OcorrenciaOficial> lista = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (idOrgao != null) {
                ps.setInt(1, idOrgao);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OcorrenciaOficial o = new OcorrenciaOficial();
                    o.setIdOcorrencia(rs.getInt("id_ocorrencia"));
                    o.setTitulo(rs.getString("titulo"));
                    o.setDescricao(rs.getString("descricao"));
                    Timestamp data = rs.getTimestamp("data_publicacao");
                    o.setDataPublicacao(data == null ? null : data.toLocalDateTime());
                    o.setIdOrgao(rs.getInt("id_orgao"));
                    lista.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }
}
