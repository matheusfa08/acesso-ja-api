package br.com.acessoja.dao;

import br.com.acessoja.entity.HistoricoStatus;
import br.com.acessoja.enums.EnumStatusAviso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela historico_status (a linha do tempo de cada aviso).
 * A GRAVAÇÃO acontece em {@link AvisoDAO#atualizarStatusComHistorico}, junto com a mudança do status.
 */
public class HistoricoStatusDAO {

    /** Linha do tempo do aviso, da mudança mais antiga para a mais nova. */
    public List<HistoricoStatus> listarHistoricoPorAviso(int idAviso) {
        String sql = "SELECT id_historico, status_historico, data_hora, id_aviso, id_usuario "
                + "FROM historico_status WHERE id_aviso = ? ORDER BY data_hora, id_historico";
        List<HistoricoStatus> lista = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idAviso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistoricoStatus h = new HistoricoStatus();
                    h.setIdHistorico(rs.getInt("id_historico"));
                    h.setStatusHistorico(EnumStatusAviso.valueOf(rs.getString("status_historico")));
                    Timestamp dataHora = rs.getTimestamp("data_hora");
                    h.setDataHora(dataHora == null ? null : dataHora.toLocalDateTime());
                    h.setIdAviso(rs.getInt("id_aviso"));
                    h.setIdUsuario(rs.getInt("id_usuario"));
                    lista.add(h);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    /**
     * O usuário (servidor) já mexeu em algum aviso? A chave estrangeira id_usuario NÃO tem cascade,
     * então um servidor com histórico não pode ser excluído (RF17).
     */
    public boolean existeHistoricoDoUsuario(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM historico_status WHERE id_usuario = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
