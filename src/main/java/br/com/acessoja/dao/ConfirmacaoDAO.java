package br.com.acessoja.dao;

import br.com.acessoja.entity.Confirmacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Acesso à tabela confirmacao (chave composta: id_aviso + id_usuario). */
public class ConfirmacaoDAO {

    /** Grava a confirmação ("Eu também vi"). */
    public void confirmarAviso(Confirmacao confirmacao) {
        String sql = "INSERT INTO confirmacao (id_aviso, id_usuario, data_confirmacao) VALUES (?, ?, ?)";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, confirmacao.getIdAviso());
            ps.setInt(2, confirmacao.getIdUsuario());
            ps.setTimestamp(3, Timestamp.valueOf(confirmacao.getDataConfirmacao()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** RN04: o usuário já confirmou este aviso? */
    public boolean existeConfirmacao(int idAviso, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM confirmacao WHERE id_aviso = ? AND id_usuario = ?";
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

    /** Quantas pessoas confirmaram o aviso. */
    public int contarConfirmacoes(int idAviso) {
        String sql = "SELECT COUNT(*) FROM confirmacao WHERE id_aviso = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idAviso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Data da confirmação mais recente (base da validade do aviso, RN12).
     * @return a data, ou {@code null} se ninguém confirmou ainda.
     */
    public LocalDateTime buscarUltimaConfirmacao(int idAviso) {
        String sql = "SELECT MAX(data_confirmacao) FROM confirmacao WHERE id_aviso = ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idAviso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp t = rs.getTimestamp(1);
                    return t == null ? null : t.toLocalDateTime();
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
