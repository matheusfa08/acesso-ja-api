package br.com.acessoja.dao;

import br.com.acessoja.entity.Aviso;
import br.com.acessoja.enums.EnumStatusAviso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela aviso (a mais importante do sistema).
 * As consultas do board juntam aviso com categoria para esconder avisos removidos e expirados.
 */
public class AvisoDAO {

    /** Colunas lidas em toda consulta (prefixo a. = tabela aviso). */
    private static final String COLUNAS =
            "a.id_aviso, a.descricao, a.endereco, a.bairro, a.foto_url, a.status_atual, "
                    + "a.data_registro, a.removido, a.id_usuario, a.id_categoria";

    // ------------------------------------------------------------------
    // Escrita
    // ------------------------------------------------------------------

    /**
     * Insere o aviso e devolve o id gerado pela sequence seq_aviso.
     * @param aviso status inicial, data, autor e categoria já preenchidos pelo service.
     */
    public int registrarAviso(Aviso aviso) {
        String sql = "INSERT INTO aviso (id_aviso, descricao, endereco, bairro, foto_url, status_atual, "
                + "data_registro, removido, id_usuario, id_categoria) "
                + "VALUES (seq_aviso.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_AVISO"})) {
            ps.setString(1, aviso.getDescricao());
            ps.setString(2, aviso.getEndereco());
            ps.setString(3, aviso.getBairro());
            ps.setString(4, aviso.getFotoUrl());
            ps.setString(5, aviso.getStatusAtual().name());
            ps.setTimestamp(6, Timestamp.valueOf(aviso.getDataRegistro()));
            ps.setString(7, aviso.isRemovido() ? "S" : "N");
            ps.setInt(8, aviso.getIdUsuario());
            ps.setInt(9, aviso.getIdCategoria());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Muda o status do aviso E grava a linha na linha do tempo (historico_status) na mesma transação:
     * ou os dois comandos funcionam, ou nenhum é gravado.
     */
    public void atualizarStatusComHistorico(int idAviso, EnumStatusAviso novoStatus, int idUsuarioServidor) {
        String sqlAviso = "UPDATE aviso SET status_atual = ? WHERE id_aviso = ?";
        String sqlHistorico = "INSERT INTO historico_status (id_historico, status_historico, data_hora, id_aviso, id_usuario) "
                + "VALUES (seq_historico.NEXTVAL, ?, ?, ?, ?)";
        Connection conexao = ConnectionFactory.obterConexao();
        try {
            conexao.setAutoCommit(false);
            try (PreparedStatement psAviso = conexao.prepareStatement(sqlAviso);
                 PreparedStatement psHistorico = conexao.prepareStatement(sqlHistorico)) {
                psAviso.setString(1, novoStatus.name());
                psAviso.setInt(2, idAviso);
                psAviso.executeUpdate();

                psHistorico.setString(1, novoStatus.name());
                psHistorico.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                psHistorico.setInt(3, idAviso);
                psHistorico.setInt(4, idUsuarioServidor);
                psHistorico.executeUpdate();
            }
            conexao.commit();
        } catch (SQLException e) {
            try {
                conexao.rollback();
            } catch (SQLException ignorado) {
                // sem ação: o erro original é o que importa
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ignorado) {
                // sem ação
            }
        }
    }

    /** RN07: redirecionamento para outra categoria (e, com ela, para outro órgão). */
    public void atualizarCategoria(int idAviso, int idCategoria) {
        executar("UPDATE aviso SET id_categoria = ? WHERE id_aviso = ?", idCategoria, idAviso);
    }

    /** Remoção lógica feita pelo moderador (RF16): o registro continua no banco. */
    public void marcarRemovido(int idAviso) {
        executar("UPDATE aviso SET removido = 'S' WHERE id_aviso = ?", idAviso);
    }

    // ------------------------------------------------------------------
    // Leitura
    // ------------------------------------------------------------------

    /** Busca qualquer aviso, inclusive removido. @return o aviso, ou {@code null}. */
    public Aviso buscarAvisoPorId(int idAviso) {
        List<Aviso> lista = consultar("SELECT " + COLUNAS + " FROM aviso a WHERE a.id_aviso = ?", idAviso);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Só devolve se o aviso NÃO foi removido (é o que o público pode ver). @return o aviso, ou {@code null}. */
    public Aviso buscarAvisoNaoRemovido(int idAviso) {
        List<Aviso> lista = consultar(
                "SELECT " + COLUNAS + " FROM aviso a WHERE a.id_aviso = ? AND a.removido = 'N'", idAviso);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Lista os avisos que passam pelos filtros. A ordem aceita "recentes" (padrão) ou "confirmacoes". */
    public List<Aviso> listarAvisos(FiltroAviso filtro, String ordem) {
        List<Object> parametros = new ArrayList<>();
        String where = montarWhere(filtro, parametros);
        String sql = "SELECT " + COLUNAS + " FROM aviso a JOIN categoria c ON c.id_categoria = a.id_categoria"
                + where + " ORDER BY " + montarOrdem(ordem);
        return consultar(sql, parametros.toArray());
    }

    /** Totais para a tela "Como funciona": {total, emAberto, resolvidos}. Não conta avisos removidos. */
    public long[] estatisticas() {
        String sql = "SELECT COUNT(*), "
                + "NVL(SUM(CASE WHEN status_atual <> 'RESOLVIDA' THEN 1 ELSE 0 END), 0), "
                + "NVL(SUM(CASE WHEN status_atual = 'RESOLVIDA' THEN 1 ELSE 0 END), 0) "
                + "FROM aviso WHERE removido = 'N'";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return new long[]{rs.getLong(1), rs.getLong(2), rs.getLong(3)};
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** RN05: quantos avisos o usuário registrou desde a data/hora informada (início do dia). */
    public int contarAvisosDoUsuarioDesde(int idUsuario, LocalDateTime desde) {
        String sql = "SELECT COUNT(*) FROM aviso WHERE id_usuario = ? AND data_registro >= ?";
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RN11: procura um aviso igual (mesma categoria e mesmo endereço) registrado depois de "desde".
     * Sem coordenadas no banco, "mesmo local" é o mesmo endereço, sem diferenciar maiúsculas.
     * @return o aviso existente, ou {@code null} se não houver duplicado.
     */
    public Aviso buscarAvisoDuplicado(int idCategoria, String endereco, LocalDateTime desde) {
        List<Aviso> lista = consultar("SELECT " + COLUNAS + " FROM aviso a "
                        + "WHERE a.id_categoria = ? AND UPPER(TRIM(a.endereco)) = UPPER(TRIM(?)) "
                        + "AND a.data_registro >= ? AND a.removido = 'N' "
                        + "ORDER BY a.data_registro DESC FETCH FIRST 1 ROWS ONLY",
                idCategoria, endereco, desde);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // ------------------------------------------------------------------
    // Auxiliares (privados)
    // ------------------------------------------------------------------

    /** Monta o WHERE e acrescenta os valores na lista, na mesma ordem dos "?". */
    private String montarWhere(FiltroAviso f, List<Object> parametros) {
        StringBuilder w = new StringBuilder(" WHERE a.removido = 'N'");

        // RF19 / RN12: categoria com dias_expiracao expira sem nova confirmação.
        // Validade = (última confirmação, ou o registro se não houver) + dias_expiracao.
        if (!f.incluirExpirados()) {
            w.append(" AND (c.dias_expiracao IS NULL OR NVL((SELECT MAX(cf.data_confirmacao) FROM confirmacao cf "
                    + "WHERE cf.id_aviso = a.id_aviso), a.data_registro) + c.dias_expiracao >= SYSDATE)");
        }
        if (f.categoriaId() != null) {
            w.append(" AND a.id_categoria = ?");
            parametros.add(f.categoriaId());
        }
        if (f.status() != null) {
            w.append(" AND a.status_atual = ?");
            parametros.add(f.status().name());
        }
        if (f.bairro() != null && !f.bairro().isBlank()) {
            w.append(" AND UPPER(a.bairro) = UPPER(?)");
            parametros.add(f.bairro().trim());
        }
        if (f.idUsuario() != null) {
            w.append(" AND a.id_usuario = ?");
            parametros.add(f.idUsuario());
        }
        if (f.idOrgao() != null) {
            w.append(" AND c.id_orgao = ?");
            parametros.add(f.idOrgao());
        }
        return w.toString();
    }

    /** Só valores conhecidos entram no ORDER BY (nunca texto vindo do usuário). */
    private String montarOrdem(String ordem) {
        if ("confirmacoes".equalsIgnoreCase(ordem)) {
            return "(SELECT COUNT(*) FROM confirmacao cf2 WHERE cf2.id_aviso = a.id_aviso) DESC, a.data_registro DESC";
        }
        return "a.data_registro DESC"; // "recentes" (padrão)
    }

    /** SELECT que devolve vários avisos. Os parâmetros podem ser Integer, String ou LocalDateTime. */
    private List<Aviso> consultar(String sql, Object... parametros) {
        List<Aviso> lista = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Aviso a = new Aviso();
                    a.setIdAviso(rs.getInt("id_aviso"));
                    a.setDescricao(rs.getString("descricao"));
                    a.setEndereco(rs.getString("endereco"));
                    a.setBairro(rs.getString("bairro"));
                    a.setFotoUrl(rs.getString("foto_url"));
                    a.setStatusAtual(EnumStatusAviso.valueOf(rs.getString("status_atual")));
                    Timestamp registro = rs.getTimestamp("data_registro");
                    a.setDataRegistro(registro == null ? null : registro.toLocalDateTime());
                    a.setRemovido("S".equalsIgnoreCase(rs.getString("removido")));
                    a.setIdUsuario(rs.getInt("id_usuario"));
                    a.setIdCategoria(rs.getInt("id_categoria"));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    /** UPDATE simples com parâmetros inteiros. */
    private void executar(String sql, int... parametros) {
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setInt(i + 1, parametros[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Preenche os "?" convertendo LocalDateTime em Timestamp. */
    private void preencher(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object p = parametros[i];
            if (p instanceof LocalDateTime dh) {
                ps.setTimestamp(i + 1, Timestamp.valueOf(dh));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }
}
