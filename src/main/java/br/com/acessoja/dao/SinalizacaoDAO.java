package br.com.acessoja.dao;

import br.com.acessoja.entity.Sinalizacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela sinalizacao (avisos marcados como falsos ou inadequados). */
public class SinalizacaoDAO {

    private static final String COLUNAS = "id_sinalizacao, motivo, data_sinalizacao, id_aviso, id_usuario";

    /** Insere a sinalização e devolve o id gerado pela sequence seq_sinalizacao. */
    public int registrarSinalizacao(Sinalizacao sinalizacao) {
        String sql = "INSERT INTO sinalizacao (id_sinalizacao, motivo, data_sinalizacao, id_aviso, id_usuario) "
                + "VALUES (seq_sinalizacao.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_SINALIZACAO"})) {
            ps.setString(1, sinalizacao.getMotivo());
            ps.setTimestamp(2, Timestamp.valueOf(sinalizacao.getDataSinalizacao()));
            ps.setInt(3, sinalizacao.getIdAviso());
            ps.setInt(4, sinalizacao.getIdUsuario());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** O banco tem UNIQUE (id_aviso, id_usuario): cada pessoa sinaliza um aviso uma vez. */
    public boolean existeSinalizacao(int idAviso, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM sinalizacao WHERE id_aviso = ? AND id_usuario = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idAviso);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return a sinalização, ou {@code null} se não existir. */
    public Sinalizacao buscarSinalizacaoPorId(int id) {
        List<Sinalizacao> lista = consultar("SELECT " + COLUNAS + " FROM sinalizacao WHERE id_sinalizacao = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Fila da moderação, da mais recente para a mais antiga. */
    public List<Sinalizacao> listarSinalizacoes() {
        return consultar("SELECT " + COLUNAS + " FROM sinalizacao ORDER BY data_sinalizacao DESC, id_sinalizacao DESC", null);
    }

    /** Moderador descarta a sinalização (sem fundamento). */
    public void excluirSinalizacao(int id) {
        String sql = "DELETE FROM sinalizacao WHERE id_sinalizacao = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** SELECT com no máximo um parâmetro inteiro. */
    private List<Sinalizacao> consultar(String sql, Integer parametro) {
        List<Sinalizacao> lista = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sinalizacao s = new Sinalizacao();
                    s.setIdSinalizacao(rs.getInt("id_sinalizacao"));
                    s.setMotivo(rs.getString("motivo"));
                    Timestamp data = rs.getTimestamp("data_sinalizacao");
                    s.setDataSinalizacao(data == null ? null : data.toLocalDateTime());
                    s.setIdAviso(rs.getInt("id_aviso"));
                    s.setIdUsuario(rs.getInt("id_usuario"));
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }
}
