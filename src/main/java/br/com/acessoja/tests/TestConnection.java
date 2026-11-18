package br.com.acessoja.tests;

import br.com.acessoja.dao.ConnectionFactory;

import java.sql.Connection;

/**
 * Teste manual da conexão com o banco (mesmo padrão do projeto Soulpass).
 * Rode o main pela IDE para conferir se URL, usuário e senha da ConnectionFactory estão certos.
 */
public class TestConnection {

    public static void main(String[] args) {
        System.out.println("\nTestando conexão com servidor de DataBase");
        try (Connection conexao = ConnectionFactory.obterConexao()) {
            if (conexao != null) {
                System.out.println("\n✅ Conectado com sucesso!");
            } else {
                System.out.println("\n❎ Erro ao conectar!");
            }
        } catch (Exception e) {
            System.out.println("\n❎ Erro ao conectar: " + e.getMessage());
        }
    }
}
