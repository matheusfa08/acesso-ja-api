package br.com.acessoja.tests;

import br.com.acessoja.dao.CategoriaDAO;
import br.com.acessoja.entity.Categoria;

/**
 * Teste manual do CategoriaDAO: lista as categorias gravadas no banco (rode o main pela IDE).
 * Serve para ver se o script Acesso-ja.sql foi executado e se a conexão funciona.
 */
public class TestCategoriaDAO {

    public static void main(String[] args) {
        System.out.println("\n--- Categorias cadastradas ---");
        for (Categoria c : new CategoriaDAO().listarCategorias()) {
            System.out.println(c.getIdCategoria() + " | " + c.getNomeCategoria()
                    + " | expira em: " + (c.getDiasExpiracao() == null ? "nunca" : c.getDiasExpiracao() + " dias")
                    + " | órgão: " + c.getIdOrgao());
        }
    }
}
