package br.com.acessoja.dao;

import br.com.acessoja.entity.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela categoria (somente leitura). */
public class CategoriaDAO {

    private static final String COLUNAS = "id_categoria, nome_categoria, dias_expiracao, id_orgao";

    public List<Categoria> listarCategorias() {
        return consultarLista("SELECT " + COLUNAS + " FROM categoria ORDER BY id_categoria", null);
    }

    /** Categorias que um órgão atende (tela de órgãos e lista do servidor). */
    public List<Categoria> listarCategoriasPorOrgao(int idOrgao) {
        return consultarLista("SELECT " + COLUNAS + " FROM categoria WHERE id_orgao = ? ORDER BY id_categoria", idOrgao);
    }

    /** @return a categoria, ou {@code null} se não existir. */
    public Categoria buscarCategoriaPorId(int id) {
        List<Categoria> lista = consultarLista("SELECT " + COLUNAS + " FROM categoria WHERE id_categoria = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Executa o SELECT (com no máximo um parâmetro inteiro) e devolve as linhas como Categoria. */
    private List<Categoria> consultarLista(String sql, Integer parametro) {
        List<Categoria> lista = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria c = new Categoria();
                    c.setIdCategoria(rs.getInt("id_categoria"));
                    c.setNomeCategoria(rs.getString("nome_categoria"));
                    int dias = rs.getInt("dias_expiracao");
                    c.setDiasExpiracao(rs.wasNull() ? null : dias); // nulo = nunca expira
                    c.setIdOrgao(rs.getInt("id_orgao"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }
}
